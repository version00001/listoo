package com.lehdonjoki.listoo.service;

import com.lehdonjoki.listoo.model.ShoppingList;
import com.lehdonjoki.listoo.model.User;
import com.lehdonjoki.listoo.repository.ShoppingListRepository;
import com.lehdonjoki.listoo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final UserRepository userRepository;

    public ShoppingListService(ShoppingListRepository shoppingListRepository, UserRepository userRepository) {
        this.shoppingListRepository = shoppingListRepository;
        this.userRepository = userRepository;
    }

    /**
     * ✅ Fetch all shopping lists belonging to the authenticated user.
     */
    public List<ShoppingList> getAllLists(String email) {
        User user = getUserByEmail(email);
        return shoppingListRepository.findByUser(user);
    }

    /**
     * ✅ Create a new shopping list for the authenticated user.
     */
    @Transactional
    public ShoppingList createList(String email, String name) {
        User user = getUserByEmail(email);
        ShoppingList newList = new ShoppingList(name, user);
        return shoppingListRepository.save(newList);
    }

    /**
     * ✅ Get a specific shopping list (ensuring user owns the list).
     */
    public ShoppingList getListById(String email, Long listId) {
        return shoppingListRepository.findByIdAndUserEmail(listId, email)
                .orElseThrow(() -> new RuntimeException("Shopping list not found or access denied."));
    }

    /**
     * ✅ Update a shopping list (only name for now).
     */
    @Transactional
    public ShoppingList updateList(String email, Long listId, String newName) {
        ShoppingList list = getListById(email, listId);
        list.setName(newName);
        return shoppingListRepository.save(list);
    }

    /**
     * ✅ Delete a shopping list (only owner can delete).
     */
    @Transactional
    public void deleteList(String email, Long listId) {
        ShoppingList list = getListById(email, listId);
        shoppingListRepository.delete(list);
    }

    /**
     * ✅ Archive a shopping list.
     */
    @Transactional
    public ShoppingList archiveList(String email, Long listId) {
        ShoppingList list = getListById(email, listId);
        list.setArchived(true);
        return shoppingListRepository.save(list);
    }

    /**
     * ✅ Restore an archived shopping list.
     */
    @Transactional
    public ShoppingList restoreList(String email, Long listId) {
        ShoppingList list = getListById(email, listId);
        list.setArchived(false);
        return shoppingListRepository.save(list);
    }

    /**
     * ✅ Helper method to fetch user by email.
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));
    }
}
