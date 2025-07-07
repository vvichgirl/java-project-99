package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;

@Setter
@Getter
public class UserDTO {
    private long id;

    private JsonNullable<String> firstName;

    private JsonNullable<String> lastName;

    private String email;

    private LocalDate createdAt;
}
