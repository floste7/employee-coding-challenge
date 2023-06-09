package io.github.floste7.employee.backendread.service;

import io.github.floste7.employee.backendread.exception.EntityNotFoundException;
import io.github.floste7.employee.backendread.processor.EmployeeEventProcessor;
import io.github.floste7.employee.common.EmployeeDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of an {@link EmployeeService} which uses Kafka's {@link InteractiveQueryService} to retrieve
 * {@link EmployeeDto}.
 */
@Slf4j
@Service
public class DefaultEmployeeService implements EmployeeService {

    private final InteractiveQueryService interactiveQueryService;

    public DefaultEmployeeService(InteractiveQueryService interactiveQueryService) {
        this.interactiveQueryService = interactiveQueryService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmployeeDto> getAllEmployees() {
        ReadOnlyKeyValueStore<String, EmployeeDto> employeeStateStore = interactiveQueryService
                .getQueryableStore(EmployeeEventProcessor.EMPLOYEE_STATE_STORE, QueryableStoreTypes.keyValueStore());

        List<EmployeeDto> allEmployees = new ArrayList<>();
        KeyValueIterator<String, EmployeeDto> employeeStateIterator = employeeStateStore.all();

        while (employeeStateIterator.hasNext()) {
            allEmployees.add(employeeStateIterator.next().value);
        }
        employeeStateIterator.close();

        return allEmployees;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmployeeDto getEmployeeById(String id) {
        ReadOnlyKeyValueStore<String, EmployeeDto> employeeStateStore = interactiveQueryService
                .getQueryableStore(EmployeeEventProcessor.EMPLOYEE_STATE_STORE, QueryableStoreTypes.keyValueStore());

        EmployeeDto employeeState = employeeStateStore.get(id);

        if(employeeState == null) {
            throw new EntityNotFoundException("Employee with id " + id + " does not exist");
        }

        return employeeState;
    }
}
