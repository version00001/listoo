package com.lehdonjoki.listoo.service;

import com.lehdonjoki.listoo.model.User;
import com.lehdonjoki.listoo.repository.UserRepository;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * ✅ Retrieve user by email.
   *
   * @throws RuntimeException if user is not found.
   */
  public User getUserByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found."));
  }

  /**
   * ✅ Update user profile (currently supports updating the name).
   *
   * @param email Authenticated user's email.
   * @param newName New name to update.
   * @return Updated user object.
   */
  @Transactional
  public User updateUserProfile(String email, String newName) {
    User user = getUserByEmail(email);

    if (newName == null || newName.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be empty.");
    }

    user.setName(newName);
    return userRepository.save(user);
  }

  /**
   * ✅ Update user preferences. This assumes preferences are stored as a JSON column or separate
   * fields.
   *
   * @param email Authenticated user's email.
   * @param preferences Map of preference key-value pairs.
   * @return Updated user object.
   */
  @Transactional
  public User updateUserPreferences(String email, Map<String, String> preferences) {
    User user = getUserByEmail(email);

    // Assuming preferences are stored separately in the User model, add logic here
    // Example: user.setPreferredTheme(preferences.get("theme"));

    return userRepository.save(user);
  }
}
