package se.jensen.quyen.socialappbackend.controller;

import se.jensen.quyen.socialappbackend.dto.request.LoginRequestDTO;
import se.jensen.quyen.socialappbackend.dto.request.UserRequestDTO;
import se.jensen.quyen.socialappbackend.dto.response.LoginResponseDTO;
import se.jensen.quyen.socialappbackend.entity.User;
import se.jensen.quyen.socialappbackend.security.JwtUtil;
import se.jensen.quyen.socialappbackend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    // ✅ Register (påverkar endast UserService + DB)
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(
            @RequestBody UserRequestDTO request) {

        // 🔒 Viktigt: UserService kontrollerar roll
        User user = userService.registerUser(
                request.getUsername(),
                request.getPassword(),
                request.getRole()
        );

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole()
        );

        return ResponseEntity.ok(
                new LoginResponseDTO(
                        token,
                        user.getUsername(),
                        user.getRole()
                )
        );
    }

    // ✅ Login (påverkar ENDAST security)
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String role = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .replace("ROLE_", "");

        String token = jwtUtil.generateToken(
                userDetails.getUsername(),
                role
        );

        return ResponseEntity.ok(
                new LoginResponseDTO(
                        token,
                        userDetails.getUsername(),
                        role
                )
        );
    }
}
