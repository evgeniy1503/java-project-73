package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    @Override
    public TaskStatus updateTaskStatus(long id, TaskStatusDto taskStatusDto) {
        TaskStatus updateTaskStatus = taskStatusRepository.findById(id).get();
        updateTaskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(updateTaskStatus);
    }

    @Override
    public TaskStatus createNewTaskStatus(TaskStatusDto taskStatusDto) {
        TaskStatus newTaskStatus = new TaskStatus();
        newTaskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(newTaskStatus);
    }
}
