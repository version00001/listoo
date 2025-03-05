package com.lehdonjoki.listoo.controller;

import com.lehdonjoki.listoo.model.Item;
import com.lehdonjoki.listoo.service.ItemService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lists/{listId}/items")
public class ItemController {

  private final ItemService itemService;

  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  /** ✅ Get all items in a shopping list. */
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<Item>> getItemsInList(
      @PathVariable Long listId, Authentication authentication) {
    List<Item> items = itemService.getItemsInList(authentication.getName(), listId);
    return ResponseEntity.ok(items);
  }

  /** ✅ Add a new item to the shopping list. */
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Item> addItemToList(
      @PathVariable Long listId,
      @RequestBody Map<String, Object> request,
      Authentication authentication) {

    String name = (String) request.get("name");
    Integer quantity = (Integer) request.getOrDefault("quantity", 1);

    Item newItem = itemService.addItemToList(authentication.getName(), listId, name, quantity);
    return ResponseEntity.ok(newItem);
  }

  /** ✅ Update an item (e.g., name, quantity). */
  @PutMapping("/{itemId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Item> updateItem(
      @PathVariable Long listId,
      @PathVariable Long itemId,
      @RequestBody Map<String, Object> request,
      Authentication authentication) {

    String newName = (String) request.get("name");
    Integer newQuantity = (Integer) request.get("quantity");

    Item updatedItem =
        itemService.updateItem(authentication.getName(), listId, itemId, newName, newQuantity);
    return ResponseEntity.ok(updatedItem);
  }

  /** ✅ Remove an item from the shopping list. */
  @DeleteMapping("/{itemId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> removeItem(
      @PathVariable Long listId, @PathVariable Long itemId, Authentication authentication) {

    itemService.removeItem(authentication.getName(), listId, itemId);
    return ResponseEntity.ok(Map.of("message", "Item removed successfully."));
  }

  /** ✅ Toggle an item’s purchased status. */
  @PutMapping("/{itemId}/toggle")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Item> toggleItemPurchased(
      @PathVariable Long listId, @PathVariable Long itemId, Authentication authentication) {

    Item toggledItem = itemService.toggleItemPurchased(authentication.getName(), listId, itemId);
    return ResponseEntity.ok(toggledItem);
  }
}
