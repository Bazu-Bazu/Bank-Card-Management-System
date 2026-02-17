package com.example.bankcards.repository;

import com.example.bankcards.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query(
            "DELETE RefreshToken rt " +
            "WHERE rt.token = :token"
    )
    void deleteToken(@Param("token") String token);


}
