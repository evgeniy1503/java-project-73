package hexlet.code.app.controller;

import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("${base-url}" + "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "")
    public Iterable<User> getUsers() {
        return userService.getAll();
    }

    @GetMapping(path = "{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping(path = "")
    public User createUser(@Valid @RequestBody UserDTO userDTO) throws NoSuchAlgorithmException {
        return userService.createNewUser(userDTO);
    }

    @PutMapping(path = "{id}")
    public User updateUser(@Valid @RequestBody UserDTO userDTO, @PathVariable Long id) {
        return userService.updateUser(userDTO, id);
    }

    @DeleteMapping(path = "{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
