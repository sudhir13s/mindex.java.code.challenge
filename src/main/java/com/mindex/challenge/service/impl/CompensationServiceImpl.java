package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Compensation createOrUpdate(Compensation compensation) {
        LOG.debug("Creating or updating compensation [{}]", compensation);

        Compensation comp;
        try {
            comp = compensationRepository.findByEmployeeEmployeeId(compensation.getEmployee().getEmployeeId());
            if (comp == null) {
                compensationRepository.insert(compensation);
            } else {
                compensationRepository.save(compensation);
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR);
        }

        return compensation;
    }

    @Override
    public Compensation read(String employeeId) {
        LOG.debug("Reading compensation for employee id [{}]", employeeId);

        Compensation compensation;
        try {
            compensation = compensationRepository.findByEmployeeEmployeeId(employeeId);
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR);
        }

        if (compensation == null) {
            throw new ResponseStatusException(NOT_FOUND, "No compensation created for employeeId: " + employeeId);
        }

        return compensation;
    }
}
