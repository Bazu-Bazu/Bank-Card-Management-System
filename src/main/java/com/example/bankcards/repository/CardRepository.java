package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    boolean existsByNumber(String number);
    void deleteById(Long id);
    Page<Card> findByUserId(Long userId, Pageable pageable);

    @Query(
            "SELECT c " +
            "FROM Card c " +
            "JOIN FETCH c.user u " +
            "WHERE c.id = :id"
    )
    Optional<Card> findWithUser(Long id);

}
