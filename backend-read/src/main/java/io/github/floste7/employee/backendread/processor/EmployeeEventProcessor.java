package io.github.floste7.employee.backendread.processor;

import io.github.floste7.employee.common.EmployeeDto;
import io.github.floste7.employee.common.EmployeeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
public class EmployeeEventProcessor {

    public static final String EMPLOYEE_STATE_STORE = "employee-state-store";

    private final JsonSerde<EmployeeDto> employeeStateJsonSerde = new JsonSerde<>();

    public EmployeeEventProcessor() {
        Map<String, String> props = new HashMap<>();
        props.put("spring.json.value.default.type", "io.github.floste7.employee.common.EmployeeDto");
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, "false");

        employeeStateJsonSerde.configure(props, false);
    }

    @Bean
    public Consumer<KStream<String, EmployeeEvent>> processEmployeeEvents() {
        return employeeEventStream -> employeeEventStream
                .peek((key, value) -> log.info("Employee event received (key={}, type={})", key, value.getType()))
                .mapValues(EmployeeEvent::getEmployee)
                .toTable(Materialized.<String, EmployeeDto, KeyValueStore<Bytes, byte[]>>as(EMPLOYEE_STATE_STORE)
                        .withKeySerde(Serdes.String())
                        .withValueSerde(employeeStateJsonSerde));
    }
}
