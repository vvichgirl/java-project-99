package hexlet.code.dto.status;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class TaskStatusUpdateDTO {
    @NotNull
    @Size(min = 1)
    private JsonNullable<String> name;

    @NotNull
    @Size(min = 1)
    private JsonNullable<String> slug;
}
