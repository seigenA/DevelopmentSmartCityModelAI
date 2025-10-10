package ai.smartcity.history;

import ai.smartcity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
    List<ActionLog> findByUserOrderByCreatedAtDesc(User user);
}
