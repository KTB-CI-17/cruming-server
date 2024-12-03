package com.ci.Cruming.auth.service;

import com.ci.Cruming.auth.entity.RefreshToken;
import com.ci.Cruming.auth.repository.RefreshTokenRepository;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken saveRefreshToken(User user, String refreshToken, LocalDateTime expiryDate) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiryDate(expiryDate)
                .build();

        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        return refreshTokenRepository.save(token);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void deleteByUserId(User user) {
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new CrumingException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    public RefreshToken rotate(RefreshToken oldToken, String newToken, LocalDateTime newExpiryDate) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(oldToken.getUser())
                .token(newToken)
                .expiryDate(newExpiryDate)
                .build();

        refreshTokenRepository.delete(oldToken);
        return refreshTokenRepository.save(refreshToken);
    }
}
