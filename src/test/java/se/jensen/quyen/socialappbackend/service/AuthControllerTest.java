package se.jensen.quyen.socialappbackend.controller;

import se.jensen.quyen.socialappbackend.dto.request.LoginRequestDTO;
import se.jensen.quyen.socialappbackend.dto.request.UserRequestDTO;
import se.jensen.quyen.socialappbackend.dto.response.LoginResponseDTO;
import se.jensen.quyen.socialappbackend.entity.User;
import se.jensen.quyen.socialappbackend.security.JwtUtil;
import se.jensen.quyen.socialappbackend.service.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    // ✅ REGISTER TEST
    @Test
    void testRegister() throws Exception {
        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setRole("USER");

        User user = new User();
        user.setUsername("newuser");
        user.setRole("USER");

        when(userService.registerUser(any(), any(), any()))
                .thenReturn(user);

        when(jwtUtil.generateToken("newuser", "USER"))
                .thenReturn("test-jwt-token");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    // ✅ LOGIN TEST
    @Test
    void testLogin() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("testuser");
        request.setPassword("password123");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("encoded")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtUtil.generateToken("testuser", "USER"))
                .thenReturn("test-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
