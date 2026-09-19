package com.healthwealth.dao;

import com.healthwealth.exception.InvalidInputException;
import com.healthwealth.exception.ResourceNotFoundException;
import com.healthwealth.model.Patient;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
public class PatientDao {
    private final DataSource dataSource;

    public PatientDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long count(String city) {
        String sql = (city == null || city.isBlank()) ? "SELECT COUNT(*) FROM patients" : "SELECT COUNT(*) FROM patients WHERE city = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (city != null && !city.isBlank()) ps.setString(1, city);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new InvalidInputException("Could not count patients.");
        }
    }

    public List<Patient> findAll(int page, int size, String city) {
        if (page < 0 || size < 1 || size > 100) throw new InvalidInputException("page must be >= 0 and size 1-100.");
        String sql = (city == null || city.isBlank())
                ? "SELECT * FROM patients ORDER BY id LIMIT ? OFFSET ?"
                : "SELECT * FROM patients WHERE city = ? ORDER BY id LIMIT ? OFFSET ?";
        List<Patient> out = new ArrayList<>();
        int offset = page * size;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            if (city != null && !city.isBlank()) ps.setString(i++, city);
            ps.setInt(i++, size);
            ps.setInt(i, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new InvalidInputException("Could not read patients.");
        }
    }

    public Optional<Patient> findById(long id) {
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM patients WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new InvalidInputException("Could not read patient.");
        }
    }

    public long insert(Patient p) {
        String sql = "INSERT INTO patients(identity_ref,full_name,date_of_birth,gender,city,annual_income,phone,blood_group,emergency_contact) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fill(ps, p);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) return k.getLong(1);
            }
            throw new InvalidInputException("Patient ID was not generated.");
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new InvalidInputException("Identity reference already exists.");
        } catch (SQLException e) {
            throw new InvalidInputException("Could not create patient.");
        }
    }

    public void update(long id, Patient p) {
        String sql = "UPDATE patients SET identity_ref=?,full_name=?,date_of_birth=?,gender=?,city=?,annual_income=?,phone=?,blood_group=?,emergency_contact=? WHERE id=?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            fill(ps, p);
            ps.setLong(10, id);
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Patient not found with id: " + id);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new InvalidInputException("Identity reference already exists.");
        } catch (SQLException e) {
            throw new InvalidInputException("Could not update patient.");
        }
    }

    public void delete(long id) {
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM patients WHERE id=?")) {
            ps.setLong(1, id);
            if (ps.executeUpdate() == 0) throw new ResourceNotFoundException("Patient not found with id: " + id);
        } catch (SQLException e) {
            throw new InvalidInputException("Could not delete patient.");
        }
    }

    private void fill(PreparedStatement ps, Patient p) throws SQLException {
        ps.setString(1, p.getIdentityRef());
        ps.setString(2, p.getFullName());
        ps.setDate(3, Date.valueOf(p.getDateOfBirth()));
        ps.setString(4, p.getGender());
        ps.setString(5, p.getCity());
        ps.setBigDecimal(6, p.getAnnualIncome());
        ps.setString(7, p.getPhone());
        ps.setString(8, p.getBloodGroup());
        ps.setString(9, p.getEmergencyContact());
    }

    private Patient map(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getLong("id"));
        p.setIdentityRef(rs.getString("identity_ref"));
        p.setFullName(rs.getString("full_name"));
        p.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        p.setGender(rs.getString("gender"));
        p.setCity(rs.getString("city"));
        p.setAnnualIncome(rs.getBigDecimal("annual_income"));
        p.setPhone(rs.getString("phone"));
        p.setBloodGroup(rs.getString("blood_group"));
        p.setEmergencyContact(rs.getString("emergency_contact"));
        return p;
    }
}
