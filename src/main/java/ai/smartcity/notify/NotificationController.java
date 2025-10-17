package ai.smartcity.notify;

import ai.smartcity.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationRepository repo;
    private final UserService users;

    @GetMapping
    public List<Notification> my(Authentication auth){
        var me = users.findByUsername(auth.getName());
        return repo.findByUserOrderByCreatedAtDesc(me);
    }

    @PostMapping("/mark_read")
    public Map<String,Object> markAllRead(Authentication auth){
        var me = users.findByUsername(auth.getName());
        var list = repo.findByUserOrderByCreatedAtDesc(me);
        list.forEach(n-> n.setReadFlag(true));
        repo.saveAll(list);
        return Map.of("updated", list.size());
    }

    @GetMapping("/unread_count")
    public Map<String,Object> unreadCount(Authentication auth){
        var me = users.findByUsername(auth.getName());
        return Map.of("unread", repo.countByUserAndReadFlagIsFalse(me));
    }
}
