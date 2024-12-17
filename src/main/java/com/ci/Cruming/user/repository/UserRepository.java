package com.ci.Cruming.user.repository;

import com.ci.Cruming.common.constants.Platform;
import com.ci.Cruming.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPlatformAndPlatformId(Platform platform, String platformId);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.homeGym WHERE u.id = :id")
    Optional<User> findByIdWithHomeGym(@Param("id") Long id);

}