package com.lehdonjoki.listoo.repository;

import com.lehdonjoki.listoo.model.ShoppingList;
import com.lehdonjoki.listoo.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
  List<ShoppingList> findByUser(User user);

  Optional<ShoppingList> findByIdAndUserEmail(Long id, String email);
}
