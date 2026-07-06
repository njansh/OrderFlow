package com.nadson.orderflow.modules.users.infra.controller;

import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.infra.controller.dto.SingUpRequest;
import com.nadson.orderflow.modules.users.infra.controller.dto.UserResponse;
import com.nadson.orderflow.modules.users.usecase.SingUpUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final SingUpUseCase singUpUseCase;

    public AuthController(SingUpUseCase singUpUseCase) {
        this.singUpUseCase = singUpUseCase;
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

    @GetMapping("/me")
    public UserResponse me() {
        // TODO: Implement getting current authenticated user from SecurityContext
        return null;
    }
}
