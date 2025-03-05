package com.lehdonjoki.listoo.controller;

import com.lehdonjoki.listoo.model.User;
import com.lehdonjoki.listoo.service.UserService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /** ✅ Get the authenticated user’s profile. */
  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<User> getUserProfile(Authentication authentication) {
    User user = userService.getUserByEmail(authentication.getName());
    return ResponseEntity.ok(user);
  }

  /** ✅ Update user profile (e.g., name). */
  @PutMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<User> updateUserProfile(
      Authentication authentication, @RequestBody Map<String, String> request) {
    String newName = request.get("name");
    User updatedUser = userService.updateUserProfile(authentication.getName(), newName);
    return ResponseEntity.ok(updatedUser);
  }

  /** ✅ Update user preferences (e.g., default list view). */
  @PutMapping("/me/preferences")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<User> updateUserPreferences(
      Authentication authentication, @RequestBody Map<String, String> preferences) {
    User updatedUser = userService.updateUserPreferences(authentication.getName(), preferences);
    return ResponseEntity.ok(updatedUser);
  }
}
