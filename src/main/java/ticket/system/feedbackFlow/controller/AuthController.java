package ticket.system.feedbackFlow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ticket.system.feedbackFlow.dto.AuthResponse;
import ticket.system.feedbackFlow.dto.LoginRequest;
import ticket.system.feedbackFlow.dto.RegisterRequest;
import ticket.system.feedbackFlow.dto.UserMapper;
import ticket.system.feedbackFlow.model.User;
import ticket.system.feedbackFlow.security.JwtUtil;
import ticket.system.feedbackFlow.service.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Register and login endpoints")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with role USER. " +
                      "Returns a JWT token on success."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201",
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "token": "eyJhbGci...",
                      "name": "Aakanksha",
                      "email": "user@gmail.com",
                      "role": "USER"
                    }
                    """)
            )),
        @ApiResponse(responseCode = "400",
            description = "Email already registered or validation failed",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-05-24T10:00:00",
                      "status": 400,
                      "message": "Email already registered",
                      "path": "/auth/register"
                    }
                    """)))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = userService.registerUser(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserMapper.toAuthResponse(user, token));
    }

    @Operation(
        summary = "Login",
        description = "Authenticate with email and password. " +
                      "Returns a JWT token valid for 24 hours."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class)
            )),
        @ApiResponse(responseCode = "400",
            description = "Invalid credentials",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "timestamp": "2026-05-24T10:00:00",
                      "status": 400,
                      "message": "Invalid credentials",
                      "path": "/auth/login"
                    }
                    """)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        User user = userService.loginUser(
                request.getEmail(),
                request.getPassword()
        );
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
        return ResponseEntity.ok(UserMapper.toAuthResponse(user, token));
    }
}