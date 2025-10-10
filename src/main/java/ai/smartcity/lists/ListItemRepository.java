package ai.smartcity.lists;

import ai.smartcity.catalog.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListItemRepository extends JpaRepository<ListItem, Long> {
    List<ListItem> findByList(UserList list);
    boolean existsByListAndSensor(UserList list, Sensor sensor);
}
