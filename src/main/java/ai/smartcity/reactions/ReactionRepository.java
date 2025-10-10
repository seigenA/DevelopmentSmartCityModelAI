package ai.smartcity.reactions;

import ai.smartcity.catalog.Sensor;
import ai.smartcity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserAndSensor(User user, Sensor sensor);

    @Query("select avg(r.rating) from Reaction r where r.sensor=:sensor and r.rating is not null")
    Double avgRating(Sensor sensor);

    @Query("select count(r) from Reaction r where r.sensor=:sensor and r.liked = true")
    long countLikes(Sensor sensor);
}
