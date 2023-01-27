package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

public interface TaskService {
    Task createNewTask(TaskDto taskDto);

    Task updateTask(TaskDto taskDto, long id);
}
