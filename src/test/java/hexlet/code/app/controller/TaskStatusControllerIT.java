package hexlet.code.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.app.config.SpringConfigForIT;
import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
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

import static hexlet.code.app.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.app.controller.UserController.ID;
import static hexlet.code.app.utils.TestUtils.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.app.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.app.utils.TestUtils.asJson;
import static hexlet.code.app.utils.TestUtils.fromJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

        final var request = post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(status))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = testUtils.perform(request, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final TaskStatus savedTaskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(savedTaskStatus.getName()).isEqualTo(status.getName());
        assertThat(taskStatusRepository.getById(savedTaskStatus.getId())).isNotNull();
    }

    @Test
    public void gelAllStatuses() throws Exception {

        final TaskStatusDto status1 = new TaskStatusDto("Done");
        final TaskStatusDto status2 = new TaskStatusDto("In process");

        final var requestPost1 = post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(status1))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestPost1, TEST_USERNAME)
                .andExpect(status().isCreated());

        final var requestPost2 = post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(status2))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestPost2, TEST_USERNAME)
                .andExpect(status().isCreated());

        assertThat(taskStatusRepository.findAll().size()).isEqualTo(2);

        final var response = testUtils.perform(
                        get(TASK_STATUS_CONTROLLER_PATH),
                TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Done");
        assertThat(response.getContentAsString()).contains("In process");
    }

    @Test
    public void getStatusById() throws Exception {

        final TaskStatusDto status = new TaskStatusDto("Done");

        final var requestPost = post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(status))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestPost, TEST_USERNAME)
                .andExpect(status().isCreated());

        final TaskStatus taskStatus = taskStatusRepository.findAll().get(0);

        final var response = testUtils.perform(
                        get(TASK_STATUS_CONTROLLER_PATH + ID, taskStatus.getId()),
                        TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus actualTaskStatuses = fromJson(response.getContentAsString(), new TypeReference<TaskStatus>() {
        });

        assertThat(taskStatus.getName()).isEqualTo(actualTaskStatuses.getName());
    }

    @Test
    public void updateStatus() throws Exception {

        final TaskStatusDto statusDto = new TaskStatusDto("Done");

        final var requestPost = post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = testUtils.perform(requestPost, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Long id = fromJson(response.getContentAsString(), new TypeReference<TaskStatus>() {
        }).getId();

        statusDto.setName("In process");

        final var requestToUpdate = put(TASK_STATUS_CONTROLLER_PATH + ID, id)
                .content(asJson(statusDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestToUpdate, TEST_USERNAME)
                .andExpect(status().isOk());

        final TaskStatus updateStatus = taskStatusRepository.findById(id).get();

        assertThat(statusDto.getName()).isEqualTo(updateStatus.getName());
    }

    @Test
    public void deleteStatus() throws Exception {
        final TaskStatusDto status = new TaskStatusDto("Done");

        final var requestPost = post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(status))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = testUtils.perform(requestPost, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final long id = fromJson(response.getContentAsString(), new TypeReference<TaskStatus>() {
        }).getId();

        testUtils.perform(delete(TASK_STATUS_CONTROLLER_PATH + ID, id), TEST_USERNAME)
                .andExpect(status().isOk());

        assertThat(taskStatusRepository.existsById(id)).isFalse();
    }
}
