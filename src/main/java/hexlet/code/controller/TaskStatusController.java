package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
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
import java.util.List;

import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;


@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASK_STATUS_CONTROLLER_PATH)
public class TaskStatusController {

    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";

    private TaskStatusRepository taskStatusRepository;

    private TaskStatusService taskStatusService;

    @Operation(summary = "Get all task statuses")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = TaskStatus.class))
    ))
    @GetMapping
    public List<TaskStatus> getAll() {
        return taskStatusRepository.findAll().stream().toList();
    }

    @Operation(summary = "Get task status by id")
    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(ID)
    public TaskStatus getById(@PathVariable Long id) {
        return taskStatusRepository.findById(id).get();
    }

    @Operation(summary = "Create new task status")
    @ApiResponse(responseCode = "201", description = "Status created")
    @PostMapping()
    @ResponseStatus(CREATED)
    public TaskStatus createTaskStatus(@RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusService.createNewTaskStatus(taskStatusDto);
    }

    @Operation(summary = "Update a task status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task Status updated"),
            @ApiResponse(responseCode = "404", description = "Task Status with that id not found")
    })
    @PutMapping(ID)
    public TaskStatus updateTaskStatus(@PathVariable Long id, @RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusService.updateTaskStatus(id, taskStatusDto);
    }

    @Operation(summary = "Delete a task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task Status deleted"),
            @ApiResponse(responseCode = "404", description = "Task Status with that id not found")
    })
    @DeleteMapping(ID)
    public void deleteTaskStatus(@PathVariable Long id) {
        taskStatusRepository.deleteById(id);
    }
}
