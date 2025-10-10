package ai.smartcity.lists;

import ai.smartcity.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_list")
public class UserList {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String name;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "owner_id")
    private User owner;
}
