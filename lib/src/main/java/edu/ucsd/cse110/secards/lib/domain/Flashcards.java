package edu.ucsd.cse110.secards.lib.domain;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Flashcards {
    @NonNull
    public static List<Flashcard> rotate(List<Flashcard> cards, int k) {
        // Build mapping from old to new sort orders using Math.floorMod to wrap around.
        var newCards = new ArrayList<Flashcard>();
        for (int i = 0; i < cards.size(); i++) {
            var thisCard = cards.get(i);
            var thatCard = cards.get(Math.floorMod(i + k, cards.size()));
            newCards.add(thisCard.withSortOrder(thatCard.sortOrder()));
        }
        return newCards;
    }

    @NonNull
    public static List<Flashcard> shuffle(List<Flashcard> cards) {
// Get the current sort order of each card.
        var sortOrders = cards.stream()
                .map(Flashcard :: sortOrder)
                .collect(Collectors.toList());
            // Shuffle the sort orders.
        Collections.shuffle(sortOrders);
        // Map each card to a new card with the shuffled sort order.
        var newCards = cards.stream()
                .map(card -> card.withSortOrder(sortOrders.remove(0)))
                .collect(Collectors.toList());
        return newCards;
    }
}
