package ai.smartcity.comments;

import ai.smartcity.catalog.Sensor;
import ai.smartcity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySensorOrderByCreatedAtDesc(Sensor sensor);
    boolean existsByIdAndAuthor(Long id, User author);
}
