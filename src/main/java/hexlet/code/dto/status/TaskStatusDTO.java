package hexlet.code.dto.status;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class TaskStatusDTO {
    private long id;

    private String name;

    private String slug;

    private LocalDate createdAt;
}
