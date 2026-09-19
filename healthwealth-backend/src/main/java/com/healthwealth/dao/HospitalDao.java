package com.healthwealth.dao;

import com.healthwealth.exception.InvalidInputException;
import com.healthwealth.exception.ResourceNotFoundException;
import com.healthwealth.model.Hospital;
import com.healthwealth.model.HospitalService;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Repository
public class HospitalDao {
    private final DataSource ds;

    public HospitalDao(DataSource ds) {
        this.ds = ds;
    }

    public long count(String city, Long diseaseId) {
        StringBuilder s = new StringBuilder("SELECT COUNT(DISTINCT h.id) FROM hospitals h ");
        if (diseaseId != null) s.append("JOIN hospital_services hs ON hs.hospital_id=h.id WHERE hs.disease_id=? ");
        else s.append("WHERE 1=1 ");
        if (city != null && !city.isBlank()) s.append("AND h.city=? ");
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(s.toString())) {
            int i = 1;
            if (diseaseId != null) ps.setLong(i++, diseaseId);
            if (city != null && !city.isBlank()) ps.setString(i++, city);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new InvalidInputException("Could not count hospitals.");
        }
    }

    public List<Hospital> findAll(int page, int size, String city, Long diseaseId) {
        if (page < 0 || size < 1 || size > 100) throw new InvalidInputException("page must be >=0 and size 1-100.");
        StringBuilder s = new StringBuilder("SELECT DISTINCT h.* FROM hospitals h ");
        if (diseaseId != null) s.append("JOIN hospital_services hs ON hs.hospital_id=h.id WHERE hs.disease_id=? ");
        else s.append("WHERE 1=1 ");
        if (city != null && !city.isBlank()) s.append("AND h.city=? ");
        s.append("ORDER BY h.rating DESC,h.id LIMIT ? OFFSET ?");
        List<Hospital> out = new ArrayList<>();
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(s.toString())) {
            int i = 1;
            if (diseaseId != null) ps.setLong(i++, diseaseId);
            if (city != null && !city.isBlank()) ps.setString(i++, city);
            ps.setInt(i++, size);
            ps.setInt(i, page * size);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new InvalidInputException("Could not read hospitals.");
        }
    }

    public List<HospitalService> servicesForDisease(long diseaseId) {
        String s = "SELECT * FROM hospital_services WHERE disease_id=? ORDER BY available_slots DESC,estimated_cost ASC";
        List<HospitalService> out = new ArrayList<>();
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(s)) {
            ps.setLong(1, diseaseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HospitalService h = new HospitalService();
                    h.setId(rs.getLong("id"));
                    h.setHospitalId(rs.getLong("hospital_id"));
                    h.setDiseaseId(rs.getLong("disease_id"));
                    h.setServiceName(rs.getString("service_name"));
                    h.setAvailableSlots(rs.getInt("available_slots"));
                    h.setEstimatedCost(rs.getBigDecimal("estimated_cost"));
                    out.add(h);
                }
            }
            return out;
        } catch (SQLException e) {
            throw new InvalidInputException("Could not read hospital services.");
        }
    }

    public Hospital findById(long id) {
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM hospitals WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new InvalidInputException("Could not read hospital.");
        }
        throw new ResourceNotFoundException("Hospital not found with id: " + id);
    }

    private Hospital map(ResultSet rs) throws SQLException {
        Hospital h = new Hospital();
        h.setId(rs.getLong("id"));
        h.setName(rs.getString("name"));
        h.setCity(rs.getString("city"));
        h.setAddress(rs.getString("address"));
        h.setPhone(rs.getString("phone"));
        h.setRating(rs.getBigDecimal("rating"));
        h.setEmergencyAvailable(rs.getBoolean("emergency_available"));
        return h;
    }
}
