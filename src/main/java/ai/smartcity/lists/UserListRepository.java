package ai.smartcity.lists;

import ai.smartcity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserListRepository extends JpaRepository<UserList, Long> {
    List<UserList> findByOwner(User owner);
    boolean existsByIdAndOwner(Long id, User owner);
}
