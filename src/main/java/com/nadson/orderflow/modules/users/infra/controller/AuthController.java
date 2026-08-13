package com.nadson.orderflow.modules.users.infra.controller;

import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.modules.users.infra.controller.dto.*;
import com.nadson.orderflow.modules.users.usecase.ListUsersUseCase;
import com.nadson.orderflow.modules.users.usecase.SingUpUseCase;
import com.nadson.orderflow.modules.users.usecase.UpdateUserUseCase;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import com.nadson.orderflow.shared.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Authentication & Users", description = "Endpoints for user signup, authentication, and management")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final SingUpUseCase singUpUseCase;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UpdateUserUseCase updateUserUseCase;
    private final ListUsersUseCase listUsersUseCase;

    public AuthController(SingUpUseCase singUpUseCase,
                          AuthenticationManager authenticationManager,
                          TokenService tokenService,
                          UserRepository userRepository, UpdateUserUseCase updateUserUseCase, ListUsersUseCase listUsersUseCase) {
        this.singUpUseCase = singUpUseCase;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.updateUserUseCase = updateUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
    }

    @Operation(summary = "Sign up user", description = "Creates a new user. The first registered user automatically becomes ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Email already registered or invalid input")
    })
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signUp(@RequestBody @Valid SingUpRequest request) {
        User user = singUpUseCase.execute(
                request.name(),
                request.email(),
                request.password()
        );
        return UserResponse.fromDomain(user);
    }

    @Operation(summary = "User login", description = "Authenticates user credentials and returns a JWT access token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        String token = tokenService.generateToken(authentication);
        return new LoginResponse(token);
    }

    @Operation(summary = "Current user profile", description = "Retrieves profile details of the currently authenticated user.")
    @GetMapping("/me")
    public UserResponse me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.getUserByEmail(authentication.getName());
        return UserResponse.fromDomain(user);
    }

    @Operation(summary = "List all users", description = "Returns all registered users. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "User does not have ADMIN role")
    })
    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.getUserByEmail(authentication.getName());

        if (currentUser.getRole() != Role.ADMIN) {
            throw new BusinessRuleException("Only admins can list all users.");
        }

        return listUsersUseCase.execute().stream()
                .map(UserResponse::fromDomain)
                .toList();
    }

    @Operation(summary = "Update user", description = "Updates user information or changes roles (ADMIN role required to update roles).")
    @PutMapping("/users")
    public UserResponse update(@RequestBody @Valid UpdateUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.getUserByEmail(authentication.getName());

        UUID targetUserId = request.id() != null ? request.id() : currentUser.getId();

        User targetUser = userRepository.getUserById(targetUserId);
        if (targetUser == null) {
            throw new BusinessRuleException("Target user not found");
        }

        Role finalRole = targetUser.getRole();
        if (request.role() != null && currentUser.getRole() == Role.ADMIN) {
            finalRole = Role.valueOf(request.role().toUpperCase());
        }

        UpdateUserUseCase.UserUpdateInput input = new UpdateUserUseCase.UserUpdateInput(
                targetUserId,
                request.name(),
                request.email(),
                finalRole
        );

        User updatedUser = updateUserUseCase.execute(input, currentUser);
        return UserResponse.fromDomain(updatedUser);
    }
}
