package com.lehdonjoki.listoo.controller;

import com.lehdonjoki.listoo.model.ShoppingList;
import com.lehdonjoki.listoo.service.ShoppingListService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lists")
@SecurityRequirement(name = "BearerAuth")
public class ShoppingListController {

  private final ShoppingListService shoppingListService;

  public ShoppingListController(ShoppingListService shoppingListService) {
    this.shoppingListService = shoppingListService;
  }

  /** ✅ Get all shopping lists for the authenticated user. */
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<ShoppingList>> getAllLists(Authentication authentication) {
    List<ShoppingList> lists = shoppingListService.getAllLists(authentication.getName());
    return ResponseEntity.ok(lists);
  }

  /** ✅ Create a new shopping list for the user. */
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ShoppingList> createList(
      Authentication authentication, @RequestBody Map<String, String> request) {
    String name = request.get("name");
    ShoppingList newList = shoppingListService.createList(authentication.getName(), name);
    return ResponseEntity.ok(newList);
  }

  /** ✅ Get a specific shopping list by ID. */
  @GetMapping("/{listId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ShoppingList> getList(
      @PathVariable Long listId, Authentication authentication) {
    ShoppingList list = shoppingListService.getListById(authentication.getName(), listId);
    return ResponseEntity.ok(list);
  }

  /** ✅ Update a shopping list’s name or properties. */
  @PutMapping("/{listId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ShoppingList> updateList(
      @PathVariable Long listId,
      @RequestBody Map<String, String> request,
      Authentication authentication) {
    String newName = request.get("name");
    ShoppingList updatedList =
        shoppingListService.updateList(authentication.getName(), listId, newName);
    return ResponseEntity.ok(updatedList);
  }

  /** ✅ Delete a shopping list. */
  @DeleteMapping("/{listId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> deleteList(@PathVariable Long listId, Authentication authentication) {
    shoppingListService.deleteList(authentication.getName(), listId);
    return ResponseEntity.ok(Map.of("message", "Shopping list deleted successfully."));
  }

  /** ✅ Archive a shopping list. */
  @PutMapping("/{listId}/archive")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ShoppingList> archiveList(
      @PathVariable Long listId, Authentication authentication) {
    ShoppingList archivedList = shoppingListService.archiveList(authentication.getName(), listId);
    return ResponseEntity.ok(archivedList);
  }

  /** ✅ Restore an archived shopping list. */
  @PutMapping("/{listId}/restore")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ShoppingList> restoreList(
      @PathVariable Long listId, Authentication authentication) {
    ShoppingList restoredList = shoppingListService.restoreList(authentication.getName(), listId);
    return ResponseEntity.ok(restoredList);
  }
}
