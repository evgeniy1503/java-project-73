package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import static hexlet.code.app.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;


@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASK_STATUS_CONTROLLER_PATH)
public class TaskStatusController {

    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskStatusService taskStatusService;

    @Operation(summary = "Get list of Task Statuses")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = TaskStatus.class))
    ))
    @GetMapping
    public List<TaskStatus> getAll() {
        return taskStatusRepository.findAll().stream().toList();
    }

    @Operation(summary = "Get Status by Id")
    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(ID)
    public TaskStatus getById(@PathVariable Long id) {
        return taskStatusRepository.findById(id).get();
    }

    @Operation(summary = "Create new Task Status")
    @ApiResponse(responseCode = "201", description = "Status created")
    @PostMapping()
    @ResponseStatus(CREATED)
    public TaskStatus createTaskStatus(@RequestBody TaskStatusDto taskStatusDto) {
        return taskStatusService.createNewTaskStatus(taskStatusDto);
    }

    @Operation(summary = "Update a Task Status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task Status updated"),
            @ApiResponse(responseCode = "404", description = "Task Status with that id not found")
    })
    @PutMapping(ID)
    public TaskStatus updateTaskStatus(@PathVariable Long id, @RequestBody TaskStatusDto taskStatusDto) {
        return taskStatusService.updateTaskStatus(id, taskStatusDto);
    }

    @Operation(summary = "Delete a Task Status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task Status deleted"),
            @ApiResponse(responseCode = "404", description = "Task Status with that id not found")
    })
    @DeleteMapping(ID)
    public void deleteTaskStatus(@PathVariable Long id) {
        taskStatusRepository.deleteById(id);
    }
}
