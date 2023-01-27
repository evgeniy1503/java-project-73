package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Component
public class TestUtils {

    public static final String TEST_USERNAME = "mail@mail.ru";

    public static final String TEST_USERNAME_2 = "mail2@mail.ru";

    public static final String USER_CONTROLLER_PATH = "/users";

    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";

    public static final String TASK_CONTROLLER_PATH = "/tasks";

    public static final String LABEL_CONTROLLER_PATH = "/labels";

    public static final String LOGIN = "/login";

    public static final String ID = "/{id}";


    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();


    private final UserDto testRegistrationDto = new UserDto(
            TEST_USERNAME,
            "Oleg",
            "Petrov",
            "123"
    );

    public UserDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private JWTHelper jwtHelper;

    public void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
    }

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).get();
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    public ResultActions regUser(final UserDto dto) throws Exception {
        final var request = post(USER_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);
        return perform(request);
    }

    public String buildToken(Object userEmail) {
        return jwtHelper.expiring(Map.of(SPRING_SECURITY_FORM_USERNAME_KEY, userEmail));
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }

    public TaskStatus createStatus(String txt, String userName) throws Exception {
        final var taskStatusDto = new TaskStatusDto(txt);

        final var response = perform(
                MockMvcRequestBuilders.post(TestUtils.TASK_STATUS_CONTROLLER_PATH)
                        .content(TestUtils.asJson(taskStatusDto))
                        .contentType(APPLICATION_JSON),
                userName
        ).andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        TaskStatus status = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        return status;
    }

}
