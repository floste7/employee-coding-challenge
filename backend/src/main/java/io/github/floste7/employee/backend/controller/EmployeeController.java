package io.github.floste7.employee.backend.controller;

import io.github.floste7.employee.backend.service.EmployeeService;
import io.github.floste7.employee.common.EmployeeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @Operation(summary = "Returns all employees")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees(HttpServletRequest request) {
        log.debug("Serving request at endpoint {}", request.getRequestURI());

        return new ResponseEntity<>(employeeService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Returns an employee with a given id")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable("employeeId") String employeeId,
                                                   HttpServletRequest request) {
        log.debug("Serving request at endpoint {}", request.getRequestURI());

        return new ResponseEntity<>(employeeService.get(employeeId), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create an employee", security = @SecurityRequirement(name = "security_auth"))
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto,
                                                      HttpServletRequest request) {
        log.debug("Serving request at endpoint {}", request.getRequestURI());

        return new ResponseEntity<>(employeeService.create(employeeDto), HttpStatus.CREATED);
    }

    @PutMapping
    @Operation(summary = "Update an employee", security = @SecurityRequirement(name = "security_auth"))
    public ResponseEntity<EmployeeDto> updateEmployee(@Valid @RequestBody EmployeeDto employeeDto,
                                                      HttpServletRequest request) {
        log.debug("Serving request at endpoint {}", request.getRequestURI());

        return new ResponseEntity<>(employeeService.update(employeeDto), HttpStatus.OK);
    }

    @DeleteMapping("/{employeeId}")
    @Operation(summary = "Delete an employee with a given id", security = @SecurityRequirement(name = "security_auth"))
    public ResponseEntity<Void> updateEmployee(@PathVariable("employeeId") String employeeId,
                                                      HttpServletRequest request) {
        log.debug("Serving request at endpoint {}", request.getRequestURI());

        employeeService.delete(employeeId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
