package com.example.bridge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BidRequest(
        @NotBlank(message = "Provide a hand description, e.g. 'AKQJ.T987.AK.QJ9'") String hand,
        @NotBlank(message = "Provide a position: N, E, S, or W") String position,
        @NotBlank(message = "Provide a bidding convention, e.g. natural, precision, polish-club") String convention,
        @NotBlank(message = "Provide vulnerability: None, NS, EW, or All") String vulnerability,
        @NotNull @Size(max = 50) List<String> auction
) {}
