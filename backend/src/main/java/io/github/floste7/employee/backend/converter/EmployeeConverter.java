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

    /**
     * Converts an {@link Employee} to a {@link EmployeeDto}.
     *
     * @param employee the {@link Employee} to be converted
     * @return converted {@link EmployeeDto}
     */
    EmployeeDto toDto(Employee employee);

    /**
     * Converts an {@link EmployeeDto} to an {@link Employee}.
     *
     * @param employeeDto {@link EmployeeDto} to be converted
     * @return converted {@link Employee}
     */
    Employee toModel(EmployeeDto employeeDto);

    /**
     * Converts a list of {@link Employee} to a list of {@link EmployeeDto}.
     *
     * @param employeeList list of {@link Employee}s to be converted
     * @return converted list of {@link EmployeeDto}s
     */
    List<EmployeeDto> toDtoList(List<Employee> employeeList);

    /**
     * Converts a set of {@link Hobby} to a set of hobby string containing their names.
     *
     * @param hobbies set of {@link Hobby} to be converted
     * @return set of strings with theirs names
     */
    default Set<Hobby> toHobbiesModel(Set<String> hobbies) {
        return hobbies.stream().map(Hobby::new).collect(Collectors.toSet());
    }

    /**
     * Converts a set of hobby names as strings to a set of {@link Hobby}
     *
     * @param hobbies set of hobby names
     * @return set of {@link Hobby}
     */
    default Set<String> toHobbiesString(Set<Hobby> hobbies) {
        return hobbies.stream().map(Hobby::getName).collect(Collectors.toSet());
    }

}
