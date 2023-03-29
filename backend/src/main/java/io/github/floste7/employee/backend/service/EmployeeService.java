package io.github.floste7.employee.backend.service;

import io.github.floste7.employee.common.EmployeeDto;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Represents a service that provides CRUD capabilities to the Employee entity
 */
public interface EmployeeService {

    /**
     * Returns a list of all {@link EmployeeDto}
     *
     * @return list of employees
     */
    List<EmployeeDto> getAll();

    /**
     * Returns an {@link EmployeeDto} with a provided id.
     *
     * @param id id of employee
     * @throws EntityNotFoundException if an {@link EmployeeDto} with the provided id does not exist.
     * @return employee
     */
    EmployeeDto get(String id);

    /**
     * Creates an employee
     *
     * @param employeeDto {@link EmployeeDto} to be created
     * @return created employee
     */
    EmployeeDto create(EmployeeDto employeeDto);

    /**
     * Updates an employee
     *
     * @param employeeDto {@link EmployeeDto} to be updated
     * @return updated employee
     * @throws EntityNotFoundException if the provided {@link EmployeeDto} does not exist.
     */
    EmployeeDto update(EmployeeDto employeeDto);

    /**
     * Deletes an employee with a provided id
     *
     * @param id id of employee
     * @throws EntityNotFoundException if an {@link EmployeeDto} with the provided id does not exist
     */
    void delete(String id);
}
