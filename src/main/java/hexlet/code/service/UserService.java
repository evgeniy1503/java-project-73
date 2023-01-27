package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;


public interface UserService {
    User createNewUser(UserDto userDto);

    User updateUser(long id, UserDto userDto);

    String getCurrentUserId();

    User getCurrentUser();

}
