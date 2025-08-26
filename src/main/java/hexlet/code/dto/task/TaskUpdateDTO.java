package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Setter
@Getter
public class TaskUpdateDTO {

    private JsonNullable<Integer> index;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    @NotNull
    @Size(min = 1)
    private JsonNullable<String> title;
    private JsonNullable<String> content;
    private JsonNullable<String> status;
    private JsonNullable<List<Long>> taskLabelIds;
}
