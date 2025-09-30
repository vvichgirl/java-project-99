package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.mapper.TaskStatusMapper;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
public class TaskStatusControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private JwtRequestPostProcessor token;

    private User testUser;
    private TaskStatus testTaskStatus;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/task_statuses").with(token);
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        String body = response.getContentAsString();

        List<TaskStatusDTO> taskStatusDTOS = om.readValue(body, new TypeReference<>() { });
        var actual = taskStatusDTOS.stream().map(taskStatusMapper::map).toList();
        var expected = taskStatusRepository.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/task_statuses/" + testTaskStatus.getId()).with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testTaskStatus.getName()),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();

        var request = post("/api/task_statuses")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        TaskStatus taskStatus = taskStatusRepository.findBySlug(data.getSlug()).orElseThrow();

        assertNotNull(taskStatus);
        assertThatJson(body).and(
                v -> v.node("slug").isEqualTo(data.getSlug()),
                v -> v.node("name").isEqualTo(data.getName())
        );
    }

    @Test
    public void testUpdate() throws Exception {

        var data = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();

        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("slug").isEqualTo(data.getSlug()),
                v -> v.node("name").isEqualTo(data.getName())
        );

        TaskStatus taskStatus = taskStatusRepository.findBySlug(data.getSlug()).orElseThrow();

        assertEquals(data.getSlug(), taskStatus.getSlug());
        assertEquals(data.getName(), taskStatus.getName());
    }

    @Test
    public void testPartialUpdate() throws Exception {
        var data = new HashMap<String, String>();
        data.put("name", "new_name");

        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug()),
                v -> v.node("name").isEqualTo(data.get("name"))
        );

        TaskStatus taskStatus = taskStatusRepository.findById(testTaskStatus.getId()).orElseThrow();

        assertEquals(data.get("name"), taskStatus.getName());
        assertEquals(testTaskStatus.getSlug(), taskStatus.getSlug());
    }

    @Test
    public void testDestroy() throws Exception {
        var request = delete("/api/task_statuses/" + testTaskStatus.getId()).with(token);
        mockMvc.perform(request).
                andExpect(status().isNoContent());

        boolean existStatus = taskStatusRepository.existsById(testTaskStatus.getId());

        assertFalse(existStatus);
    }

    @Test
    public void testDestroyIfExistTask() throws Exception {
        Label testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
        Set<Label> labels = Set.of(testLabel);

        Task testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setTaskStatus(testTaskStatus);
        testTask.setLabels(labels);
        taskRepository.save(testTask);

        var request = delete("/api/task_statuses/" + testTaskStatus.getId()).with(token);
        mockMvc.perform(request).
                andExpect(status().isConflict());
    }
}
