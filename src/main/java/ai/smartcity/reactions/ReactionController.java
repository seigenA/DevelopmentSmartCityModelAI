package ai.smartcity.reactions;

import ai.smartcity.auth.User;
import ai.smartcity.auth.UserService;
import ai.smartcity.catalog.Sensor;
import ai.smartcity.catalog.SensorService;
import ai.smartcity.notify.Notification;
import ai.smartcity.notify.NotificationRepository;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sensors/{id}")
public class ReactionController {
    private final ReactionRepository reactions;
    private final SensorService sensors;
    private final UserService users;
    private final NotificationRepository notifyRepo; // ✅ добавили репозиторий уведомлений

    // PUT /api/sensors/{id}/reaction — создать/обновить свою реакцию
    @PutMapping("/reaction")
    public Reaction upsert(@PathVariable Long id,
                           @RequestBody ReactionDto req,
                           Authentication auth) {

        Sensor sensor = sensors.findById(id);
        User user = users.findByUsername(auth.getName());

        Reaction r = reactions.findByUserAndSensor(user, sensor).orElseGet(() -> {
            Reaction x = new Reaction();
            x.setUser(user);
            x.setSensor(sensor);
            return x;
        });

        r.setLiked(req.liked());
        r.setRating(req.rating());
        Reaction saved = reactions.save(r);

        // 🔔 уведомить автора сенсора, если это не мы
        var owner = sensor.getAuthor();
        if (owner != null && !owner.getUsername().equals(user.getUsername())) {
            Notification n = new Notification();
            n.setUser(owner);
            n.setType("REACTION");
            n.setText(user.getUsername() + " reacted to your sensor #" + sensor.getId()
                    + (req.rating() != null ? " (rating " + req.rating() + ")" : ""));
            notifyRepo.save(n);
        }

        return saved;
    }

    public record ReactionDto(Boolean liked, @Min(1) @Max(5) Integer rating) {}
}
