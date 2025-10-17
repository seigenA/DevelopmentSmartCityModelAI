package ai.smartcity.comments;

import ai.smartcity.auth.User;
import ai.smartcity.auth.UserService;
import ai.smartcity.catalog.Sensor;
import ai.smartcity.catalog.SensorService;
import ai.smartcity.notify.Notification;
import ai.smartcity.notify.NotificationRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentRepository comments;
    private final SensorService sensors;
    private final UserService users;
    private final NotificationRepository notifyRepo; // ✅ добавили репозиторий уведомлений

    // POST /api/sensors/{id}/comments
    @PostMapping("/sensors/{id}/comments")
    public Comment add(@PathVariable Long id,
                       @RequestBody CommentCreate req,
                       Authentication auth) {

        Sensor sensor = sensors.findById(id);
        User user = users.findByUsername(auth.getName());

        Comment c = new Comment();
        c.setText(req.text());
        c.setSensor(sensor);
        c.setAuthor(user);
        Comment saved = comments.save(c);

        // 🔔 уведомить автора сенсора, если это не мы
        var owner = sensor.getAuthor();
        if (owner != null && !owner.getUsername().equals(user.getUsername())) {
            Notification n = new Notification();
            n.setUser(owner);
            n.setType("COMMENT");
            n.setText(user.getUsername() + " commented on your sensor #" + sensor.getId());
            notifyRepo.save(n);
        }

        return saved;
    }

    // GET /api/sensors/{id}/comments
    @GetMapping("/sensors/{id}/comments")
    public List<Comment> list(@PathVariable Long id) {
        Sensor sensor = sensors.findById(id);
        return comments.findBySensorOrderByCreatedAtDesc(sensor);
    }

    // DELETE /api/comments/{id} (только автор)
    @DeleteMapping("/comments/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        var me = users.findByUsername(auth.getName());
        if (!comments.existsByIdAndAuthor(id, me)) {
            throw new IllegalArgumentException("Not allowed");
        }
        comments.deleteById(id);
    }

    public record CommentCreate(@NotBlank String text) {}
}
