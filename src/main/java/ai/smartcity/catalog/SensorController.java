package ai.smartcity.catalog;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@Validated
public class SensorController {
    private final SensorService service;
    public SensorController(SensorService service){ this.service = service; }

    @GetMapping public List<Sensor> all(){ return service.findAll(); }
    @GetMapping("/{id}") public Sensor one(@PathVariable Long id){ return service.findById(id); }

    @PostMapping
    public ResponseEntity<Sensor> create(@Valid @RequestBody Sensor s, Authentication auth){
        return ResponseEntity.ok(service.create(s, auth));
    }

    @PutMapping("/{id}")
    public Sensor update(@PathVariable Long id, @Valid @RequestBody Sensor s, Authentication auth){
        return service.update(id, s, auth);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication auth){ service.delete(id, auth); }
}
