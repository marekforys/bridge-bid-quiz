package com.example.bridge.dto;

import java.util.List;

public record QuizHandResponse(
        String hand,
        String position,
        String convention,
        List<String> auction
) {}
