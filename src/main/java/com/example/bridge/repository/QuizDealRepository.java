package com.example.bridge.repository;

import com.example.bridge.entity.QuizDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizDealRepository extends JpaRepository<QuizDeal, Long> {
}
