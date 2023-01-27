package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.utils.TestUtils;

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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ActiveProfiles(SpringConfigForIT.TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskStatusControllerIT {
    @Autowired
    private TestUtils testUtils;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @BeforeEach
    public void before() throws Exception {
        testUtils.regDefaultUser();
    }

    @AfterEach
    public void tearDown() {
        testUtils.tearDown();
    }

    @Test
    public void createNewStatus() throws Exception {

        final TaskStatusDto status = new TaskStatusDto("Done");

        final var request = MockMvcRequestBuilders.post(TestUtils.TASK_STATUS_CONTROLLER_PATH)
                .content(TestUtils.asJson(status))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = testUtils.perform(request, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final TaskStatus savedTaskStatus = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(savedTaskStatus.getName()).isEqualTo(status.getName());
        assertThat(taskStatusRepository.getById(savedTaskStatus.getId())).isNotNull();
    }

    @Test
    public void gelAllStatuses() throws Exception {

        final TaskStatusDto status1 = new TaskStatusDto("Done");
        final TaskStatusDto status2 = new TaskStatusDto("In process");

        final var requestPost1 = MockMvcRequestBuilders.post(TestUtils.TASK_STATUS_CONTROLLER_PATH)
                .content(TestUtils.asJson(status1))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestPost1, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated());

        final var requestPost2 = MockMvcRequestBuilders.post(TestUtils.TASK_STATUS_CONTROLLER_PATH)
                .content(TestUtils.asJson(status2))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestPost2, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated());

        assertThat(taskStatusRepository.findAll().size()).isEqualTo(2);

        final var response = testUtils.perform(
                        MockMvcRequestBuilders.get(TestUtils.TASK_STATUS_CONTROLLER_PATH),
                TestUtils.TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Done");
        assertThat(response.getContentAsString()).contains("In process");
    }

    @Test
    public void getStatusById() throws Exception {

        final TaskStatusDto status = new TaskStatusDto("Done");

        final var requestPost = MockMvcRequestBuilders.post(TestUtils.TASK_STATUS_CONTROLLER_PATH)
                .content(TestUtils.asJson(status))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestPost, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated());

        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);

        final var response = testUtils.perform(
                        get(TestUtils.TASK_STATUS_CONTROLLER_PATH + UserController.ID, taskStatus.getId()),
                        TestUtils.TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus actualTaskStatuses = TestUtils.fromJson(
                response.getContentAsString(), new TypeReference<TaskStatus>() {
        });

        assertThat(taskStatus.getName()).isEqualTo(actualTaskStatuses.getName());
    }

    @Test
    public void updateStatus() throws Exception {

        final TaskStatusDto statusDto = new TaskStatusDto("Done");

        final var requestPost = MockMvcRequestBuilders.post(TestUtils.TASK_STATUS_CONTROLLER_PATH)
                .content(TestUtils.asJson(statusDto))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = testUtils.perform(requestPost, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Long id = TestUtils.fromJson(response.getContentAsString(), new TypeReference<TaskStatus>() {
        }).getId();

        statusDto.setName("In process");

        final var requestToUpdate = MockMvcRequestBuilders.put(
                TestUtils.TASK_STATUS_CONTROLLER_PATH + UserController.ID, id)
                .content(TestUtils.asJson(statusDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestToUpdate, TestUtils.TEST_USERNAME)
                .andExpect(status().isOk());

        final TaskStatus updateStatus = taskStatusRepository.findById(id).get();

        assertThat(statusDto.getName()).isEqualTo(updateStatus.getName());
    }

    @Test
    public void deleteStatus() throws Exception {
        final TaskStatusDto status = new TaskStatusDto("Done");

        final var requestPost = MockMvcRequestBuilders.post(TestUtils.TASK_STATUS_CONTROLLER_PATH)
                .content(TestUtils.asJson(status))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = testUtils.perform(requestPost, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final long id = TestUtils.fromJson(response.getContentAsString(), new TypeReference<TaskStatus>() {
        }).getId();

        testUtils.perform(MockMvcRequestBuilders.delete(
                TestUtils.TASK_STATUS_CONTROLLER_PATH + UserController.ID, id), TestUtils.TEST_USERNAME)
                .andExpect(status().isOk());

        assertThat(taskStatusRepository.existsById(id)).isFalse();
    }
}
