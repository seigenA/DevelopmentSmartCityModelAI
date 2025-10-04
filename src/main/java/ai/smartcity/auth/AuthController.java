package ai.smartcity.auth;

import ai.smartcity.auth.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final UserService users;
    private final JwtService jwt;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Validated RegisterRequest req){
        users.register(req.username(), req.password());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Validated LoginRequest req){
        var user = users.authenticate(req.username(), req.password());
        return new AuthResponse(jwt.generate(user));
    }

    @GetMapping("/me")
    public MeResponse me(Authentication auth){
        var u = users.findByUsername(auth.getName());
        return new MeResponse(u.getId(), u.getUsername());
    }
}
