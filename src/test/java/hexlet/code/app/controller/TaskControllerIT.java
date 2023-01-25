package hexlet.code.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.app.config.SpringConfigForIT;
import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Set;

import static hexlet.code.app.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.app.utils.TestUtils.ID;
import static hexlet.code.app.utils.TestUtils.TASK_CONTROLLER_PATH;
import static hexlet.code.app.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.app.utils.TestUtils.asJson;
import static hexlet.code.app.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerIT {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private User user;

    private TaskStatus taskStatus;

    private Label label;

    @BeforeEach
    public void before() throws Exception {
        testUtils.regDefaultUser();
    }

    @BeforeEach
    public void create() {
        user = userRepository.findByEmail(TEST_USERNAME).get();
        taskStatus = addTaskStatus("New");
        label = addLabel("bug");
    }

    @AfterEach
    public void tearDown() {
        testUtils.tearDown();
    }



    @Test
    public void testCreateTask() throws Exception {

        final TaskDto task = new TaskDto(
                "test",
                "test_description",
                user.getId(),
                taskStatus.getId(),
                Set.of(label.getId())
        );

        final var request = post(TASK_CONTROLLER_PATH)
                .content(asJson(task))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = testUtils.perform(request, TEST_USERNAME)
                .andExpect(status().isCreated());
        System.out.println(response.andReturn().getResponse().getContentAsString());


       assertThat(taskStatusRepository.findAll()).isNotNull();

    }

    @Test
    public void testGetAllTask() throws Exception {

        final TaskDto task1 = buildTaskDto(
                "Task_1",
                "test_description_1",
                user,
                taskStatus,
                label
        );

        final TaskDto task2 = buildTaskDto(
                "Task_2",
                "test_description_2",
                user,
                taskStatus,
                label
        );

        final var requestTask1 = post(TASK_CONTROLLER_PATH)
                .content(asJson(task1))
                .contentType(MediaType.APPLICATION_JSON);

        final var responseTask1 = testUtils.perform(requestTask1, TEST_USERNAME)
                .andExpect(status().isCreated());

        final var requestTask2 = post(TASK_CONTROLLER_PATH)
                .content(asJson(task2))
                .contentType(MediaType.APPLICATION_JSON);

        final var responseTask2 = testUtils.perform(requestTask2, TEST_USERNAME)
                .andExpect(status().isCreated());

        final var response = testUtils.perform(get(TASK_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Task_1", "test_description_1");
        assertThat(response.getContentAsString()).contains("Task_2", "test_description_2");
    }

    @Test
    public void getTaskById() throws Exception {

        final TaskDto taskDto = new TaskDto(
                "Task1",
                "test_description_1",
                user.getId(),
                taskStatus.getId(),
                Set.of(label.getId())
        );

        final var requestTask = post(TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestTask, TEST_USERNAME)
                .andExpect(status().isCreated());

        final Task expectedTask = taskRepository.findAll().get(0);

        final var response = testUtils.perform(
                get(TASK_CONTROLLER_PATH + ID, expectedTask.getId()),
                user.getEmail()
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task taskActual = fromJson(response.getContentAsString(), new TypeReference<Task>() {
        });

        assertEquals(expectedTask.getId(), taskActual.getId());
        assertEquals(expectedTask.getName(), taskActual.getName());
    }

    @Test
    public void deleteTask() throws Exception {

        final TaskDto taskDto = new TaskDto(
                "Task1",
                "test_description_1",
                user.getId(),
                taskStatus.getId(),
                Set.of(label.getId())
        );

        final var requestTask = post(TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(MediaType.APPLICATION_JSON);

        final var responseTask = testUtils.perform(requestTask, TEST_USERNAME)
                .andExpect(status().isCreated());

        assertThat(taskRepository.findAll().size()).isEqualTo(1);

        final Task task = taskRepository.findAll().get(0);

        testUtils.perform(
                        delete(TASK_CONTROLLER_PATH + ID, task.getId()),
                        user.getEmail()
                ).andExpect(status().isOk());

        assertThat(taskRepository.findAll().size()).isEqualTo(0);

    }

    @Test
    public void updateTask() throws Exception {

        final TaskDto taskDto = new TaskDto(
                "Task1",
                "test_description_1",
                user.getId(),
                taskStatus.getId(),
                Set.of(label.getId())
        );

        final var requestTask = post(TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestTask, TEST_USERNAME)
                .andExpect(status().isCreated());

        final Task task = taskRepository.findAll().get(0);

        taskDto.setName("UpdateTask");
        taskDto.setDescription("UpdateDisc");

        final var requestTaskUpdate = put(TASK_CONTROLLER_PATH + ID, task.getId())
                .content(asJson(taskDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestTaskUpdate, TEST_USERNAME)
                .andExpect(status().isOk());

        final Task taskUpdate = taskRepository.findAll().get(0);

        assertEquals(taskUpdate.getName(), "UpdateTask");
        assertEquals(taskUpdate.getDescription(), "UpdateDisc");

    }

    private TaskDto buildTaskDto(String name, String description, User user, TaskStatus taskStatus, Label label) {
        return  new TaskDto(
                name,
                description,
                user.getId(),
                taskStatus.getId(),
                Set.of(label.getId())
        );
    }

    private TaskStatus addTaskStatus(String name) {
        final TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName("New");
        return taskStatusRepository.save(taskStatus);
    }

    private Label addLabel(String name) {
        final Label label = new Label();
        label.setName("New");
        return labelRepository.save(label);
    }
}
