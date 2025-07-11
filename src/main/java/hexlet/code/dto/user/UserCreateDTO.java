package hexlet.code.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class UserCreateDTO {

    @Email
    private String email;

    @Size(min = 3)
    private String password;

    private JsonNullable<String> firstName;

    private JsonNullable<String> lastName;
}
