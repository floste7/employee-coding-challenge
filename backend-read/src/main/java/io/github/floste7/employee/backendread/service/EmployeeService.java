package io.github.floste7.employee.backendread.service;

import io.github.floste7.employee.common.EmployeeDto;

import java.util.List;

public interface EmployeeService {

    List<EmployeeDto> getAllEmployees();

    EmployeeDto getEmployeeById(String id);

}
