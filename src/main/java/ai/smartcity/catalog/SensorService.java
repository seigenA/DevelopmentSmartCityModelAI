package ai.smartcity.catalog;

import ai.smartcity.auth.User;
import ai.smartcity.auth.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class SensorService {
    private final SensorRepository repo;
    private final UserService users;

    public SensorService(SensorRepository repo, UserService users){
        this.repo = repo; this.users = users;
    }

    public List<Sensor> findAll(){ return repo.findAll(); }

    public Sensor findById(Long id){
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Sensor create(Sensor s, Authentication auth){
        User author = users.findByUsername(auth.getName());
        s.setAuthor(author);
        return repo.save(s);
    }

    public Sensor update(Long id, Sensor s, Authentication auth){
        Sensor cur = findById(id);
        ensureOwner(cur, auth);
        cur.setName(s.getName());
        cur.setType(s.getType());
        cur.setYear(s.getYear());
        cur.setInstalledAt(s.getInstalledAt());
        cur.setDescription(s.getDescription());
        cur.setRating(s.getRating());
        cur.setTags(s.getTags());
        cur.setMediaUrl(s.getMediaUrl());
        return repo.save(cur);
    }

    public void delete(Long id, Authentication auth){
        Sensor cur = findById(id);
        ensureOwner(cur, auth);
        repo.deleteById(id);
    }

    private void ensureOwner(Sensor s, Authentication auth){
        if (s.getAuthor() == null || !s.getAuthor().getUsername().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only author can modify");
        }
    }
}
