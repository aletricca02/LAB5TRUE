package edu.ucsd.cse110.secards.app.ui.cardlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.secards.app.databinding.ListItemCardBinding;
import edu.ucsd.cse110.secards.lib.domain.Flashcard;

public class CardListAdapter extends ArrayAdapter<Flashcard> {
    public CardListAdapter(Context context, List<Flashcard> flashcards) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(flashcards));
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the flashcard for this position.
        var flashcard = getItem(position);
        assert flashcard != null;

        // Check if a view is being reused...
        ListItemCardBinding binding;
        if (convertView != null) {
            // if so, bind to it
            binding = ListItemCardBinding.bind(convertView);
        } else {
            // otherwise inflate a new view from our layout XML.
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemCardBinding.inflate(layoutInflater, parent, false);
        }

        // Populate the view with the flashcard's data.
        binding.cardFrontText.setText(flashcard.front());
        binding.cardBackText.setText(flashcard.back());

        return binding.getRoot();
    }

    // The below methods aren't strictly necessary, usually.
    // But get in the habit of defining them because they never hurt
    // (as long as you have IDs for each item) and sometimes you need them.

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        var flashcard = getItem(position);
        assert flashcard != null;

        var id = flashcard.id();
        assert id != null;

        return id;
    }
}
