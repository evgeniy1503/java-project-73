package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.utils.TestUtils;

import org.assertj.core.api.AssertionsForClassTypes;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(SpringConfigForIT.TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class LabelControllerIT {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private LabelRepository labelRepository;

    @BeforeEach
    public void before() throws Exception {
        testUtils.regDefaultUser();
    }

    @AfterEach
    public void tearDown() {
        testUtils.tearDown();
    }

    @Test
    public void testCreateLabel() throws Exception {

        LabelDto labelDto = new LabelDto("bug");

        final var request = MockMvcRequestBuilders.post(TestUtils.LABEL_CONTROLLER_PATH)
                .content(TestUtils.asJson(labelDto))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = testUtils.perform(request, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Label label = TestUtils.fromJson(response.getContentAsString(), new TypeReference<Label>() {
        });

        assertThat(labelRepository.findAll().size()).isEqualTo(1);
        assertThat(labelDto.getName()).isEqualTo(label.getName());
    }

    @Test
    public void getAllLabels() throws Exception {

        LabelDto labelDto1 = new LabelDto("bug");
        LabelDto labelDto2 = new LabelDto("fix");

        final var request1 = MockMvcRequestBuilders.post(TestUtils.LABEL_CONTROLLER_PATH)
                .content(TestUtils.asJson(labelDto1))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(request1, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final var request2 = MockMvcRequestBuilders.post(TestUtils.LABEL_CONTROLLER_PATH)
                .content(TestUtils.asJson(labelDto2))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(request2, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated());

        final var response = testUtils.perform(
                        MockMvcRequestBuilders.get(TestUtils.LABEL_CONTROLLER_PATH),
                        TestUtils.TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(labelRepository.findAll().size()).isEqualTo(2);
        assertThat(response.getContentAsString()).contains("bug");
        assertThat(response.getContentAsString()).contains("fix");
    }

    @Test
    public void getLabelById() throws Exception {

        LabelDto labelDto = new LabelDto("bug");

        final var request = MockMvcRequestBuilders.post(TestUtils.LABEL_CONTROLLER_PATH)
                .content(TestUtils.asJson(labelDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(request, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated());

        final Label label = labelRepository.findAll().get(0);

        final var response = testUtils.perform(
                        MockMvcRequestBuilders.get(TestUtils.LABEL_CONTROLLER_PATH + UserController.ID, label.getId()),
                        TestUtils.TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label actualLabel = TestUtils.fromJson(response.getContentAsString(), new TypeReference<Label>() {
        });

        assertThat(label.getName()).isEqualTo(actualLabel.getName());


    }

    @Test
    public void updateLabel() throws Exception {

        LabelDto labelDto = new LabelDto("bug");

        final var request = MockMvcRequestBuilders.post(TestUtils.LABEL_CONTROLLER_PATH)
                .content(TestUtils.asJson(labelDto))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = testUtils.perform(request, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Long id = TestUtils.fromJson(response.getContentAsString(), new TypeReference<Label>() {
        }).getId();

        labelDto.setName("fix");

        final var requestToUpdate = MockMvcRequestBuilders.put(TestUtils.LABEL_CONTROLLER_PATH + UserController.ID, id)
                .content(TestUtils.asJson(labelDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(requestToUpdate, TestUtils.TEST_USERNAME)
                .andExpect(status().isOk());

        final Label updateLabel = labelRepository.findById(id).get();

        assertThat(labelDto.getName()).isEqualTo(updateLabel.getName());

    }

    @Test
    public void deleteLabel() throws Exception {

        LabelDto labelDto = new LabelDto("bug");

        final var request = MockMvcRequestBuilders.post(TestUtils.LABEL_CONTROLLER_PATH)
                .content(TestUtils.asJson(labelDto))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = testUtils.perform(request, TestUtils.TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Long id = TestUtils.fromJson(response.getContentAsString(), new TypeReference<Label>() {
        }).getId();

        testUtils.perform(MockMvcRequestBuilders.delete(
                TestUtils.LABEL_CONTROLLER_PATH + UserController.ID, id), TestUtils.TEST_USERNAME)
                .andExpect(status().isOk());

        AssertionsForClassTypes.assertThat(labelRepository.existsById(id)).isFalse();
    }
}
