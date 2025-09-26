package hexlet.code.controller;

import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class LabelController {

    private LabelService labelService;

    @GetMapping(path = "")
    ResponseEntity<List<LabelDTO>> index() {
        var labels = labelService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labels);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    LabelDTO show(@PathVariable("id") long id) {
        return labelService.findById(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    LabelDTO create(@Valid @RequestBody LabelCreateDTO labelData) {
        return labelService.create(labelData);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    LabelDTO update(@RequestBody LabelUpdateDTO labelData, @PathVariable Long id) {
        return labelService.update(labelData, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable("id") long id) {
        labelService.delete(id);
    }
}
