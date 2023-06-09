package io.github.floste7.employee.backend.service.impl;

import io.github.floste7.employee.backend.converter.EmployeeConverter;
import io.github.floste7.employee.backend.model.Employee;
import io.github.floste7.employee.backend.repository.EmployeeRepository;
import io.github.floste7.employee.backend.service.EmployeeService;
import io.github.floste7.employee.common.EmployeeDto;
import io.github.floste7.employee.common.EmployeeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of an {@link EmployeeService} which performs the CRUD operations against an
 * {@link EmployeeRepository}. With each create, update or delete operation, a domain event is sent to kafka.
 */
@Slf4j
@Service
public class DefaultEmployeeService implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DefaultEmployeeService(EmployeeRepository employeeRepository,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        this.employeeRepository = employeeRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmployeeDto> getAll() {
        return EmployeeConverter.INSTANCE.toDtoList(employeeRepository.findAll());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmployeeDto get(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee with id '" + id + "' does not exist"));

        return EmployeeConverter.INSTANCE.toDto(employee);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmployeeDto create(EmployeeDto employeeDto) {
        employeeDto.setId(UUID.randomUUID().toString());
        Employee savedEmployee = employeeRepository.save(EmployeeConverter.INSTANCE.toModel(employeeDto));

        log.info("Employee with id {} created.", savedEmployee.getId());

        EmployeeDto savedEmployeeDto = EmployeeConverter.INSTANCE.toDto(savedEmployee);
        kafkaTemplate.send(EmployeeEvent.EMPLOYEE_EVENT_TOPIC, savedEmployee.getId(), EmployeeEvent.builder()
                .type(EmployeeEvent.Type.CREATED)
                .employee(savedEmployeeDto)
                .build());

        return savedEmployeeDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmployeeDto update(EmployeeDto employeeDto) {
        if(!employeeRepository.existsById(employeeDto.getId()))
            throw new EntityNotFoundException("Employee with id " + employeeDto.getId() + " does not exist");

        Employee updatedEmployee = employeeRepository.save(EmployeeConverter.INSTANCE.toModel(employeeDto));
        log.info("Employee with id {} updated", updatedEmployee.getId());

        EmployeeDto updatedEmployeeDto = EmployeeConverter.INSTANCE.toDto(updatedEmployee);
        kafkaTemplate.send(EmployeeEvent.EMPLOYEE_EVENT_TOPIC, updatedEmployeeDto.getId(), EmployeeEvent.builder()
                .type(EmployeeEvent.Type.UPDATED)
                .employee(updatedEmployeeDto)
                .build());

        return updatedEmployeeDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee with id " + id + " does not exist"));

        employeeRepository.delete(employee);
        log.info("Employee with id {} deleted", id);

        EmployeeDto deletedEmployee = EmployeeConverter.INSTANCE.toDto(employee);
        kafkaTemplate.send(EmployeeEvent.EMPLOYEE_EVENT_TOPIC, deletedEmployee.getId(), EmployeeEvent.builder()
                .type(EmployeeEvent.Type.DELETED)
                .employee(deletedEmployee)
                .build());
    }
}
