package io.github.floste7.employee.backend.converter;

import io.github.floste7.employee.backend.model.Employee;
import io.github.floste7.employee.backend.model.Hobby;
import io.github.floste7.employee.common.EmployeeDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeConverter {

    EmployeeConverter INSTANCE = Mappers.getMapper(EmployeeConverter.class);

    EmployeeDto toDto(Employee employee);

    Employee toModel(EmployeeDto employeeDto);

    List<EmployeeDto> toDtoList(List<Employee> employeeList);

    default Set<Hobby> toHobbiesModel(Set<String> hobbies) {
        return hobbies.stream().map(Hobby::new).collect(Collectors.toSet());
    }

    default Set<String> toHobbiesString(Set<Hobby> hobbies) {
        return hobbies.stream().map(Hobby::getName).collect(Collectors.toSet());
    }

}
