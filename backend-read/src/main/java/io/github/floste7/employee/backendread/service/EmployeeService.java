package io.github.floste7.employee.backendread.service;

import io.github.floste7.employee.backendread.exception.EntityNotFoundException;
import io.github.floste7.employee.common.EmployeeDto;

import java.util.List;

/**
 * Represents a service that reads {@link EmployeeDto} from a data store
 */
public interface EmployeeService {

    /**
     * Returns a list of all {@link EmployeeDto}
     *
     * @return list of employees
     */
    List<EmployeeDto> getAllEmployees();

    /**
     * Returns an {@link EmployeeDto} with a provided id.
     *
     * @param id id of employee
     * @throws EntityNotFoundException if an {@link EmployeeDto} with the provided id does not exist.
     * @return employee
     */
    EmployeeDto getEmployeeById(String id);

}
