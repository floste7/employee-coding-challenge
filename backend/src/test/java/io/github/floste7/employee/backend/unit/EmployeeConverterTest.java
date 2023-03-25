package io.github.floste7.employee.backend.unit;

import io.github.floste7.employee.backend.converter.EmployeeConverter;
import io.github.floste7.employee.backend.model.Employee;
import io.github.floste7.employee.backend.model.Hobby;
import io.github.floste7.employee.common.EmployeeDto;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeConverterTest {

    @Test
    public void whenConvertToDto_returnDto() {
        String id = UUID.randomUUID().toString();
        Date birthday = new Date();

        Hobby hobby1 = new Hobby("Hiking");
        Hobby hobby2 = new Hobby("Roadtrips");

        Employee employee = new Employee();
        employee.setId(id);
        employee.setFullName("John Doe");
        employee.setEmail("john.doe@example.com");
        employee.setBirthday(birthday);
        employee.setHobbies(Set.of(hobby1, hobby2));

        EmployeeDto employeeDto = EmployeeConverter.INSTANCE.toDto(employee);

        assertEquals(id, employeeDto.getId());
        assertEquals("John Doe", employeeDto.getFullName());
        assertEquals("john.doe@example.com", employeeDto.getEmail());
        assertEquals(birthday, employeeDto.getBirthday());
        assertEquals(2, employeeDto.getHobbies().size());
    }

    @Test
    public void whenConvertToModel_returnModel() {
        String id = UUID.randomUUID().toString();
        Date birthday = new Date();

        EmployeeDto employeeDto = EmployeeDto.builder()
                .id(id)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .birthday(birthday)
                .hobbies(Set.of("Hiking", "Roadtrips"))
                .build();

        Employee employee = EmployeeConverter.INSTANCE.toModel(employeeDto);

        assertEquals(id, employee.getId());
        assertEquals("John Doe", employee.getFullName());
        assertEquals("john.doe@example.com", employee.getEmail());
        assertEquals(birthday, employee.getBirthday());
        assertEquals(2, employee.getHobbies().size());
    }
}
