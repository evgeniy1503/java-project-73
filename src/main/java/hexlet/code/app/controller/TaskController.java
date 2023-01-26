package hexlet.code.app.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static hexlet.code.app.controller.TaskController.TASK_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {
    public static final String TASK_CONTROLLER_PATH = "/tasks";
    public static final String ID = "/{id}";

    private static final String ONLY_AUTHOR_BY_ID = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;

    private final TaskRepository taskRepository;
    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(CREATED)
    public Task createNewTask(@RequestBody @Valid TaskDto taskDto) {
        return taskService.createNewTask(taskDto);
    }

    @GetMapping
    public Iterable<Task> getAllTask(@QuerydslPredicate(root = Task.class) Predicate predicate) {
        return taskRepository.findAll(predicate);
    }

    @GetMapping(ID)
    public Task getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id).get();
    }

    @PutMapping(ID)
    public Task updateTask(@RequestBody @Valid TaskDto taskDto,
                           @PathVariable Long id) {
        return taskService.updateTask(taskDto, id);
    }

    @DeleteMapping(ID)
    @PreAuthorize(ONLY_AUTHOR_BY_ID)
    public void deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }

}
