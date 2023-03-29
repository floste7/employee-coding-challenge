package io.github.floste7.employee.analytics.controller;

import io.github.floste7.employee.analytics.service.EmployeeService;
import io.github.floste7.employee.common.EmployeeDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @Operation(summary = "Returns all employees")
    public ResponseEntity<List<EmployeeDto>> getAll() {
        return new ResponseEntity<>(employeeService.getAllEmployees(), HttpStatus.OK);
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Returns an employee with a given id")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("employeeId") String employeeId) {
        return new ResponseEntity<>(employeeService.getEmployeeById(employeeId), HttpStatus.OK);
    }
}
