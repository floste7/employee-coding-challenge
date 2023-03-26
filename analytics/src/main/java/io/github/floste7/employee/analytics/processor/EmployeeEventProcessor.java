package io.github.floste7.employee.analytics.processor;

import io.github.floste7.employee.common.EmployeeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class EmployeeEventProcessor {

    public static final String EMPLOYEE_STATE_STORE = "employee-state-store";

    Predicate<String, EmployeeEvent> isCreateEvent = (k, v) -> v.getType() == EmployeeEvent.Type.CREATED;
    Predicate<String, EmployeeEvent> isUpdateEvent = (k, v) -> v.getType() == EmployeeEvent.Type.UPDATED;
    Predicate<String, EmployeeEvent> isDeletedEvent = (k, v) -> v.getType() == EmployeeEvent.Type.DELETED;

    @Bean
    public Consumer<KStream<String, EmployeeEvent>> processEmployeeEvents() {
        return employeeEventStream -> employeeEventStream
                .peek((key, value) -> log.info("Employee event recieved (key={}, type={})", key, value.getType()))
                .toTable(Materialized.as(EMPLOYEE_STATE_STORE));
    }
}
