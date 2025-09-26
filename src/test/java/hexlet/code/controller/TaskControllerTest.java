package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.mapper.TaskMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private User testUser;
    private Task testTask;
    private TaskStatus testTaskStatus;
    private Label testLabel;

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

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
        Set<Label> labels = Set.of(testLabel);

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.setLabels(labels);
        taskRepository.save(testTask);
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/tasks").with(token);
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        String body = response.getContentAsString();

        List<TaskDTO> taskDTOS = om.readValue(body, new TypeReference<>() { });
        var actual = taskDTOS.stream().map(taskMapper::map).toList();
        var expected = taskRepository.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testIndexWithTitleContains() throws Exception {
        var result = mockMvc.perform(get("/api/tasks?name="
                        + testTask.getName()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("title").asString().containsIgnoringCase(testTask.getName())));
    }

    @Test
    public void testIndexWithAssigneeId() throws Exception {
        var result = mockMvc.perform(get("/api/tasks?assigneeId="
                    +  testTask.getAssignee().getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("assignee_id").isEqualTo(testTask.getAssignee().getId()))
        );
    }

    @Test
    public void testIndexWithStatus() throws Exception {
        var result = mockMvc.perform(get("/api/tasks?status="
                        +  testTask.getTaskStatus().getSlug()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()))
        );
    }

    @Test
    public void testIndexWithLabelIds() throws Exception {
        var result = mockMvc.perform(get("/api/tasks?labelId="
                        +  testLabel.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("taskLabelIds").isArray()
                                .contains(testLabel.getId()))
        );
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/tasks/" + testTask.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var taskData = Instancio.of(modelGenerator.getTaskModel())
                .create();
        taskData.setAssignee(testUser);
        taskData.setTaskStatus(testTaskStatus);
        taskData.setLabels(Set.of(testLabel));
        var dto = taskMapper.map(taskData);

        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        Task task = taskRepository.findByName(taskData.getName()).orElseThrow();

        assertNotNull(task);
        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(taskData.getName()),
                v -> v.node("content").isEqualTo(taskData.getDescription())
        );
    }

    @Test
    public void testCreateWithoutName() throws Exception {
        var taskData = Instancio.of(modelGenerator.getTaskModel())
                .create();
        taskData.setName("");
        var dto = taskMapper.map(taskData);

        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new HashMap<>();
        data.put("title", "New title");

        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(data.get("title")),
                v -> v.node("content").isEqualTo(testTask.getDescription())
        );

        var task = taskRepository.findById(testTask.getId()).orElseThrow();

        assertEquals(data.get("title"), task.getName());
        assertEquals(testTask.getDescription(), task.getDescription());
    }

    @Test
    public void testDestroy() throws Exception {
        var request = delete("/api/tasks/" + testTask.getId()).with(token);
        mockMvc.perform(request).
                andExpect(status().isNoContent());

        boolean existTask = taskRepository.existsById(testTask.getId());

        assertFalse(existTask);
    }
}
