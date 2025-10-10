package ai.smartcity.history;

import ai.smartcity.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HistoryController {
    private final ActionLogRepository logs;
    private final UserService users;

    // GET /api/users/me/history — история моих действий
    @GetMapping("/users/me/history")
    public List<ActionLog> my(Authentication auth){
        var me = users.findByUsername(auth.getName());
        return logs.findByUserOrderByCreatedAtDesc(me);
    }
}
