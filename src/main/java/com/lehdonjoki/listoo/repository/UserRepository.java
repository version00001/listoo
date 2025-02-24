package com.lehdonjoki.listoo.repository;

import com.lehdonjoki.listoo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if a user with the given email exists.
     * @param email The email to check.
     * @return true if the user exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by their email address.
     * @param email The email of the user.
     * @return An Optional containing the user if found, or empty if not.
     */
    Optional<User> findByEmail(String email);
}
