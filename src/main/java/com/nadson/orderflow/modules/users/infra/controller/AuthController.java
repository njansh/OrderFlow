package com.nadson.orderflow.modules.users.infra.controller;

import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.modules.users.infra.controller.dto.LoginRequest;
import com.nadson.orderflow.modules.users.infra.controller.dto.LoginResponse;
import com.nadson.orderflow.modules.users.infra.controller.dto.SingUpRequest;
import com.nadson.orderflow.modules.users.infra.controller.dto.UserResponse;
import com.nadson.orderflow.modules.users.usecase.SingUpUseCase;
import com.nadson.orderflow.shared.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final SingUpUseCase singUpUseCase;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public AuthController(SingUpUseCase singUpUseCase,
                          AuthenticationManager authenticationManager,
                          TokenService tokenService,
                          UserRepository userRepository) {
        this.singUpUseCase = singUpUseCase;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

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

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        String token = tokenService.generateToken(authentication);
        return new LoginResponse(token);
    }

    @GetMapping("/me")
    public UserResponse me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.getUserByEmail(authentication.getName());
        return UserResponse.fromDomain(user);
    }
}
