package com.nadson.orderflow.modules.users.infra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.modules.users.infra.controller.dto.LoginRequest;
import com.nadson.orderflow.modules.users.infra.controller.dto.SingUpRequest;
import com.nadson.orderflow.modules.users.infra.controller.dto.UpdateUserRequest;
import com.nadson.orderflow.modules.users.usecase.ListUsersUseCase;
import com.nadson.orderflow.modules.users.usecase.SingUpUseCase;
import com.nadson.orderflow.modules.users.usecase.UpdateUserUseCase;
import com.nadson.orderflow.shared.security.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SingUpUseCase singUpUseCase;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UpdateUserUseCase updateUserUseCase;

    @MockitoBean
    private ListUsersUseCase listUsersUseCase;

    @Test
    void shouldSignUpUserSuccessfully() throws Exception {
        SingUpRequest request = new SingUpRequest("John Doe", "john@orderflow.com", "password123");
        User mockUser = new User(UUID.randomUUID(), "John Doe", "john@orderflow.com", "password123", Role.GUEST);

        when(singUpUseCase.execute(anyString(), anyString(), anyString())).thenReturn(mockUser);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@orderflow.com"));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("admin@orderflow.com", "password123");
        Authentication authMock = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);
        when(tokenService.generateToken(authMock)).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    @WithMockUser(username = "john@orderflow.com")
    void shouldReturnCurrentUserProfile() throws Exception {
        User mockUser = new User(UUID.randomUUID(), "John Doe", "john@orderflow.com", "password123", Role.GUEST);

        when(userRepository.getUserByEmail("john@orderflow.com")).thenReturn(mockUser);

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@orderflow.com"))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    @WithMockUser(username = "admin@orderflow.com")
    void shouldListUsersWhenUserIsAdmin() throws Exception {
        User adminUser = User.createAdmin("Admin", "admin@orderflow.com", "password123");
        User guestUser = new User(UUID.randomUUID(), "Guest", "guest@orderflow.com", "password123", Role.GUEST);

        when(userRepository.getUserByEmail("admin@orderflow.com")).thenReturn(adminUser);
        when(listUsersUseCase.execute(adminUser)).thenReturn(List.of(adminUser, guestUser));

        mockMvc.perform(get("/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "admin@orderflow.com")
    void shouldUpdateUserSuccessfully() throws Exception {
        User adminUser = User.createAdmin("Admin", "admin@orderflow.com", "password123");
        UUID targetId = UUID.randomUUID();
        User targetUser = new User(targetId, "Old Name", "user@orderflow.com", "password123", Role.GUEST);
        User updatedUser = new User(targetId, "New Name", "user@orderflow.com", "password123", Role.ATTENDANT);

        UpdateUserRequest request = new UpdateUserRequest(targetId, "New Name", "user@orderflow.com", "ATTENDANT");

        when(userRepository.getUserByEmail("admin@orderflow.com")).thenReturn(adminUser);
        when(userRepository.getUserById(targetId)).thenReturn(targetUser);
        when(updateUserUseCase.execute(any(), any())).thenReturn(updatedUser);

        mockMvc.perform(put("/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.role").value("ATTENDANT"));
    }
}