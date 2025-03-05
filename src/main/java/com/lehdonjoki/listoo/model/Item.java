package com.lehdonjoki.listoo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private int quantity;

  @Column(nullable = false)
  private boolean purchased = false;

  @ManyToOne
  @JoinColumn(name = "shopping_list_id", nullable = false)
  private ShoppingList shoppingList;

  public Item(String name, int quantity, ShoppingList shoppingList) {
    this.name = name;
    this.quantity = quantity;
    this.purchased = false;
    this.shoppingList = shoppingList;
  }
}
