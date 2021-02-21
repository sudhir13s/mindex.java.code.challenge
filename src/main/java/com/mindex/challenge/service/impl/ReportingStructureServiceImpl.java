package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.*;


@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Received employee reporting structure read request for id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);
        if (employee == null)
            throw new ResponseStatusException(NOT_FOUND, "No Employee exist for employeeId: " + id);

        int totalReports = getTotalNumberOfReportees(employee);
        return new ReportingStructure(employee, totalReports);
    }

    public int getTotalNumberOfReportees(Employee employee) {
        LOG.debug("Fetching total reports for employee id [{}]", employee.getEmployeeId());

        // store total reports and just to ensure not to stuck in looping structure, use hashset.
        Set<Employee> employeeSet = new HashSet<>();
        Queue<Employee> employeeQueue = new ArrayDeque<>();
        employeeQueue.add(employee);

        while (!employeeQueue.isEmpty()) {
            Employee tempEmp = employeeQueue.poll();
            tempEmp = employeeRepository.findByEmployeeId(tempEmp.getEmployeeId());
            if (tempEmp == null)
                continue;  // skip invalid employee record.

            List<Employee> directReports = tempEmp.getDirectReports();
            if (directReports != null) {
                for (Employee reportee : directReports) {
                    if (!employeeSet.contains(reportee)) {
                        employeeQueue.offer(reportee);
                        employeeSet.add(reportee);
                    }
                }
            }
        }
        return employeeSet.size();
    }
}
