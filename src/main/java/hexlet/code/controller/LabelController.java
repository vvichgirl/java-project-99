package hexlet.code.controller;

import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.RequestCannotBeProcessedException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    @GetMapping(path = "")
    ResponseEntity<List<LabelDTO>> index() {
        var labels = labelRepository.findAll();
        var result = labels.stream()
                .map(labelMapper::map)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(result);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    LabelDTO show(@PathVariable("id") long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        return labelMapper.map(label);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    LabelDTO create(@Valid @RequestBody LabelCreateDTO labelData) {
        var label = labelMapper.map(labelData);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    LabelDTO update(@RequestBody LabelUpdateDTO labelData, @PathVariable Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        labelMapper.update(labelData, label);
        labelRepository.save(label);
        var labelDTO = labelMapper.map(label);
        return labelDTO;
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable("id") long id) {
        labelRepository.deleteById(id);

        try {
            labelRepository.deleteById(id);
        } catch (Exception e) {
            throw new RequestCannotBeProcessedException(
                    "There are tasks with this label. You can't delete this label."
            );
        }
    }
}
