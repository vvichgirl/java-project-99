package hexlet.code.repository;

import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    Optional<Task> findByName(String name);

    boolean existsByTaskStatus(TaskStatus taskStatus);

    boolean existsByAssigneeId(Long assigneeId);

    @Query("SELECT EXISTS (SELECT 1 FROM Task t JOIN t.labels l WHERE l.id = :labelId)")
    boolean existTaskWithLabel(@Param("labelId") Long labelId);
}
