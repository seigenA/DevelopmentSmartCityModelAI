package ai.smartcity.notify;

import ai.smartcity.auth.User;
import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;
import java.time.Instant;

@Getter @Setter
@Entity @Table(name="notification")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant createdAt = Instant.now();
    private String type;   // COMMENT|REACTION
    private String text;   // "User u2 commented on your sensor #5"
    private boolean readFlag = false;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="user_id")
    private User user;     // кому адресовано
}
