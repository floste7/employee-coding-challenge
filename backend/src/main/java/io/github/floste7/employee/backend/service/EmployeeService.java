package io.github.floste7.employee.backend.service;

import io.github.floste7.employee.common.EmployeeDto;

import java.util.List;

public interface EmployeeService {

    List<EmployeeDto> getAll();

    EmployeeDto get(String id);

    EmployeeDto create(EmployeeDto employeeDto);

    EmployeeDto update(EmployeeDto employeeDto);

    void delete(String id);
}
