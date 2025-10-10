package ai.smartcity.lists;

import ai.smartcity.auth.UserService;
import ai.smartcity.catalog.SensorService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ListController {
    private final UserListRepository lists;
    private final ListItemRepository items;
    private final UserService users;
    private final SensorService sensors;

    // POST /api/lists  (создать список у текущего пользователя)
    @PostMapping("/lists")
    public UserList create(@RequestBody CreateList req, Authentication auth){
        var owner = users.findByUsername(auth.getName());
        var l = new UserList(); l.setName(req.name()); l.setOwner(owner);
        return lists.save(l);
    }

    // GET /api/lists (мои списки)
    @GetMapping("/lists")
    public List<UserList> myLists(Authentication auth){
        var owner = users.findByUsername(auth.getName());
        return lists.findByOwner(owner);
    }

    // POST /api/lists/{listId}/items  (добавить объект)
    @PostMapping("/lists/{listId}/items")
    public ListItem addItem(@PathVariable Long listId, @RequestParam Long sensorId, Authentication auth){
        var owner = users.findByUsername(auth.getName());
        if(!lists.existsByIdAndOwner(listId, owner)) throw new IllegalArgumentException("Not allowed");
        var l = lists.findById(listId).orElseThrow();
        var s = sensors.findById(sensorId);
        if(!items.existsByListAndSensor(l, s)){
            var it = new ListItem(); it.setList(l); it.setSensor(s);
            return items.save(it);
        }
        return items.findByList(l).stream().filter(x->x.getSensor().getId().equals(sensorId)).findFirst().orElseThrow();
    }

    // GET /api/lists/{listId}
    @GetMapping("/lists/{listId}")
    public List<ListItem> get(@PathVariable Long listId, Authentication auth){
        var owner = users.findByUsername(auth.getName());
        if(!lists.existsByIdAndOwner(listId, owner)) throw new IllegalArgumentException("Not allowed");
        var l = lists.findById(listId).orElseThrow();
        return items.findByList(l);
    }

    public record CreateList(@NotBlank String name){}
}
