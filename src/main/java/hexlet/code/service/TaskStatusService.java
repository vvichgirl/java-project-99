package hexlet.code.service;

import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.dto.status.TaskStatusUpdateDTO;

import java.util.List;

public interface TaskStatusService {

    List<TaskStatusDTO> getAll();
    TaskStatusDTO findById(Long id);
    TaskStatusDTO create(TaskStatusCreateDTO taskStatusData);
    TaskStatusDTO update(TaskStatusUpdateDTO taskStatusData, Long id);
    void delete(Long id);
}
