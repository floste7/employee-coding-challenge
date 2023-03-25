package io.github.floste7.employee.backend.integration;

import io.github.floste7.employee.backend.config.KafkaConfig;
import io.github.floste7.employee.backend.event.EmployeeEvent;
import io.github.floste7.employee.backend.model.Employee;
import io.github.floste7.employee.backend.repository.EmployeeRepository;
import io.github.floste7.employee.backend.service.EmployeeService;
import io.github.floste7.employee.backend.service.impl.DefaultEmployeeService;
import io.github.floste7.employee.common.EmployeeDto;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.test.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
@Transactional
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class EmployeeServiceTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    Consumer<String, Object> consumer;

    @BeforeEach
    public void beforeEach() {
        consumer = configureConsumer();
    }

    @AfterEach
    public void afterEach() {
        employeeRepository.deleteAll();
        consumer.close();
    }

    @Test
    public void whenEmployeeCreated_saveEmployeeInDB_andSendKafkaMessage() {
        EmployeeService employeeService = new DefaultEmployeeService(employeeRepository, kafkaTemplate);

        EmployeeDto employeeDto = getSampleEmployee();
        EmployeeDto createdEmployee = employeeService.create(employeeDto);

        //Employee Service Assertion
        assertNotEquals("will-be-overwritten", createdEmployee.getId());

        //DB Assertions
        assertDoesNotThrow(() -> employeeRepository.findById(employeeDto.getId()).get());

        //Kafka Assertions
        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(consumer, KafkaConfig.EMPLOYEE_EVENT_TOPIC);
        assertEquals(createdEmployee.getId(), singleRecord.key());
        assertEquals(EmployeeEvent.Type.CREATED, ((EmployeeEvent) singleRecord.value()).getType());
        assertEquals(createdEmployee, ((EmployeeEvent) singleRecord.value()).getEmployee());

    }

    @Test
    public void whenEmployeeUpdated_updateEmployeeInDB_andSendKafkaMessage() {
        EmployeeService employeeService = new DefaultEmployeeService(employeeRepository, kafkaTemplate);

        EmployeeDto employeeDto = getSampleEmployee();
        EmployeeDto createdEmployee = employeeService.create(employeeDto);

        //Assert email address of initially created employee
        Employee employeeInDb = employeeRepository.findById(createdEmployee.getId()).get();
        assertEquals("john.doe@example.com", createdEmployee.getEmail());

        //Update employee
        createdEmployee.setEmail("john.doe2@example.com");
        employeeService.update(createdEmployee);

        //Assert updated e-mail address
        employeeInDb = employeeRepository.findById(createdEmployee.getId()).get();
        assertEquals("john.doe2@example.com", employeeInDb.getEmail());

        //Assert Kafka events. Two events should have been sent
        ConsumerRecords<String, Object> records = KafkaTestUtils.getRecords(consumer);
        assertEquals(2, records.count());

        List<EmployeeEvent> allEvents = StreamSupport.stream(records.spliterator(), false)
                .map(record -> (EmployeeEvent) record.value())
                .collect(Collectors.toList());

        //Last event should be of type UPDATED
        assertEquals(EmployeeEvent.Type.UPDATED, allEvents.get(allEvents.size() - 1).getType());
    }

    @Test
    public void whenEmployeeDeleted_deleteEmployeeInDB_andSendKafkaEvent() {
        EmployeeService employeeService = new DefaultEmployeeService(employeeRepository, kafkaTemplate);

        EmployeeDto employeeDto = getSampleEmployee();
        EmployeeDto createdEmployee = employeeService.create(employeeDto);

        employeeService.delete(createdEmployee.getId());

        //Assert Employee Service
        assertThrows(EntityNotFoundException.class, () -> employeeService.get(createdEmployee.getId()));

        //Assert DB
        assertEquals(0, employeeRepository.findAll().size());

        //Assert Kafka message
        ConsumerRecords<String, Object> records = KafkaTestUtils.getRecords(consumer);
        assertEquals(2, records.count());

        List<EmployeeEvent> allEvents = StreamSupport.stream(records.spliterator(), false)
                .map(record -> (EmployeeEvent) record.value())
                .collect(Collectors.toList());

        //Last event should be of type DELETED
        assertEquals(EmployeeEvent.Type.DELETED, allEvents.get(allEvents.size() - 1).getType());
    }

    private Consumer<String, Object> configureConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put("spring.json.trusted.packages", "io.github.floste7.employee.*");

        Consumer<String, Object> consumer = new DefaultKafkaConsumerFactory<String, Object>(consumerProps)
                .createConsumer();
        consumer.subscribe(Collections.singleton(KafkaConfig.EMPLOYEE_EVENT_TOPIC));

        return consumer;
    }

    private EmployeeDto getSampleEmployee() {
        return EmployeeDto.builder()
                .id("will-be-overwritten")
                .email("john.doe@example.com")
                .fullName("John Doe")
                .hobbies(Set.of("Hiking", "Roadtrips"))
                .birthday(new Date())
                .build();
    }
}
