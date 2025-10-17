package ai.smartcity.integrations;

import ai.smartcity.catalog.Sensor;
import ai.smartcity.catalog.SensorRepository;
import ai.smartcity.auth.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Map;

@Service
public class ExternalAirService {
    private final HttpClient http = HttpClient.newHttpClient();
    private final SensorRepository repo;
    private final UserService users;

    public ExternalAirService(SensorRepository repo, UserService users) {
        this.repo = repo; this.users = users;
    }

    @Cacheable(value = "air_now", key = "#lat + ':' + #lon")
    public String fetchCurrentAirJson(double lat, double lon) {
        try {
            // Документация: https://open-meteo.com/en/docs/air-quality-api
            String url = "https://air-quality-api.open-meteo.com/v1/air-quality"
                    + "?latitude=" + lat + "&longitude=" + lon
                    + "&hourly=pm2_5,pm10,carbon_monoxide,nitrogen_dioxide,ozone";
            var req = HttpRequest.newBuilder(URI.create(url)).GET().build();
            var resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "External API error");
            }
            return resp.body();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "External API failure: "+e.getMessage());
        }
    }

    /** Опционально: быстрый импорт как Sensor (минимум полей). */
    public Sensor importAsSensor(double lat, double lon, String username) {
        String json = fetchCurrentAirJson(lat, lon);
        Sensor s = new Sensor();
        s.setName("Air node (" + lat + "," + lon + ")");
        s.setType("AIR_QUALITY");
        s.setYear(LocalDate.now().getYear());
        s.setInstalledAt(LocalDate.now());
        s.setDescription("Imported from Open-Meteo API");
        s.setTags("air,api,auto");
        s.setAuthor(users.findByUsername(username));
        return repo.save(s);
    }
}
