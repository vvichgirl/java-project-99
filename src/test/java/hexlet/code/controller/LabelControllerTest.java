package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelMapper labelMapper;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private User testUser;
    private Label testLabel;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/labels").with(token);
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        String body = response.getContentAsString();

        List<LabelDTO> labelDTOS = om.readValue(body, new TypeReference<>() { });
        var actual = labelDTOS.stream().map(labelMapper::map).toList();
        var expected = labelRepository.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/labels/" + testLabel.getId()).with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getLabelModel())
                .create();

        var request = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        Label label = labelRepository.findByName(data.getName()).orElseThrow();

        assertNotNull(label);
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(data.getName())
        );
    }

    @Test
    public void testUpdate() throws Exception {

        var data = Instancio.of(modelGenerator.getLabelModel())
                .create();

        var request = put("/api/labels/" + testLabel.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(data.getName())
        );

        Label label = labelRepository.findByName(data.getName()).orElseThrow();

        assertEquals(data.getName(), label.getName());
    }
    @Test
    public void testDestroy() throws Exception {
        var request = delete("/api/labels/" + testLabel.getId()).with(token);
        mockMvc.perform(request).
                andExpect(status().isNoContent());

        boolean existLabel = labelRepository.existsById(testLabel.getId());

        assertFalse(existLabel);
    }


    @Test
    public void testDestroyIfExistTask() throws Exception {
        Set<Label> labels = Set.of(testLabel);

        TaskStatus testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        Task testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setTaskStatus(testTaskStatus);
        testTask.setLabels(labels);
        taskRepository.save(testTask);

        var request = delete("/api/labels/" + testLabel.getId()).with(token);
        mockMvc.perform(request).
                andExpect(status().isUnprocessableEntity());
    }
}
