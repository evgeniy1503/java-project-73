package hexlet.code.app.service.impl;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.TaskService;
import hexlet.code.app.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    private final LabelRepository labelRepository;
    private final UserService userService;

    @Override
    public Task createNewTask(TaskDto taskDto) {
        final User author = userService.getCurrentUser();
        Task newTask = new Task();
        newTask.setName(taskDto.getName());
        newTask.setAuthor(author);
        newTask.setExecutor(taskDto.getExecutorId() == null ? null : userRepository
                .findById(taskDto.getExecutorId()).orElse(null));
        newTask.setTaskStatus(taskStatusRepository.findById(taskDto.getTaskStatusId()).get());
        newTask.setDescription(taskDto.getDescription());
        newTask.setLabels(taskDto.getLabelIds() == null ? null : addLabels(taskDto.getLabelIds()));
        return taskRepository.save(newTask);
    }



    @Override
    public Task updateTask(TaskDto taskDto, long id) {
        final Task updateTask = taskRepository.findById(id).get();
        final User author = userService.getCurrentUser();
        updateTask.setName(taskDto.getName());
        updateTask.setAuthor(author);
        updateTask.setExecutor(taskDto.getExecutorId() == null ? null : userRepository
                .findById(taskDto.getExecutorId()).orElse(null));
        updateTask.setTaskStatus(taskStatusRepository.findById(taskDto.getTaskStatusId()).get());
        updateTask.setDescription(taskDto.getDescription());
        updateTask.setLabels(taskDto.getLabelIds() == null ? null : addLabels(taskDto.getLabelIds()));
        return taskRepository.save(updateTask);
    }

    private Set<Label> addLabels(Set<Long> labelIds) {
        Set<Label> labels = new HashSet<>();
        for (Long id : labelIds) {
            Label label = labelRepository.findById(id).get();
            labels.add(label);
        }
        return labels;
    }
}
