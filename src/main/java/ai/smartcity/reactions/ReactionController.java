package ai.smartcity.reactions;

import ai.smartcity.auth.UserService;
import ai.smartcity.catalog.Sensor;
import ai.smartcity.catalog.SensorService;
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

    // PUT /api/sensors/{id}/reaction  — создать/обновить свою реакцию
    @PutMapping("/reaction")
    public Reaction upsert(@PathVariable Long id, @RequestBody ReactionDto req, Authentication auth){
        Sensor sensor = sensors.findById(id);
        var user = users.findByUsername(auth.getName());
        var r = reactions.findByUserAndSensor(user, sensor).orElseGet(() -> {
            var x = new Reaction();
            x.setUser(user); x.setSensor(sensor);
            return x;
        });
        r.setLiked(req.liked());
        r.setRating(req.rating());
        return reactions.save(r);
    }

    public record ReactionDto(Boolean liked, @Min(1) @Max(5) Integer rating) {}
}
