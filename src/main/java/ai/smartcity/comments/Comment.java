package ai.smartcity.comments;

import ai.smartcity.auth.User;
import ai.smartcity.catalog.Sensor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "comment")
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 4000)
    private String text;

    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sensor_id")
    private Sensor sensor;
}
