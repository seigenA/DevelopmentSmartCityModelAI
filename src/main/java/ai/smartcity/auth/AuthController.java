package ai.smartcity.auth;

import ai.smartcity.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest req){
        users.register(req.getUsername(), req.getPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest req){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        var user = users.findByUsername(req.getUsername());
        return new AuthResponse(jwt.generate(user));
    }

    @GetMapping("/me")
    public MeResponse me(Authentication auth){
        var u = users.findByUsername(auth.getName());
        return new MeResponse(u.getId(), u.getUsername());
    }
}
