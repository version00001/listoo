package com.lehdonjoki.listoo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lehdonjoki.listoo.model.ShoppingList;
import com.lehdonjoki.listoo.model.User;
import com.lehdonjoki.listoo.repository.ShoppingListRepository;
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
public class ShoppingListControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ShoppingListRepository shoppingListRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  private User testUser;
  private ShoppingList testList;

  @BeforeEach
  public void setupTestData() {
    userRepository.deleteAll();
    shoppingListRepository.deleteAll();

    testUser = new User(null, "test@example.com", passwordEncoder.encode("password"), "Test User");
    userRepository.save(testUser);

    testList = new ShoppingList("Groceries", testUser);
    shoppingListRepository.save(testList);
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testGetAllLists() throws Exception {
    mockMvc
        .perform(get("/api/lists"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Groceries"));
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testCreateList() throws Exception {
    mockMvc
        .perform(
            post("/api/lists")
                .contentType(ContentType.JSON.toString())
                .content("{\"name\": \"New List\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New List"));
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testGetListById() throws Exception {
    mockMvc
        .perform(get("/api/lists/" + testList.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Groceries"));
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testUpdateList() throws Exception {
    mockMvc
        .perform(
            put("/api/lists/" + testList.getId())
                .contentType(ContentType.JSON.toString())
                .content("{\"name\": \"Updated List\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated List"));
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testDeleteList() throws Exception {
    mockMvc
        .perform(delete("/api/lists/" + testList.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Shopping list deleted successfully."));
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testArchiveList() throws Exception {
    mockMvc
        .perform(put("/api/lists/" + testList.getId() + "/archive"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.archived").value(true));
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testRestoreList() throws Exception {
    testList.setArchived(true);
    shoppingListRepository.save(testList);

    mockMvc
        .perform(put("/api/lists/" + testList.getId() + "/restore"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.archived").value(false));
  }
}
