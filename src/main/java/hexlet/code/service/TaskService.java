package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;

import java.util.List;

public interface TaskService {

    List<TaskDTO> getAll(TaskParamsDTO params);
    TaskDTO findById(Long id);
    TaskDTO create(TaskCreateDTO taskData);
    TaskDTO update(TaskUpdateDTO taskData, Long id);
    void delete(Long id);
}
