package ehei.iot.coldChain.controller;

import ehei.iot.coldChain.dto.OperatorDto;
import ehei.iot.coldChain.dto.auth.LoginRequest;
import ehei.iot.coldChain.dto.auth.LoginResponse;
import ehei.iot.coldChain.dto.auth.RegisterRequest;
import ehei.iot.coldChain.entity.AppUser;
import ehei.iot.coldChain.repository.AppUserRepository;
import ehei.iot.coldChain.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AppUserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @GetMapping("/me")
    public ResponseEntity<OperatorDto> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        OperatorDto dto = new OperatorDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity.ok(dto);
    }
}
