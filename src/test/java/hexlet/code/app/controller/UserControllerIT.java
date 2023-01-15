package hexlet.code.app.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DBRider
@DataSet("user.yml")
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllUsers() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/api/users"))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("Evgeniy", "Prohorov");
        assertThat(response.getContentAsString()).contains("Oleg", "Petrov");
    }

    @Test
    void testGetUserById() throws Exception {

        MockHttpServletResponse response = mockMvc
                .perform(get("/api/users/2"))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("Oleg", "Petrov");

    }

    @Test
    void testCreatePerson() throws Exception {

        MockHttpServletResponse responsePost = mockMvc
                .perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" + "\"email\": \"ivan@google.com\",\n" + "\"firstName\": \"Ivan\",\n"
                                + "\"lastName\": \"Petrov\",\n" + "\"password\": \"some-password\"\n" + "}"))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(responsePost.getStatus()).isEqualTo(200);

        MockHttpServletResponse response = mockMvc
                .perform(get("/api/users"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("Ivan", "Petrov");

    }

    @Test
    void testUpDatePersonById() throws Exception {

        MockHttpServletResponse responsePatch = mockMvc
                .perform(put("/api/users/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" + "\"email\": \"ivan@google.com\",\n" + "\"firstName\": \"Leo\",\n"
                                + "\"lastName\": \"Petrov\",\n" + "\"password\": \"some-password\"\n" + "}")
                )
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(responsePatch.getStatus()).isEqualTo(200);

        MockHttpServletResponse response = mockMvc
                .perform(get("/api/users"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("Leo", "Petrov");
        assertThat(response.getContentAsString()).doesNotContain("Robert", "Lock");
    }

    @Test
    void testDeletePersonById() throws Exception {

        MockHttpServletResponse responseDelete = mockMvc
                .perform(delete("/api/users/1"))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(responseDelete.getStatus()).isEqualTo(200);

        MockHttpServletResponse response = mockMvc
                .perform(get("/api/users"))
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).doesNotContain("Evgeniy", "Prohorov");
    }

}
