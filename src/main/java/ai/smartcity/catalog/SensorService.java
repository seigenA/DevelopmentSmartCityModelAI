package ai.smartcity.catalog;

import ai.smartcity.auth.User;
import ai.smartcity.auth.UserService;
import ai.smartcity.history.ActionLog;
import ai.smartcity.history.ActionLogRepository;
import ai.smartcity.reactions.ReactionRepository;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository repo;
    private final UserService users;
    private final ReactionRepository reactionRepo;
    private final ActionLogRepository logs;

    public List<Sensor> findAll() {
        return repo.findAll();
    }

    public Sensor findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Sensor create(Sensor s, Authentication auth) {
        User author = users.findByUsername(auth.getName());
        s.setAuthor(author);
        Sensor saved = repo.save(s);
        log(auth, "CREATE_SENSOR", "SENSOR", saved.getId());
        return saved;
    }

    public Sensor update(Long id, Sensor s, Authentication auth) {
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

        Sensor out = repo.save(cur);
        log(auth, "UPDATE_SENSOR", "SENSOR", id);
        return out;
    }

    public void delete(Long id, Authentication auth) {
        Sensor cur = findById(id);
        ensureOwner(cur, auth);
        repo.deleteById(id);
        log(auth, "DELETE_SENSOR", "SENSOR", id);
    }

    private void ensureOwner(Sensor s, Authentication auth) {
        if (s.getAuthor() == null || !s.getAuthor().getUsername().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only author can modify");
        }
    }

    private void log(Authentication auth, String action, String type, Long id) {
        var u = users.findByUsername(auth.getName());
        var l = new ActionLog();
        l.setAction(action);
        l.setEntityType(type);
        l.setEntityId(id);
        l.setUser(u);
        logs.save(l);
    }

    // ---------- Статистика лайков/рейтинга ----------
    public record SensorStats(
            Long id, String name, String type, @PositiveOrZero int year,
            java.time.LocalDate installedAt, String description,
            Integer rating, String tags, String mediaUrl,
            Long authorId, Long likes, Double avgRating) {}

    public List<SensorStats> findAllWithStats() {
        return repo.findAll().stream().map(s -> new SensorStats(
                s.getId(), s.getName(), s.getType(), s.getYear(),
                s.getInstalledAt(), s.getDescription(),
                s.getRating(), s.getTags(), s.getMediaUrl(),
                s.getAuthor() != null ? s.getAuthor().getId() : null,
                reactionRepo.countLikes(s),
                reactionRepo.avgRating(s)
        )).toList();
    }

    // ---------- Фильтры / поиск / сортировка ----------
    public List<Sensor> filter(String search, String tag, Integer rating, String sort) {
        var stream = repo.findAll().stream();

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            stream = stream.filter(x ->
                    (x.getName() != null && x.getName().toLowerCase().contains(q)) ||
                            (x.getDescription() != null && x.getDescription().toLowerCase().contains(q)) ||
                            (x.getTags() != null && x.getTags().toLowerCase().contains(q))
            );
        }
        if (tag != null && !tag.isBlank()) {
            String t = tag.toLowerCase();
            stream = stream.filter(x -> x.getTags() != null && x.getTags().toLowerCase().contains(t));
        }
        if (rating != null) {
            stream = stream.filter(x -> x.getRating() != null && x.getRating().equals(rating));
        }

        List<Sensor> list = stream.toList();

        if (sort != null) {
            boolean desc = sort.endsWith("_desc");
            String key = desc ? sort.substring(0, sort.length() - 5) : sort;
            Comparator<Sensor> cmp = switch (key) {
                case "rating" -> Comparator.comparing(
                        (Sensor s) -> s.getRating() == null ? -1 : s.getRating());
                case "date" -> Comparator.comparing(Sensor::getInstalledAt,
                        Comparator.nullsLast(Comparator.naturalOrder()));
                case "name" -> Comparator.comparing(Sensor::getName,
                        Comparator.nullsLast(String::compareToIgnoreCase));
                default -> null;
            };
            if (cmp != null) list = list.stream().sorted(desc ? cmp.reversed() : cmp).toList();
        }
        return list;
    }
}
