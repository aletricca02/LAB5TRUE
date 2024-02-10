package edu.ucsd.cse110.secards.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.secards.lib.domain.Flashcard;
import edu.ucsd.cse110.secards.lib.domain.FlashcardRepository;
import edu.ucsd.cse110.secards.lib.domain.Flashcards;
import edu.ucsd.cse110.secards.lib.util.Subject;

public class MainViewModel extends ViewModel {
    // Domain state (true "Model" state)
    private final FlashcardRepository flashcardRepository;

    // UI state
    private final Subject<List<Flashcard>> orderedCards;
    private final Subject<Flashcard> topCard;
    private final Subject<Boolean> isShowingFront;
    private final Subject<String> displayedText;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SECardsApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getFlashcardRepository());
                    });

    public MainViewModel(FlashcardRepository flashcardRepository) {
        this.flashcardRepository = flashcardRepository;

        // Create the observable subjects.
        this.orderedCards = new Subject<>();
        this.topCard = new Subject<>();
        this.isShowingFront = new Subject<>();
        this.displayedText = new Subject<>();

        // Initialize...
        isShowingFront.setValue(true);

        // When the list of cards changes (or is first loaded), reset the ordering.
        flashcardRepository.findAll().observe(cards -> {
            if (cards == null) return; // not ready yet, ignore

            var newOrderedCards = cards.stream()
                    .sorted(Comparator.comparingInt(Flashcard::sortOrder))
                    .collect(Collectors.toList());
            orderedCards.setValue(newOrderedCards);
        });


        // When the ordering changes, update the ordered cards.

        // When the ordering changes, update the top card.
        orderedCards.observe(cards -> {
            if (cards == null || cards.size() == 0) return;
            var card = cards.get(0);
            this.topCard.setValue(card);
        });

        // When the top card changes, update the displayed text and display the front side.
        topCard.observe(card -> {
            if (card == null) return;

            displayedText.setValue(card.front());
            isShowingFront.setValue(true);
        });

        // When isShowingFront changes, update the displayed text.
        isShowingFront.observe(isShowingFront -> {
            if (isShowingFront == null) return;

            var card = topCard.getValue();
            if (card == null) return;

            var text = isShowingFront ? card.front() : card.back();
            displayedText.setValue(text);
        });
    }

    public Subject<String> getDisplayedText() {
        return displayedText;
    }

    public Subject<List<Flashcard>> getOrderedCards() {
        return orderedCards;
    }

    public void flipTopCard() {
        var isShowingFront = this.isShowingFront.getValue();
        if (isShowingFront == null) return;
        this.isShowingFront.setValue(!isShowingFront);
    }

    public void stepForward() {
        var cards = this.orderedCards.getValue();
        if (cards == null) return;
        var newCards = Flashcards.rotate(cards, 1);
        // Update the repository with the new sort orders.
        flashcardRepository.save(newCards);
    }

    public void stepBackward() {
        var cards = this.orderedCards.getValue();
        if (cards == null) return;

        // Build mapping from old to new sort orders using Math.floorMod to wrap around.
        var newCards = Flashcards.rotate(cards, 1);

        // Update the repository with the new sort orders.
        flashcardRepository.save(newCards);
    }

    public void shuffle() {
        var cards = this.orderedCards.getValue();
        if (cards == null) return;

        var newCards = Flashcards.shuffle(cards);
        flashcardRepository.save(newCards);
    }

    public void append(Flashcard card){
        flashcardRepository.append(card);
    }

    public void prepend(Flashcard card){
        flashcardRepository.prepend(card);
    }
}
