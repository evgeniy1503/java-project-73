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
import static hexlet.code.app.utils.TestUtils.ID;
import static hexlet.code.app.utils.TestUtils.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.app.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.app.utils.TestUtils.asJson;
import static hexlet.code.app.utils.TestUtils.fromJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
        final TaskStatusDto statusDtoSave = new TaskStatusDto("Done");

        final var request = post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(statusDtoSave))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = testUtils.perform(request, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final TaskStatus savedTaskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(taskStatusRepository.getById(savedTaskStatus.getId())).isNotNull();
    }

    @Test
    public void updateStatus() throws Exception {
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
        status.setName("In process");

        final var requestToUpdate = put(TASK_STATUS_CONTROLLER_PATH + ID, id)
                .content(asJson(status))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestToUpdate, TEST_USERNAME)
                .andExpect(status().isOk());

        final TaskStatus updateStatus = taskStatusRepository.findById(id).get();

        assertThat(status.getName()).isEqualTo(updateStatus.getName());
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
