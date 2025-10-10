package ai.smartcity.comments;

import ai.smartcity.auth.UserService;
import ai.smartcity.catalog.SensorService;
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

    // POST /api/sensors/{id}/comments
    @PostMapping("/sensors/{id}/comments")
    public Comment add(@PathVariable Long id,
                       @RequestBody CommentCreate req,
                       Authentication auth) {
        var sensor = sensors.findById(id);
        var user = users.findByUsername(auth.getName());
        var c = new Comment();
        c.setText(req.text());
        c.setSensor(sensor);
        c.setAuthor(user);
        return comments.save(c);
    }

    // GET /api/sensors/{id}/comments
    @GetMapping("/sensors/{id}/comments")
    public List<Comment> list(@PathVariable Long id) {
        var sensor = sensors.findById(id);
        return comments.findBySensorOrderByCreatedAtDesc(sensor);
    }

    // DELETE /api/comments/{id} (только автор)
    @DeleteMapping("/comments/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        var me = users.findByUsername(auth.getName());
        if (!comments.existsByIdAndAuthor(id, me)) throw new IllegalArgumentException("Not allowed");
        comments.deleteById(id);
    }

    public record CommentCreate(@NotBlank String text) {}
}
