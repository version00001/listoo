package com.lehdonjoki.listoo.service;

import com.lehdonjoki.listoo.model.Item;
import com.lehdonjoki.listoo.model.ShoppingList;
import com.lehdonjoki.listoo.repository.ItemRepository;
import com.lehdonjoki.listoo.repository.ShoppingListRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {

  private final ItemRepository itemRepository;
  private final ShoppingListRepository shoppingListRepository;

  public ItemService(ItemRepository itemRepository, ShoppingListRepository shoppingListRepository) {
    this.itemRepository = itemRepository;
    this.shoppingListRepository = shoppingListRepository;
  }

  /** ✅ Get all items in a shopping list. */
  public List<Item> getItemsInList(String email, Long listId) {
    ShoppingList list = getUserShoppingList(email, listId);
    return itemRepository.findByShoppingList(list);
  }

  /** ✅ Add a new item to the shopping list. */
  @Transactional
  public Item addItemToList(String email, Long listId, String name, int quantity) {
    ShoppingList list = getUserShoppingList(email, listId);

    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Item name cannot be empty.");
    }
    if (quantity < 1) {
      throw new IllegalArgumentException("Quantity must be at least 1.");
    }

    Item newItem = new Item(name, quantity, false, list);
    return itemRepository.save(newItem);
  }

  /** ✅ Update an item's name or quantity. */
  @Transactional
  public Item updateItem(
      String email, Long listId, Long itemId, String newName, Integer newQuantity) {
    Item item = getItemInUserList(email, listId, itemId);

    if (newName != null && !newName.trim().isEmpty()) {
      item.setName(newName);
    }
    if (newQuantity != null && newQuantity > 0) {
      item.setQuantity(newQuantity);
    }

    return itemRepository.save(item);
  }

  /** ✅ Remove an item from the shopping list. */
  @Transactional
  public void removeItem(String email, Long listId, Long itemId) {
    Item item = getItemInUserList(email, listId, itemId);
    itemRepository.delete(item);
  }

  /** ✅ Toggle an item's purchased status. */
  @Transactional
  public Item toggleItemPurchased(String email, Long listId, Long itemId) {
    Item item = getItemInUserList(email, listId, itemId);
    item.setPurchased(!item.isPurchased());
    return itemRepository.save(item);
  }

  /** 🔍 Helper method: Get a shopping list owned by the authenticated user. */
  private ShoppingList getUserShoppingList(String email, Long listId) {
    return shoppingListRepository
        .findByIdAndUserEmail(listId, email)
        .orElseThrow(() -> new RuntimeException("Shopping list not found or access denied."));
  }

  /** 🔍 Helper method: Get an item within a shopping list owned by the authenticated user. */
  private Item getItemInUserList(String email, Long listId, Long itemId) {
    ShoppingList list = getUserShoppingList(email, listId);
    return itemRepository
        .findByIdAndShoppingList(itemId, list)
        .orElseThrow(() -> new RuntimeException("Item not found or access denied."));
  }
}
