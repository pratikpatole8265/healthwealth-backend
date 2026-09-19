package com.healthwealth.service;

import com.healthwealth.dao.HospitalDao;
import com.healthwealth.dto.PagedResponse;
import org.springframework.stereotype.Service;

@Service
public class HospitalService {
    private final HospitalDao dao;

    public HospitalService(HospitalDao d) {
        dao = d;
    }

    public PagedResponse<com.healthwealth.model.Hospital> findAll(int page, int size, String city, Long diseaseId) {
        var c = dao.findAll(page, size, city, diseaseId);
        long t = dao.count(city, diseaseId);
        return new PagedResponse<>(c, page, size, (int) Math.ceil((double) t / size), t);
    }
}
