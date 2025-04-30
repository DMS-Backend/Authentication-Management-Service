package com.example.auth_service.controller;
import com.example.auth_service.config.JwtUtil;
import com.example.auth_service.model.User;
import com.example.auth_service.repositories.UserRepository;
import com.example.dto.AuthRequest;
import com.example.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    // Initialize default admin user when application starts
    @Bean
    public CommandLineRunner initAdminUser() {
        return args -> {
            // Check if admin user already exists
            if (userRepository.findByUsername("admin").isEmpty()) {
                log.info("Creating default admin user");
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin123")); // Set a default secure password
                adminUser.setRoles(new ArrayList<>());
                adminUser.getRoles().add("ROLE_ADMIN");  // Only admin role for true admin users
                userRepository.save(adminUser);
                log.info("Default admin user created successfully");
            }
        };
    }

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(new ArrayList<>());
        user.getRoles().add("ROLE_USER");
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
    
    // Register an admin user (requires admin privileges)
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(new ArrayList<>());
        user.getRoles().add("ROLE_ADMIN"); // Only admin role for true admin users
        userRepository.save(user);
        return ResponseEntity.ok("Admin user registered successfully");
    }

    // Authenticate and get JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        var userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
        // Generate JWT token using the JwtUtil
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    // (Optional) View all users (for debugging)
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}