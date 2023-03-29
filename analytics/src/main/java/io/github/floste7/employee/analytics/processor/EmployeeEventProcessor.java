package io.github.floste7.employee.analytics.processor;

import io.github.floste7.employee.common.EmployeeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class EmployeeEventProcessor {

    public static final String EMPLOYEE_STATE_STORE = "employee-state-store";

    @Bean
    public Consumer<KStream<String, EmployeeEvent>> processEmployeeEvents() {
        return employeeEventStream -> employeeEventStream
                .peek((key, value) -> log.info("Employee event received (key={}, type={})", key, value.getType()))
                .toTable(Materialized.as(EMPLOYEE_STATE_STORE));
    }
}
