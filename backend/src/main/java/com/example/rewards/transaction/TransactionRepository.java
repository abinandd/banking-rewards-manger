package com.example.rewards.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // For backwards compatibility where pagination is not yet implemented on the frontend
    List<Transaction> findAllByOrderByCreatedAtDesc();
}
