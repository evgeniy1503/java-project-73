package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;

public interface TaskStatusService {
    TaskStatus createNewTaskStatus(TaskStatusDto taskStatusDto);

    TaskStatus updateTaskStatus(long id, TaskStatusDto taskStatusDto);
}
