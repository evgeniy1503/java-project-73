package hexlet.code.app.service;

import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.model.User;

import java.security.NoSuchAlgorithmException;


public interface UserService {
    User createNewUser(UserDTO userDTO) throws NoSuchAlgorithmException;
    User updateUser(UserDTO userDTO, Long id);
    void deleteUser(Long id);
    User getUserById(Long id);

    Iterable<User> getAll();

}
