package com.lehdonjoki.listoo.repository;

import com.lehdonjoki.listoo.model.RefreshToken;
import com.lehdonjoki.listoo.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByToken(String token);

  void deleteByUser(User user);

  Optional<RefreshToken> findByUser(User user);
}
