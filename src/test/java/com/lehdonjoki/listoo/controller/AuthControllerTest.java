package com.lehdonjoki.listoo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lehdonjoki.listoo.model.User;
import com.lehdonjoki.listoo.repository.UserRepository;
import com.lehdonjoki.listoo.testutils.TestingDatabase;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestingDatabase
public class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  private User testUser;

  @BeforeEach
  public void setupTestData() {
    userRepository.deleteAll();
    testUser = new User(null, "test@example.com", passwordEncoder.encode("password"), "Test User");
    userRepository.save(testUser);
  }

  @Test
  public void testRegister() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(ContentType.JSON.toString())
                .content(
                    "{\"email\": \"newuser@example.com\", \"password\": \"password123\", \"name\": \"New User\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("User registered successfully."));
  }

  @Test
  public void testLogin() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(ContentType.JSON.toString())
                .content("{\"email\": \"test@example.com\", \"password\": \"password\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists());
  }

  /* @Test
  @WithMockUser(username = "test@example.com", roles = {"USER"})
  public void testRefreshToken() throws Exception {
    String refreshToken = authService.generateTokens(testUser).get("refreshToken");

    mockMvc.perform(post("/api/auth/refresh-token")
            .contentType(ContentType.JSON.toString())
            .content("{\"refreshToken\": \"" + refreshToken + "\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists());
  } */

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testLogout() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/logout")
                .contentType(ContentType.JSON.toString())
                .content("{\"email\": \"test@example.com\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Logged out successfully."));
  }
}
