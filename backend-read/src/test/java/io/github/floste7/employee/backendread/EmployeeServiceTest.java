package io.github.floste7.employee.backendread;

import io.github.floste7.employee.backendread.service.DefaultEmployeeService;
import io.github.floste7.employee.backendread.service.EmployeeService;
import io.github.floste7.employee.common.EmployeeDto;
import io.github.floste7.employee.common.EmployeeEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class EmployeeServiceTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private InteractiveQueryService interactiveQueryService;

    @Test
    public void whenEmployeeCreatedEventReceived_queryStateStore_andReturnEmployee() throws InterruptedException {
        //Send event
        EmployeeEvent sampleEvent = getSampleEmployeeEvent(UUID.randomUUID().toString(), EmployeeEvent.Type.CREATED);
        KafkaTemplate<String, Object> kafkaTemplate = configureProducer();
        kafkaTemplate.send(EmployeeEvent.EMPLOYEE_EVENT_TOPIC, sampleEvent.getEmployee().getId(), sampleEvent);

        //Wait for kafka stream to switch from state PARTITIONS_ASSIGNED to RUNNING.
        //This is properly not the right way to do it....
        Thread.sleep(1000);

        //Query state store
        EmployeeService employeeService = new DefaultEmployeeService(interactiveQueryService);
        List<EmployeeDto> allEmployees = employeeService.getAllEmployees();

        assertEquals(1, allEmployees.size());
    }

    @Test
    public void whenEmployeeUpdatedEventReceived_queryStateStore_andReturnUpdatedEmployee() throws InterruptedException {
        KafkaTemplate<String, Object> kafkaTemplate = configureProducer();

        //Send employee created event
        EmployeeEvent sampleCreatedEvent = getSampleEmployeeEvent("employee-id-1", EmployeeEvent.Type.CREATED);
        kafkaTemplate.send(EmployeeEvent.EMPLOYEE_EVENT_TOPIC, "employee-id-1", sampleCreatedEvent);

        //Update employee and send employee updated event
        EmployeeEvent sampleUpdatedEvent = getSampleEmployeeEvent("employee-id-1", EmployeeEvent.Type.UPDATED);
        sampleUpdatedEvent.getEmployee().setEmail("updated-email@example.com");
        kafkaTemplate.send(EmployeeEvent.EMPLOYEE_EVENT_TOPIC, "employee-id-1", sampleUpdatedEvent);

        //Wait for kafka stream to switch from state PARTITIONS_ASSIGNED to RUNNING.
        //This is properly not the right way to do it....
        Thread.sleep(1000);

        //Query state store
        EmployeeService employeeService = new DefaultEmployeeService(interactiveQueryService);
        EmployeeDto employee = employeeService.getEmployeeById("employee-id-1");

        assertEquals("updated-email@example.com", employee.getEmail());
    }

    private KafkaTemplate<String, Object> configureProducer() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("testProducerGroup", "true",
                embeddedKafkaBroker);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        DefaultKafkaProducerFactory<String, Object> kafkaProducerFactory = new DefaultKafkaProducerFactory<>(props);

        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    private EmployeeEvent getSampleEmployeeEvent(String employeeId, EmployeeEvent.Type type) {
        return EmployeeEvent.builder()
                .type(type)
                .employee(EmployeeDto.builder()
                        .id(employeeId)
                        .fullName("Joe Doe")
                        .email("joe.doe@example.com")
                        .hobbies(Set.of("Hiking", "Roadtrips"))
                        .build())
                .build();
    }
}
