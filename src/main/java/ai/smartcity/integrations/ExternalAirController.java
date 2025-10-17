package ai.smartcity.integrations;

import ai.smartcity.catalog.Sensor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/external/air")
public class ExternalAirController {
    private final ExternalAirService service;
    public ExternalAirController(ExternalAirService service) { this.service = service; }

    // Пример: GET /api/external/air/now?lat=43.2389&lon=76.8897
    @GetMapping("/now")
    public Map<String,Object> now(@RequestParam double lat, @RequestParam double lon){
        String json = service.fetchCurrentAirJson(lat, lon);
        return Map.of("source","open-meteo", "payload", json);
    }

    // Импорт данных как Sensor
    // POST /api/external/air/import?lat=...&lon=...
    @PostMapping("/import")
    public Sensor importNode(@RequestParam double lat, @RequestParam double lon, Authentication auth){
        return service.importAsSensor(lat, lon, auth.getName());
    }
}
