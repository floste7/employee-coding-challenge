package io.github.floste7.employee.analytics.service;

import io.github.floste7.employee.analytics.exception.EntityNotFoundException;
import io.github.floste7.employee.analytics.processor.EmployeeEventProcessor;
import io.github.floste7.employee.common.EmployeeEvent;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultEmployeeService implements EmployeeService {

    private final InteractiveQueryService interactiveQueryService;

    public DefaultEmployeeService(InteractiveQueryService interactiveQueryService) {
        this.interactiveQueryService = interactiveQueryService;
    }

    @Override
    public List<EmployeeEvent> getAllEmployees() {
        ReadOnlyKeyValueStore<String, EmployeeEvent> employeeStore = interactiveQueryService
                .getQueryableStore(EmployeeEventProcessor.EMPLOYEE_STATE_STORE, QueryableStoreTypes.keyValueStore());

        List<EmployeeEvent> allEmployees = new ArrayList<>();
        KeyValueIterator<String, EmployeeEvent> employeeIterator = employeeStore.all();

        while (employeeIterator.hasNext()) {
            allEmployees.add(employeeIterator.next().value);
        }
        employeeIterator.close();

        return allEmployees;
    }

    @Override
    public EmployeeEvent getEmployeeById(String id) {
        ReadOnlyKeyValueStore<String, EmployeeEvent> employeeStore = interactiveQueryService
                .getQueryableStore(EmployeeEventProcessor.EMPLOYEE_STATE_STORE, QueryableStoreTypes.keyValueStore());

        EmployeeEvent employeeEvent = employeeStore.get(id);

        if(employeeEvent == null) {
            throw new EntityNotFoundException("Employee with id " + id + " does not exist");
        }

        return employeeEvent;
    }
}
