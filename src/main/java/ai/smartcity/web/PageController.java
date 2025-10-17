package ai.smartcity.web;

import ai.smartcity.analytics.AnalyticsController;
import ai.smartcity.catalog.Sensor;
import ai.smartcity.catalog.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final AnalyticsController analytics;
    private final SensorService sensors;

    @GetMapping("/")
    public String index(Model model){
        List<Sensor> all = sensors.findAll();
        List<Sensor> top = all.stream()
                .filter(s -> s.getRating() != null)
                .sorted(Comparator
                        .comparing(Sensor::getRating, Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed()
                        .thenComparing(s -> s.getName() == null ? "" : s.getName(), String.CASE_INSENSITIVE_ORDER))
                .limit(5)
                .toList();

        model.addAttribute("overview", analytics.overview());
        model.addAttribute("top", top);
        return "index"; // templates/index.html
    }

    @GetMapping("/sensors")
    public String sensorsPage(Model model,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) String tag,
                              @RequestParam(required = false, name = "rating_min") Integer ratingMin,
                              @RequestParam(required = false, name = "sort") String sort) {
        List<Sensor> list = sensors.findAll();

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            list = list.stream().filter(s ->
                    (s.getName() != null && s.getName().toLowerCase().contains(q)) ||
                            (s.getDescription() != null && s.getDescription().toLowerCase().contains(q)) ||
                            (s.getTags() != null && s.getTags().toLowerCase().contains(q))
            ).toList();
        }
        if (tag != null && !tag.isBlank()) {
            String t = tag.toLowerCase();
            list = list.stream().filter(s ->
                    s.getTags() != null && s.getTags().toLowerCase().contains(t)
            ).toList();
        }
        if (ratingMin != null) {
            list = list.stream().filter(s ->
                    s.getRating() != null && s.getRating() >= ratingMin
            ).toList();
        }
        if (sort != null && !sort.isBlank()) {
            switch (sort) {
                case "rating_desc" -> list = list.stream()
                        .sorted(Comparator
                                .comparing(Sensor::getRating, Comparator.nullsLast(Comparator.naturalOrder()))
                                .reversed())
                        .toList();
                case "date_desc" -> list = list.stream()
                        .sorted(Comparator
                                .comparing(Sensor::getInstalledAt, Comparator.nullsLast(Comparator.naturalOrder()))
                                .reversed())
                        .toList();
                case "name" -> list = list.stream()
                        .sorted(Comparator.comparing(
                                s -> s.getName() == null ? "" : s.getName(),
                                String.CASE_INSENSITIVE_ORDER))
                        .toList();
                case "year_desc" -> list = list.stream()
                        .sorted(Comparator.comparingInt(Sensor::getYear).reversed())
                        .toList();
            }
        }

        model.addAttribute("items", list);
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);
        model.addAttribute("ratingMin", ratingMin);
        model.addAttribute("sort", sort);
        return "sensors"; // templates/sensors.html
    }
}
