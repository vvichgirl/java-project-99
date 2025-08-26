package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TaskDTO {

    private Long id;

    private JsonNullable<Integer> index;

    private String title;

    private JsonNullable<String> content;

    private String status;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    private List<Long> taskLabelIds = new ArrayList<>();

    private LocalDate createdAt;
}
