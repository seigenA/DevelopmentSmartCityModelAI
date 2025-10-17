package ai.smartcity.analytics;

import ai.smartcity.auth.User;
import ai.smartcity.auth.UserRepository;
import ai.smartcity.catalog.Sensor;
import ai.smartcity.catalog.SensorRepository;
import ai.smartcity.comments.CommentRepository;
import ai.smartcity.reactions.ReactionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final SensorRepository sensors;
    private final ReactionRepository reactions;
    private final CommentRepository comments;
    private final UserRepository users;

    public AnalyticsController(SensorRepository sensors,
                               ReactionRepository reactions,
                               CommentRepository comments,
                               UserRepository users) {
        this.sensors = sensors;
        this.reactions = reactions;
        this.comments = comments;
        this.users = users;
    }

    @GetMapping("/overview")
    public Map<String, Object> overview() {
        var all = sensors.findAll();

        long total = all.size();

        Map<String, Long> byType = all.stream()
                .collect(Collectors.groupingBy(Sensor::getType, Collectors.counting()));

        double avgRating = all.stream()
                .filter(s -> s.getRating() != null)
                .mapToInt(Sensor::getRating)
                .average()
                .orElse(0);

        // топ авторов по количеству сенсоров
        Map<Long, Long> byAuthor = all.stream()
                .filter(s -> s.getAuthor() != null)
                .collect(Collectors.groupingBy(s -> s.getAuthor().getId(), Collectors.counting()));

        List<Map<String, Object>> topAuthors = byAuthor.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();   // <-- ЯВНЫЙ ТИП Map
                    m.put("userId", e.getKey());
                    m.put("username", users.findById(e.getKey()).map(User::getUsername).orElse("unknown"));
                    m.put("count", e.getValue());
                    return m;                                        // <-- возвращаем Map, не LinkedHashMap
                })
                .collect(Collectors.toList());                       // можно и .toList(), но так надёжнее


        long totalComments = comments.count();

        var out = new LinkedHashMap<String, Object>();
        out.put("totalSensors", total);
        out.put("byType", byType);
        out.put("avgRating", avgRating);
        out.put("topAuthors", topAuthors);
        out.put("totalComments", totalComments);
        return out;
    }
}
