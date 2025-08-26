package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TaskCreateDTO {

    @NotNull
    private JsonNullable<Integer> index;

    @NotBlank
    @Size(min = 1)
    private String title;

    @NotNull
    private JsonNullable<String> content;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    private List<Long> taskLabelIds = new ArrayList<>();

    @NotBlank
    private String status;
}
