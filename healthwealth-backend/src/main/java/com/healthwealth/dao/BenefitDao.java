package com.healthwealth.dao;

import com.healthwealth.exception.InvalidInputException;
import com.healthwealth.model.GovernmentScheme;
import com.healthwealth.model.InsurancePolicy;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Repository
public class BenefitDao {
    private final DataSource ds;

    public BenefitDao(DataSource ds) {
        this.ds = ds;
    }

    public List<GovernmentScheme> eligibleSchemes(long patientId, long diseaseId) {
        String s = """
                SELECT DISTINCT gs.* FROM government_schemes gs
                JOIN scheme_diseases sd ON sd.scheme_id=gs.id
                JOIN patients p ON p.id=?
                WHERE sd.disease_id=? AND gs.active=TRUE
                  AND (gs.min_age IS NULL OR TIMESTAMPDIFF(YEAR,p.date_of_birth,CURRENT_DATE)>=gs.min_age)
                  AND (gs.max_age IS NULL OR TIMESTAMPDIFF(YEAR,p.date_of_birth,CURRENT_DATE)<=gs.max_age)
                  AND (gs.max_annual_income IS NULL OR p.annual_income<=gs.max_annual_income)
                ORDER BY gs.max_coverage DESC""";
        List<GovernmentScheme> out = new ArrayList<>();
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(s)) {
            ps.setLong(1, patientId);
            ps.setLong(2, diseaseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapScheme(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new InvalidInputException("Could not evaluate government schemes.");
        }
    }

    public List<InsurancePolicy> eligibleInsurance(long patientId, long diseaseId) {
        String s = """
                SELECT DISTINCT ip.* FROM insurance_policies ip
                JOIN patient_insurances pi ON pi.policy_id=ip.id
                JOIN policy_diseases pd ON pd.policy_id=ip.id
                WHERE pi.patient_id=? AND pd.disease_id=? AND pi.active=TRUE AND ip.active=TRUE AND pi.coverage_remaining>0
                ORDER BY ip.coverage_percent DESC""";
        List<InsurancePolicy> out = new ArrayList<>();
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(s)) {
            ps.setLong(1, patientId);
            ps.setLong(2, diseaseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapPolicy(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new InvalidInputException("Could not evaluate insurance coverage.");
        }
    }

    private GovernmentScheme mapScheme(ResultSet rs) throws SQLException {
        GovernmentScheme g = new GovernmentScheme();
        g.setId(rs.getLong("id"));
        g.setName(rs.getString("name"));
        g.setDescription(rs.getString("description"));
        g.setStateName(rs.getString("state_name"));
        g.setMinAge((Integer) rs.getObject("min_age"));
        g.setMaxAge((Integer) rs.getObject("max_age"));
        g.setMaxAnnualIncome(rs.getBigDecimal("max_annual_income"));
        g.setMaxCoverage(rs.getBigDecimal("max_coverage"));
        g.setActive(rs.getBoolean("active"));
        return g;
    }

    private InsurancePolicy mapPolicy(ResultSet rs) throws SQLException {
        InsurancePolicy i = new InsurancePolicy();
        i.setId(rs.getLong("id"));
        i.setProviderName(rs.getString("provider_name"));
        i.setPolicyName(rs.getString("policy_name"));
        i.setMaxCoverage(rs.getBigDecimal("max_coverage"));
        i.setCoveragePercent(rs.getBigDecimal("coverage_percent"));
        i.setActive(rs.getBoolean("active"));
        return i;
    }
}
