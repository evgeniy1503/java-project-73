package hexlet.code.app.repository;

import hexlet.code.app.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TaskRepository extends JpaRepository<Task, Long>, QuerydslPredicateExecutor<Task> {

}
