package io.github.floste7.employee.analytics.service;

import io.github.floste7.employee.common.EmployeeEvent;

import java.util.List;

public interface EmployeeService {

    List<EmployeeEvent> getAllEmployees();

    EmployeeEvent getEmployeeById(String id);

}
