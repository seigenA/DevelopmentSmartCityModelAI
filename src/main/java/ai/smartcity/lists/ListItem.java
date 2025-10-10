package ai.smartcity.lists;

import ai.smartcity.catalog.Sensor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "list_item",
        uniqueConstraints = @UniqueConstraint(columnNames = {"list_id","sensor_id"}))
public class ListItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "list_id")
    private UserList list;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sensor_id")
    private Sensor sensor;
}
