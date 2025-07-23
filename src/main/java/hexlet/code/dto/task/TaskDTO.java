package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;

@Setter
@Getter
public class TaskDTO {

    private long id;

    private JsonNullable<Integer> index;

    private String title;

    private JsonNullable<String> content;

    private String status;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    private LocalDate createdAt;
}
