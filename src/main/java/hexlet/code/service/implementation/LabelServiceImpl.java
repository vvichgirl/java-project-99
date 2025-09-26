package hexlet.code.service.implementation;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.RequestCannotBeProcessedException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.LabelService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;
    private final TaskRepository taskRepository;

    @Override
    public List<LabelDTO> getAll() {
        var labels = labelRepository.findAll(Sort.by(Sort.Order.asc("name")));
        return labels.stream()
                .map(labelMapper::map)
                .toList();
    }

    @Override
    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        return labelMapper.map(label);
    }

    @Override
    public LabelDTO create(LabelCreateDTO labelData) {
        var label = labelMapper.map(labelData);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    @Override
    public LabelDTO update(LabelUpdateDTO labelData, Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        labelMapper.update(labelData, label);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    @Override
    public void delete(Long id) {
        if (taskRepository.existTaskWithLabel(id)) {
            throw new RequestCannotBeProcessedException(
                    "There are tasks with this label. You can't delete this label."
            );
        }
        labelRepository.deleteById(id);
    }
}
