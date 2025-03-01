package com.lehdonjoki.listoo.repository;

import com.lehdonjoki.listoo.model.Item;
import com.lehdonjoki.listoo.model.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * ✅ Get all items for a specific shopping list.
     */
    List<Item> findByShoppingList(ShoppingList shoppingList);

    /**
     * ✅ Find an item by ID, ensuring it belongs to a specific shopping list.
     */
    Optional<Item> findByIdAndShoppingList(Long id, ShoppingList shoppingList);
}
