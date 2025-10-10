package ai.smartcity.reactions;

import ai.smartcity.auth.User;
import ai.smartcity.catalog.Sensor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="reaction",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","sensor_id"}))
public class Reaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // like (true/false). Можно не использовать и хранить только рейтинг — оставил для расширения
    private Boolean liked;

    @Min(1) @Max(5)
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sensor_id")
    private Sensor sensor;
}
