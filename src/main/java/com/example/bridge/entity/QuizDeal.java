package com.example.bridge.entity;

import com.example.bridge.model.HandPosition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "quiz_deal")
@Getter
@Setter
@NoArgsConstructor
public class QuizDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private HandPosition dealer;

    @Column(nullable = false, length = 64)
    private String northHand;

    @Column(nullable = false, length = 64)
    private String eastHand;

    @Column(nullable = false, length = 64)
    private String southHand;

    @Column(nullable = false, length = 64)
    private String westHand;

    @Column(length = 64)
    private String convention;

    @Lob
    @Column(name = "auction_json")
    private String auctionJson;
}
