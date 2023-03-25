package io.github.floste7.employee.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDto {

    private String id;
    @Email(message = "Email address is invalid.", flags = { Pattern.Flag.CASE_INSENSITIVE })
    private String email;
    private String fullName;
    private Date birthday;
    private Set<String> hobbies;

}
