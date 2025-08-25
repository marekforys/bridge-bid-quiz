package com.example.bridge.dto;

/**
 * For now, reuse the same shape as BidResponse to provide a suggestion
 * while the endpoint name implies checking. Can be extended later with
 * fields like `isConsistent`, `issues`, etc.
 */
public record CheckBidResponse(String suggestedBid, String explanation) {}
