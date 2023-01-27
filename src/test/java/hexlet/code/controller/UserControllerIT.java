package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LoginDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ActiveProfiles(SpringConfigForIT.TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class UserControllerIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils testUtils;


    @AfterEach
    public void cleat() {
        testUtils.tearDown();
    }

    @Test
    public void registration() throws Exception {
        assertEquals(0, userRepository.count());
        testUtils.regDefaultUser().andExpect(status().isCreated());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void getUserById() throws Exception {
        testUtils.regDefaultUser();

        final User expectedUser = userRepository.findAll().get(0);

        final var response = testUtils.perform(
                        MockMvcRequestBuilders.get(
                                TestUtils.USER_CONTROLLER_PATH + UserController.ID, expectedUser.getId()),
                        expectedUser.getEmail()
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

         final User user = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
         });

         assertEquals(expectedUser.getId(), user.getId());
    }

    @Disabled("For now active only positive tests")
    @Test
    public void getUserByIdFails() throws Exception {
        testUtils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);
        testUtils.perform(MockMvcRequestBuilders.get(
                "${base-url}" + TestUtils.USER_CONTROLLER_PATH + UserController.ID, expectedUser.getId()))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void getAllUsers() throws Exception {
        testUtils.regDefaultUser();
        final var response = testUtils.perform(MockMvcRequestBuilders.get(TestUtils.USER_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(users).hasSize(1);
    }

    @Disabled("For now active only positive tests")
    @Test
    public void twiceRegTheSameUserFail() throws Exception {
        testUtils.regDefaultUser().andExpect(status().isCreated());
        testUtils.regDefaultUser().andExpect(status().isBadRequest());

        assertEquals(1, userRepository.count());
    }

    @Test
    public void login() throws Exception {
        final LoginDto loginDto = new LoginDto(
                testUtils.getTestRegistrationDto().getFirstName(),
                testUtils.getTestRegistrationDto().getLastName(),
                testUtils.getTestRegistrationDto().getEmail(),
                testUtils.getTestRegistrationDto().getPassword()
        );
        final var loginRequest = MockMvcRequestBuilders.post(
                TestUtils.LOGIN).content(TestUtils.asJson(loginDto)).contentType(MediaType.APPLICATION_JSON);
        testUtils.perform(loginRequest).andExpect(status().isUnauthorized());
    }

    @Test
    public void updateUser() throws Exception {
        testUtils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TestUtils.TEST_USERNAME).get().getId();

        final var userDto = new UserDto(TestUtils.TEST_USERNAME_2, "Petr", "Ivanov", "123");

        final var updateRequest = MockMvcRequestBuilders.put(TestUtils.USER_CONTROLLER_PATH + UserController.ID, userId)
                .content(TestUtils.asJson(userDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.perform(updateRequest, TestUtils.TEST_USERNAME).andExpect(status().isOk());

        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findByEmail(TestUtils.TEST_USERNAME).orElse(null));
        assertNotNull(userRepository.findByEmail(TestUtils.TEST_USERNAME_2).orElse(null));
    }
    @Test
    public void deleteUser() throws Exception {
        testUtils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TestUtils.TEST_USERNAME).get().getId();

        testUtils.perform(MockMvcRequestBuilders.delete(
                TestUtils.USER_CONTROLLER_PATH + UserController.ID, userId), TestUtils.TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, userRepository.count());
    }
}
