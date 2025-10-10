package ai.smartcity.history;

import ai.smartcity.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "action_log")
public class ActionLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant createdAt = Instant.now();
    private String action;     // CREATE_SENSOR / UPDATE_SENSOR / DELETE_SENSOR / ADD_COMMENT / DELETE_COMMENT ...
    private String entityType; // SENSOR / COMMENT
    private Long entityId;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id")
    private User user;
}
