package ai.smartcity.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;

    public void register(String username, String password){
        if (repo.findByUsername(username).isPresent()){
            throw new IllegalArgumentException("Username already taken");
        }
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(encoder.encode(password));
        repo.save(u);
    }

    public User authenticate(String username, String password){
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return repo.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
    }

    public User findByUsername(String username){
        return repo.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }
}
