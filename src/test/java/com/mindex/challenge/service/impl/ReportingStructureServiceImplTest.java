package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String employeeUrl;
    private String reportingStructureEmployeeIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        reportingStructureEmployeeIdUrl = "http://localhost:" + port + "/reporting/{id}";
    }

    @Test
    public void testReportingStructureRead() {

        Employee testEmployee2 = new Employee();
        testEmployee2.setFirstName("Ringo");
        testEmployee2.setLastName("Starr");
        testEmployee2.setDepartment("Developer V");
        testEmployee2.setPosition("Engineering");

        Employee testEmployee1 = new Employee();
        testEmployee1.setFirstName("Paul");
        testEmployee1.setLastName("McCartney");
        testEmployee1.setDepartment("Developer I");
        testEmployee1.setPosition("Engineering");

        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");
        List<Employee> directs = new ArrayList<>();
        directs.add(testEmployee1);
        directs.add(testEmployee2);
        testEmployee.setDirectReports(directs);

        Employee createdEmployee2 = restTemplate.postForEntity(employeeUrl, testEmployee2, Employee.class).getBody();
        Employee createdEmployee1 = restTemplate.postForEntity(employeeUrl, testEmployee1, Employee.class).getBody();
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assert createdEmployee2 != null;
        assert createdEmployee1 != null;
        assert createdEmployee != null;
        assertNotNull(createdEmployee2.getEmployeeId());
        assertNotNull(createdEmployee1.getEmployeeId());
        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee2, createdEmployee2);
        assertEmployeeEquivalence(testEmployee1, createdEmployee1);
        assertEmployeeEquivalence(testEmployee, createdEmployee);

        // setting the ids
        testEmployee2.setEmployeeId(createdEmployee2.getEmployeeId());
        testEmployee1.setEmployeeId(createdEmployee1.getEmployeeId());
        testEmployee.setEmployeeId(createdEmployee.getEmployeeId());

        // zero reports
        ReportingStructure testReportStruct = new ReportingStructure();
        testReportStruct.setEmployee(testEmployee2);
        testReportStruct.setNumberOfReports(0);

        // Read checks
        ReportingStructure readReportStruct =
                restTemplate.getForEntity(reportingStructureEmployeeIdUrl, ReportingStructure.class,
                        createdEmployee2.getEmployeeId()).getBody();

        assert readReportStruct != null;
        assertEquals(testReportStruct.getEmployee().getEmployeeId(), readReportStruct.getEmployee().getEmployeeId());
        assertReportingStructureEquivalence(testReportStruct, readReportStruct);

        // two reports
        ReportingStructure testTwoReportStruct = new ReportingStructure();
        testTwoReportStruct.setEmployee(testEmployee);
        testTwoReportStruct.setNumberOfReports(2);

        // Read checks
        ReportingStructure readTwoReportStruct =
                restTemplate.getForEntity(reportingStructureEmployeeIdUrl, ReportingStructure.class,
                        createdEmployee.getEmployeeId()).getBody();

        assert readTwoReportStruct != null;
        assertEquals(testTwoReportStruct.getEmployee().getEmployeeId(), readTwoReportStruct.getEmployee().getEmployeeId());
        assertReportingStructureEquivalence(testTwoReportStruct, readTwoReportStruct);

    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private static void assertReportingStructureEquivalence(ReportingStructure expected, ReportingStructure actual) {
        assertEquals(expected.getEmployee().getEmployeeId(), actual.getEmployee().getEmployeeId());
        assertEquals(expected.getNumberOfReports(), actual.getNumberOfReports());
    }
}
