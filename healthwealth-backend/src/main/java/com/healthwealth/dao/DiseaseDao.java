package com.healthwealth.dao;

import com.healthwealth.exception.InvalidInputException;
import com.healthwealth.exception.ResourceNotFoundException;
import com.healthwealth.model.Disease;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Repository
public class DiseaseDao {
    private final DataSource dataSource;

    public DiseaseDao(DataSource ds) {
        dataSource = ds;
    }

    public List<Disease> findByPatientId(long patientId) {
        String sql = "SELECT d.* FROM diseases d JOIN patient_diseases pd ON pd.disease_id=d.id WHERE pd.patient_id=? ORDER BY d.name";
        List<Disease> out = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new InvalidInputException("Could not read patient diseases.");
        }
    }

    public Disease findById(long id) {
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM diseases WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new InvalidInputException("Could not read disease.");
        }
        throw new ResourceNotFoundException("Disease not found with id: " + id);
    }

    public void addToPatient(long patientId, long diseaseId) {
        String sql = "INSERT INTO patient_diseases(patient_id,disease_id,diagnosed_date,severity,status) VALUES(?,?,CURRENT_DATE,'UNKNOWN','ACTIVE')";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setLong(2, diseaseId);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new InvalidInputException("Patient/disease relation already exists or an ID is invalid.");
        } catch (SQLException e) {
            throw new InvalidInputException("Could not add disease to patient.");
        }
    }

    private Disease map(ResultSet rs) throws SQLException {
        Disease d = new Disease();
        d.setId(rs.getLong("id"));
        d.setName(rs.getString("name"));
        d.setCategory(rs.getString("category"));
        d.setDescription(rs.getString("description"));
        return d;
    }
}
