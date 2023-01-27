package hexlet.code.service.impl;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskService;
import hexlet.code.service.UserService;
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
