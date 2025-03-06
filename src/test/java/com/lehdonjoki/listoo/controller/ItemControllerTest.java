package com.lehdonjoki.listoo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lehdonjoki.listoo.model.Item;
import com.lehdonjoki.listoo.model.ShoppingList;
import com.lehdonjoki.listoo.model.User;
import com.lehdonjoki.listoo.repository.ItemRepository;
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
public class ItemControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ItemRepository itemRepository;

  @Autowired private ShoppingListRepository shoppingListRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  private User testUser;
  private ShoppingList testList;
  private Item testItem;

  @BeforeEach
  public void setupTestData() {
    testUser = new User(null, "test@example.com", passwordEncoder.encode("password"), "Test User");
    userRepository.save(testUser);

    testList = new ShoppingList("Groceries", testUser);
    shoppingListRepository.save(testList);

    testItem = new Item("Milk", 2, testList);
    itemRepository.save(testItem);
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testGetItemsInList() throws Exception {
    mockMvc
        .perform(get("/api/lists/" + testList.getId() + "/items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Milk"));
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testAddItemToList() throws Exception {
    mockMvc
        .perform(
            post("/api/lists/" + testList.getId() + "/items")
                .contentType(ContentType.JSON.toString())
                .content("{\"name\": \"Bread\", \"quantity\": 1}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Bread"));
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testUpdateItem() throws Exception {
    mockMvc
        .perform(
            put("/api/lists/" + testList.getId() + "/items/" + testItem.getId())
                .contentType(ContentType.JSON.toString())
                .content("{\"name\": \"Updated Milk\", \"quantity\": 3}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Milk"))
        .andExpect(jsonPath("$.quantity").value(3));
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testRemoveItem() throws Exception {
    mockMvc
        .perform(delete("/api/lists/" + testList.getId() + "/items/" + testItem.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Item removed successfully."));
  }

  @Test
  @WithMockUser(
      username = "test@example.com",
      roles = {"USER"})
  public void testToggleItemPurchased() throws Exception {
    mockMvc
        .perform(put("/api/lists/" + testList.getId() + "/items/" + testItem.getId() + "/toggle"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.purchased").value(true));
  }
}
