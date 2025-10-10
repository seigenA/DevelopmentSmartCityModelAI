package ai.smartcity.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    private final UserService users;

    // GET /api/profile (я сам)
    @GetMapping
    public Profile me(Authentication auth){
        var u = users.findByUsername(auth.getName());
        return new Profile(u.getId(), u.getUsername(), u.getDisplayName(), u.getEmail(), u.getAvatarUrl());
    }

    // PUT /api/profile (обновить имя/email)
    @PutMapping
    public Profile update(@RequestBody UpdateProfile req, Authentication auth){
        var u = users.findByUsername(auth.getName());
        if (req.displayName()!=null) u.setDisplayName(req.displayName());
        if (req.email()!=null) u.setEmail(req.email());
        users.save(u);
        return new Profile(u.getId(), u.getUsername(), u.getDisplayName(), u.getEmail(), u.getAvatarUrl());
    }

    // PUT /api/profile/avatar?url=... (привязать url, который вернул media.upload)
    @PutMapping("/avatar")
    public Profile setAvatar(@RequestParam String url, Authentication auth){
        var u = users.findByUsername(auth.getName());
        u.setAvatarUrl(url);
        users.save(u);
        return new Profile(u.getId(), u.getUsername(), u.getDisplayName(), u.getEmail(), u.getAvatarUrl());
    }

    public record UpdateProfile(String displayName, @Email String email){}
    public record Profile(Long id, String username, String displayName, String email, String avatarUrl){}
}
