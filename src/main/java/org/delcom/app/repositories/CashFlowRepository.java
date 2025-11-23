package org.delcom.app.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.delcom.app.entities.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, UUID> {
    @Query("SELECT c FROM CashFlow c WHERE (LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.type) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND c.userId = :userId")
    List<CashFlow> findByKeyword(UUID userId, String keyword);

    @Query("SELECT c FROM CashFlow c WHERE c.userId = :userId")
    List<CashFlow> findAllByUserId(UUID userId);

    @Query("SELECT c FROM CashFlow c WHERE c.id = :id AND c.userId = :userId")
    Optional<CashFlow> findByUserIdAndId(UUID userId, UUID id);
}