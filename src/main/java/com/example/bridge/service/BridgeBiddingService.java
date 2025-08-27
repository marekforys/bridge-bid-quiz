package com.example.bridge.service;

import com.example.bridge.dto.BidRequest;
import com.example.bridge.dto.BidResponse;
import com.example.bridge.dto.CheckBidRequest;
import com.example.bridge.dto.CheckBidResponse;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class BridgeBiddingService {

    public BidResponse suggestBid(BidRequest request) {
        HandAnalysis ha = analyzeHand(request.hand());
        String conv = normalizeConvention(request.convention());

        String suggested = suggestOpening(conv, ha);
        String explanation = String.format(
                "HCP=%d, dist=%s, convention='%s'. Open with %s by simple rules.",
                ha.hcp, ha.distSummary(), conv, suggested
        );
        return new BidResponse(suggested, explanation);
    }

    public CheckBidResponse checkBid(CheckBidRequest request) {
        HandAnalysis ha = analyzeHand(request.hand());
        String conv = normalizeConvention(request.convention());
        String suggested = suggestOpening(conv, ha);

        String verdict = request.proposedBid() != null && request.proposedBid().equalsIgnoreCase(suggested)
                ? "Your bid matches the suggested opening."
                : String.format("Your bid '%s' differs from suggested '%s'.", request.proposedBid(), suggested);

        String explanation = String.format(
                "%s HCP=%d, dist=%s, convention='%s'.",
                verdict, ha.hcp, ha.distSummary(), conv
        );
        return new CheckBidResponse(suggested, explanation);
    }

    /**
     * Public helper used by controllers to get an opening bid for a given hand/convention.
     * Auction context is ignored; this returns an opening only.
     */
    public String suggestOpeningBid(String hand, String convention) {
        HandAnalysis ha = analyzeHand(hand);
        String conv = normalizeConvention(convention);
        return suggestOpening(conv, ha);
    }

    // --- Simple evaluation and suggestion helpers ---

    private String normalizeConvention(String convention) {
        if (convention == null) return "natural";
        String c = convention.trim().toLowerCase();
        if (c.contains("precision") || c.contains("strong")) return "precision";
        if (c.contains("polish")) return "polish-club";
        if (c.contains("2/1") || c.contains("two-over-one") || c.contains("2-over-1")) return "2/1";
        if (c.contains("acol")) return "acol";
        if (c.contains("sayc") || c.contains("standard") || c.contains("std")) return "natural";
        return c;
    }

    private String suggestOpening(String convention, HandAnalysis ha) {
        // Basic rules, simplified:
        // - Precision: 16+ HCP open 1C; else natural-style opening
        // - Natural/2/1: 12+ HCP open 1 of longest 5+ suit; with balanced 15-17 suggest 1NT; else PASS
        // - Acol: 12+ HCP 4-card majors possible; use longest suit else PASS

        if ("precision".equals(convention)) {
            if (ha.hcp >= 16) return "1C"; // strong club
            // fallthrough to natural logic for limited hands
        }

        if ("polish-club".equals(convention)) {
            if (isBalanced(ha) && ha.hcp >= 15 && ha.hcp <= 17) return "1NT"; // PC still 1NT for 15-17
            if (isBalanced(ha) && ha.hcp >= 12 && ha.hcp <= 14) return "1C";  // 12-14 balanced 1C
            if (ha.hcp >= 12) {
                char ls = ha.longest5PlusSuit();
                if (ls != '\0') return "1" + suitSymbolToBid(ls);
                return "1" + (ha.counts.get('C') >= ha.counts.get('D') ? 'C' : 'D');
            }
            return "PASS";
        }

        if ("natural".equals(convention) || "2/1".equals(convention) || "precision".equals(convention)) {
            if (isBalanced(ha) && ha.hcp >= 15 && ha.hcp <= 17) return "1NT";
            if (ha.hcp >= 12) {
                char ls = ha.longest5PlusSuit();
                if (ls != '\0') return "1" + suitSymbolToBid(ls);
                // No 5-card suit: open better minor (prefer clubs if equal here)
                return "1" + (ha.counts.get('C') >= ha.counts.get('D') ? 'C' : 'D');
            }
            return "PASS";
        }

        if ("acol".equals(convention)) {
            if (ha.hcp >= 12) {
                // Allow 4-card major opening: pick longest; tie-break S, H, D, C
                char suit = ha.longestSuitAny();
                return "1" + suitSymbolToBid(suit);
            }
            return "PASS";
        }

        // default
        return ha.hcp >= 12 ? "1" + suitSymbolToBid(ha.longestSuitAny()) : "PASS";
    }

    private boolean isBalanced(HandAnalysis ha) {
        // Balanced patterns: 4-3-3-3, 4-4-3-2, 5-3-3-2
        int s = ha.counts.get('S');
        int h = ha.counts.get('H');
        int d = ha.counts.get('D');
        int c = ha.counts.get('C');
        int[] arr = new int[]{s, h, d, c};
        java.util.Arrays.sort(arr);
        String key = arr[3] + "-" + arr[2] + "-" + arr[1] + "-" + arr[0];
        return key.equals("4-3-3-3") || key.equals("4-4-3-2") || key.equals("5-3-3-2");
    }

    private char suitSymbolToBid(char suit) { return suit; }

    private HandAnalysis analyzeHand(String hand) {
        // Expect format like "AKQJ.T987.AK.QJ9" (S.H.D.C), empty suit allowed as ""
        Map<Character, String> suits = parseSuits(hand);
        int hcp = computeHcp(suits);
        Map<Character, Integer> counts = new HashMap<>();
        counts.put('S', suits.get('S').length());
        counts.put('H', suits.get('H').length());
        counts.put('D', suits.get('D').length());
        counts.put('C', suits.get('C').length());
        return new HandAnalysis(hcp, suits, counts);
    }

    private Map<Character, String> parseSuits(String hand) {
        Map<Character, String> suits = new HashMap<>();
        suits.put('S', ""); suits.put('H', ""); suits.put('D', ""); suits.put('C', "");
        if (hand == null || hand.isEmpty()) return suits;
        String[] parts = hand.trim().split("\\.", -1);
        if (parts.length == 4) {
            suits.put('S', parts[0]);
            suits.put('H', parts[1]);
            suits.put('D', parts[2]);
            suits.put('C', parts[3]);
        }
        return suits;
    }

    private int computeHcp(Map<Character, String> suits) {
        int hcp = 0;
        for (String cards : suits.values()) {
            for (char r : cards.toCharArray()) {
                switch (r) {
                    case 'A': hcp += 4; break;
                    case 'K': hcp += 3; break;
                    case 'Q': hcp += 2; break;
                    case 'J': hcp += 1; break;
                }
            }
        }
        return hcp;
    }

    private static class HandAnalysis {
        final int hcp;
        final Map<Character, String> suits;
        final Map<Character, Integer> counts;
        HandAnalysis(int hcp, Map<Character, String> suits, Map<Character, Integer> counts) {
            this.hcp = hcp; this.suits = suits; this.counts = counts;
        }
        String distSummary() {
            return String.format("%d-%d-%d-%d", counts.get('S'), counts.get('H'), counts.get('D'), counts.get('C'));
        }
        char longest5PlusSuit() {
            char best = '\0'; int bestLen = 0;
            for (char suit : new char[]{'S','H','D','C'}) {
                int len = counts.get(suit);
                if (len >= 5 && len > bestLen) { best = suit; bestLen = len; }
            }
            return best;
        }
        char longestSuitAny() {
            char best = 'C'; int bestLen = -1;
            for (char suit : new char[]{'S','H','D','C'}) {
                int len = counts.get(suit);
                if (len > bestLen) { best = suit; bestLen = len; }
            }
            return best;
        }
    }
}
