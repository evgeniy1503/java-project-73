package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.assertj.core.api.Assertions;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ActiveProfiles(SpringConfigForIT.TEST_PROFILE)
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
        user = userRepository.findByEmail(TestUtils.TEST_USERNAME).get();
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

        final var request = MockMvcRequestBuilders.post(TestUtils.TASK_CONTROLLER_PATH)
                .content(TestUtils.asJson(task))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = testUtils.perform(request, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated());
        System.out.println(response.andReturn().getResponse().getContentAsString());


       Assertions.assertThat(taskStatusRepository.findAll()).isNotNull();

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

        final var requestTask1 = MockMvcRequestBuilders.post(TestUtils.TASK_CONTROLLER_PATH)
                .content(TestUtils.asJson(task1))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestTask1, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated());

        final var requestTask2 = MockMvcRequestBuilders.post(TestUtils.TASK_CONTROLLER_PATH)
                .content(TestUtils.asJson(task2))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestTask2, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated());

        final var response = testUtils.perform(
                MockMvcRequestBuilders.get(TestUtils.TASK_CONTROLLER_PATH), TestUtils.TEST_USERNAME)
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

        final var requestTask = MockMvcRequestBuilders.post(TestUtils.TASK_CONTROLLER_PATH)
                .content(TestUtils.asJson(taskDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestTask, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated());

        final Task expectedTask = taskRepository.findAll().get(0);

        final var response = testUtils.perform(
                MockMvcRequestBuilders.get(TestUtils.TASK_CONTROLLER_PATH + TestUtils.ID, expectedTask.getId()),
                user.getEmail()
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task taskActual = TestUtils.fromJson(response.getContentAsString(), new TypeReference<Task>() {
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

        final var requestTask = MockMvcRequestBuilders.post(TestUtils.TASK_CONTROLLER_PATH)
                .content(TestUtils.asJson(taskDto))
                .contentType(MediaType.APPLICATION_JSON);

        final var responseTask = testUtils.perform(requestTask, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated());

        assertThat(taskRepository.findAll().size()).isEqualTo(1);

        final Task task = taskRepository.findAll().get(0);

        testUtils.perform(
                        MockMvcRequestBuilders.delete(TestUtils.TASK_CONTROLLER_PATH + TestUtils.ID, task.getId()),
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

        final var requestTask = MockMvcRequestBuilders.post(TestUtils.TASK_CONTROLLER_PATH)
                .content(TestUtils.asJson(taskDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestTask, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated());

        final Task task = taskRepository.findAll().get(0);

        taskDto.setName("UpdateTask");
        taskDto.setDescription("UpdateDisc");

        final var requestTaskUpdate = MockMvcRequestBuilders.put(
                TestUtils.TASK_CONTROLLER_PATH + TestUtils.ID, task.getId())
                .content(TestUtils.asJson(taskDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestTaskUpdate, TestUtils.TEST_USERNAME)
                .andExpect(status().isOk());

        final Task taskUpdate = taskRepository.findAll().get(0);

        assertEquals(taskUpdate.getName(), "UpdateTask");
        assertEquals(taskUpdate.getDescription(), "UpdateDisc");

    }

    @Test
    public void testFilter() throws Exception {

        final TaskStatus taskStatus1 = addTaskStatus("Created");

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
                taskStatus1,
                label
        );

        final var requestTask1 = MockMvcRequestBuilders.post(TestUtils.TASK_CONTROLLER_PATH)
                .content(TestUtils.asJson(task1))
                .contentType(MediaType.APPLICATION_JSON);

        final var response1 = testUtils.perform(requestTask1, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task taskForFilter = TestUtils.fromJson(response1.getContentAsString(), new TypeReference<Task>() {
        });

        final Long idStatus = taskForFilter.getTaskStatus().getId();
        final Long authorId = taskForFilter.getAuthor().getId();

        final var requestTask2 = MockMvcRequestBuilders.post(TestUtils.TASK_CONTROLLER_PATH)
                .content(TestUtils.asJson(task2))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestTask2, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated());

        final var response = testUtils.perform(
                get(TestUtils.TASK_CONTROLLER_PATH
                        + "?taskStatus=" + idStatus + "&authorId=" + authorId),
                        TestUtils.TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();




        assertThat(response.getContentAsString()).contains(label.getName());
        assertThat(response.getContentAsString()).contains(task1.getName());
        assertThat(response.getContentAsString()).contains(taskStatus.getName());
        assertThat(response.getContentAsString()).doesNotContain(task2.getName());



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
        taskStatus.setName(name);
        return taskStatusRepository.save(taskStatus);
    }

    private Label addLabel(String name) {
        final Label label = new Label();
        label.setName(name);
        return labelRepository.save(label);
    }
}
