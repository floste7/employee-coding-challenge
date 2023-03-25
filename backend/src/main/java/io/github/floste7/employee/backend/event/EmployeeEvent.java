package io.github.floste7.employee.backend.event;

import io.github.floste7.employee.common.EmployeeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EmployeeEvent {

    public enum Type {
        CREATED, UPDATED, DELETED;
    }

    private Type type;
    private EmployeeDto employee;

}
