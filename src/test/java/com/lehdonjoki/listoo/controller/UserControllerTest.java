package com.lehdonjoki.listoo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@AutoConfigureMockMvc // ✅ Enables MockMvc
@TestingDatabase
public class UserControllerTest {

  @Autowired private MockMvc mockMvc; // ✅ Use MockMvc for security tests

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  public void setupTestUser() {
    userRepository.deleteAll();
    User testUser =
        new User(null, "test@example.com", passwordEncoder.encode("password"), "Test User");
    userRepository.save(testUser);
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"}) // ✅ Mock authentication
  public void testGetUserProfile() throws Exception {
    mockMvc
        .perform(get("/api/users/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.name").value("Test User"));
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testUpdateUserProfile() throws Exception {
    mockMvc
        .perform(
            put("/api/users/me")
                .contentType(ContentType.JSON.toString())
                .content("{\"name\": \"Updated Name\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Name"));
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testUpdateUserPreferences() throws Exception {
    mockMvc
        .perform(
            put("/api/users/me/preferences")
                .contentType(ContentType.JSON.toString())
                .content("{\"theme\": \"dark\", \"language\": \"en\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test User"));
  }
}
