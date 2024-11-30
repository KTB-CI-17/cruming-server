package com.ci.Cruming.auth.repository;

import com.ci.Cruming.auth.entity.RefreshToken;
import com.ci.Cruming.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByToken(String token);
}
