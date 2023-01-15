package hexlet.code.app.service;

import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.utils.Encryptor;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;


@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;

    @Override
    public User createNewUser(UserDTO userDTO) throws NoSuchAlgorithmException {
        User newUser = new User();
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setEmail(userDTO.getEmail());
        Encryptor encryptor = new Encryptor();
        newUser.setPassword(encryptor.encryptString(userDTO.getPassword()));
        return userRepository.save(newUser);
    }

    @Override
    public User updateUser(UserDTO userDTO, Long id) {
        User updateUser = userRepository.findById(id).get();
        updateUser.setFirstName(userDTO.getFirstName());
        updateUser.setLastName(userDTO.getLastName());
        updateUser.setEmail(userDTO.getEmail());
        updateUser.setPassword(userDTO.getPassword());
        return userRepository.save(updateUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public Iterable<User> getAll() {
        return userRepository.findAll();
    }
}
