package hexlet.code.service.implementation;

import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.dto.status.TaskStatusUpdateDTO;
import hexlet.code.exception.RequestCannotBeProcessedException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;
    private final TaskRepository taskRepository;

    @Override
    public List<TaskStatusDTO> getAll() {
        var labels = taskStatusRepository.findAll(Sort.by(Sort.Order.asc("name")));
        return labels.stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    @Override
    public TaskStatusDTO findById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + id + " not found"));
        return taskStatusMapper.map(taskStatus);
    }

    @Override
    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusData) {
        var taskStatus = taskStatusMapper.map(taskStatusData);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    @Override
    public TaskStatusDTO update(TaskStatusUpdateDTO taskStatusData, Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + id + " not found"));
        taskStatusMapper.update(taskStatusData, taskStatus);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    @Override
    public void delete(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + id + " not found"));
        if (taskRepository.existsByTaskStatus(taskStatus)) {
            throw new RequestCannotBeProcessedException(
                    "There are tasks with this status. You can't delete this status."
            );
        }
        taskStatusRepository.deleteById(id);
    }
}
