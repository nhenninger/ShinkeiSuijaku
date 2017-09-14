package net.nathanhenninger.android.shinkeisuijaku;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Nathan Henninger on 2017.07.19.
 * https://github.com/nhenninger
 * nathanhenninger@u.boisestate.edu
 */

/**
 * Analagous to PhotoGalleryFragment in PhotoGallery.
 */
public class ShinkeiSuijakuFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "ShinkeiSuijakuFragment";
    private static final int FIRST_LESSON = 1;
    private static final int DEFAULT_NUM_COLUMNS = 3;

    /**
     * The total deck of cards -- two of each kind.
     */
    private ArrayList<Card> mCards = new ArrayList<>();
    /**
     * The two cards compared for a match.
     */
    private ArrayList<CardHolder> mActiveCardHolders = new ArrayList<>(2);
    private RecyclerView mRecyclerView;

    private boolean mShowLatinText;
    /**
     * Number of pairs the player has made.
     */
    private int mMatches;
    private int mLessonNumber = FIRST_LESSON;
    private int mNumColumns = DEFAULT_NUM_COLUMNS;
    private String mOrientation = "";

    public static ShinkeiSuijakuFragment newInstance() {
        return new ShinkeiSuijakuFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        setupSharedPreferences();
        updateItems();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shinkei_suijaku, container, false);
        mRecyclerView = view.findViewById(R.id.fragment_shinkei_suijaku_recycler_view);
        setupLayoutManager(mNumColumns);

        setupRecyclerViewCacheSize(mRecyclerView, mCards.size());
        setupOrientation(mOrientation);
        setupAdapter();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_latin:
                mShowLatinText = !mShowLatinText;
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putBoolean(getString(
                                R.string.pref_display_latin_text_key), mShowLatinText)
                        .apply();
                return true;
            case R.id.action_settings:
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.action_reset:
                resetBoard();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }

    @Override
    public void onPause() {
        super.onPause();
        // TODO: remove this if/when able to retain status of flipped cards.
        mActiveCardHolders.clear();
        mMatches = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Shuffle ze cards.
     */
    private void shuffleCards() {
        Collections.shuffle(mCards);
    }

    /**
     * Iterates through the board and toggles display of the Latin text.
     *
     * @param toggle If true, display the Latin text.
     */
    private void toggleAllLatinText(boolean toggle) {
        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
            CardHolder holder = (CardHolder) mRecyclerView
                    .findViewHolderForAdapterPosition(i);
            holder.toggleLatinText(toggle);
        }
    }

    /**
     * Determines whether two ViewHolders hold matching cards.
     *
     * @param holder The most recent clicked ViewHolder.
     */
    private void checkForMatch(CardHolder holder) {
        mActiveCardHolders.add(holder);
        holder.mCardView.setClickable(false);
        if (mActiveCardHolders.size() == 1) {
            // Do nothing~!
            return;
        } else if (mActiveCardHolders.size() == 2) {
            if (mActiveCardHolders.get(0).mTvCharacter.getText().toString().equals(
                    mActiveCardHolders.get(1).mTvCharacter.getText().toString())) {
                mMatches++;
                checkForWin();
            } else {
                for (int i = 0; i < mActiveCardHolders.size(); i++) {
                    mActiveCardHolders.get(i).flip();
                }
            }
            mActiveCardHolders.clear();
        }
    }

    /**
     * Checks if player has made all matches and if so displays a
     * congratulatory AlertDialog before resetting the board.
     */
    private void checkForWin() {
        if (mMatches == mCards.size() / 2) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.congratulations)
                    .setMessage(R.string.notice_reset_board)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            resetBoard();
                        }
                    })
                    .create()
                    .show();
        }
    }

    /**
     * Initiates the FetchLessonsTask with the current lesson.
     */
    private void updateItems() {
        new FetchLessonsTask(mLessonNumber).execute();
    }

    /**
     * Initializes member variables to stored preference values and registers
     * this fragment as an OnSharedPreferenceChangeListener.
     */
    private void setupSharedPreferences() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        mLessonNumber = Integer.parseInt(preferences
                .getString(getString(R.string.pref_lesson_key),
                        getString(R.string.pref_lesson_default)));
        mNumColumns = Integer.parseInt(preferences
                .getString(getString(R.string.pref_number_of_columns_key),
                        getString(R.string.pref_number_of_columns_default)));
        mShowLatinText = preferences
                .getBoolean(getString(R.string.pref_display_latin_text_key),
                        getResources().getBoolean(R.bool.pref_display_latin_text_default));
        mOrientation = preferences
                .getString(getString(R.string.pref_orientation_key),
                        getString(R.string.pref_orientation_default));
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Assigns the RecyclerView's LayoutManager.
     *
     * @param numColumns The number of columns for the GridLayoutManager.
     */
    private void setupLayoutManager(int numColumns) {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                numColumns));
    }

    /**
     * Locks and unlocks the screen orientation to portrait or landscape.
     *
     * @param str Either "portrait" or "landscape" to lock the screen.  Anything
     *            else will unlock it.
     */
    private void setupOrientation(String str) {
        if (str.equals(getString(R.string.pref_orientation_value_portrait))) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (str.equals(getString(R.string.pref_orientation_value_landscape))) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    /**
     * Assigns the RecyclerView's adapter to a new CardAdapter.
     */
    private void setupAdapter() {
        if (isAdded()) {
            mRecyclerView.setAdapter(new CardAdapter(mCards));
        }
    }

    /**
     * Assigns the passed RecyclerView's cache size to the passed integer.
     *
     * @param rv   The RecyclerView to modify.
     * @param size The new size of the ItemViewCacheSize.
     */
    private void setupRecyclerViewCacheSize(RecyclerView rv, int size) {
        rv.setItemViewCacheSize(size);
    }

    /**
     * Reset the game board.
     */
    private void resetBoard() {
        mActiveCardHolders.clear();
        mMatches = 0;
        shuffleCards();
        setupAdapter();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.pref_display_latin_text_key))) {
            mShowLatinText = sharedPreferences.getBoolean(
                    getString(R.string.pref_display_latin_text_key),
                    getResources().getBoolean(R.bool.pref_display_latin_text_default)
            );
            toggleAllLatinText(mShowLatinText);
        } else if (s.equals(getString(R.string.pref_lesson_key))) {
            mLessonNumber = Integer.parseInt(sharedPreferences
                    .getString(getString(R.string.pref_lesson_key),
                            getString(R.string.pref_lesson_default)));
            mActiveCardHolders.clear();
            updateItems();
        } else if (s.equals(getString(R.string.pref_number_of_columns_key))) {
            mNumColumns = Integer.parseInt(sharedPreferences
                    .getString(getString(R.string.pref_number_of_columns_key),
                            getString(R.string.pref_number_of_columns_default)));
            setupLayoutManager(mNumColumns);
            setupAdapter();
        } else if (s.equals(getString(R.string.pref_orientation_key))) {
            mOrientation = sharedPreferences.getString(getString(R.string.pref_orientation_key),
                    getString(R.string.pref_orientation_default));
            setupOrientation(mOrientation);
        }
    }

    /**
     * Analagous to PhotoHolder in PhotoGallery.
     */
    private class CardHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private Card mCard;
        private CardView mCardView;
        private TextView mCardFront;
        private ConstraintLayout mCardBack;
        private TextView mTvCharacter;
        private TextView mTvLatinText;
        private AnimatorSet mFrontToBackStart;
        private AnimatorSet mFrontToBackEnd;
        private AnimatorSet mBackToFrontStart;
        private AnimatorSet mBackToFrontEnd;

        private boolean mShowingBack = false;

        public CardHolder(View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.cv_card);
            mCardFront = itemView.findViewById(R.id.tv_card_front);
            mCardBack = itemView.findViewById(R.id.cl_card_back);
            mTvCharacter = itemView.findViewById(R.id.tv_card_back_character);
            mTvLatinText = itemView.findViewById(R.id.tv_card_back_latin_text);
            itemView.setOnClickListener(this);

            mFrontToBackStart = (AnimatorSet) AnimatorInflater
                    .loadAnimator(getActivity(), R.animator.card_flip_left_out);
            mFrontToBackStart.setTarget(mCardView);
            mFrontToBackEnd = (AnimatorSet) AnimatorInflater
                    .loadAnimator(getActivity(), R.animator.card_flip_left_in);
            mFrontToBackEnd.setTarget(mCardView);
            mFrontToBackStart.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    mCardFront.setVisibility(View.GONE);
                    mCardBack.setVisibility(View.VISIBLE);
                    mFrontToBackEnd.start();
                }
            });
            mBackToFrontStart = (AnimatorSet) AnimatorInflater
                    .loadAnimator(getActivity(), R.animator.card_flip_right_out);
            mBackToFrontStart.setStartDelay(1000);
            mBackToFrontStart.setTarget(mCardView);
            mBackToFrontEnd = (AnimatorSet) AnimatorInflater
                    .loadAnimator(getActivity(), R.animator.card_flip_right_in);
            mBackToFrontEnd.setTarget(mCardView);
            mBackToFrontStart.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCardFront.setVisibility(View.VISIBLE);
                    mCardBack.setVisibility(View.GONE);
                    mBackToFrontEnd.start();
                }
            });
        }

        /**
         * Binds the card to this ViewHolder.
         *
         * @param card A Card object.
         */
        public void bindCard(Card card) {
            mCard = card;
            if (mCard.getKana() != null) { // Lessons 1 and 2
                mTvCharacter.setText(mCard.getKana());
                mTvLatinText.setText(mCard.getPronunciation());
//                Log.i(TAG, "bindCard: " + mCard.getKana() + ", " + mCard.getPronunciation());
            } else { // Lessons 3 to 23
                mTvCharacter.setText(mCard.getCharacter());
                mTvLatinText.setText(mCard.getMeaning());
            }
            toggleLatinText(mShowLatinText);
        }

        @Override
        public void onClick(View view) {
//            Log.i(TAG, "onClick: clicked");
            flip();
            checkForMatch(this);
        }

        /**
         * Begins the flip animation in either direction.
         */
        public void flip() {
            if (mShowingBack) {
                mBackToFrontStart.start();
                mCardView.setClickable(true);
            } else {
                mFrontToBackStart.start();
                mCardView.setClickable(false);
            }
            mShowingBack = !mShowingBack;
        }

        /**
         * Toggles the display of Latin text on the card.
         *
         * @param toggle True to display Latin text.
         */
        public void toggleLatinText(boolean toggle) {
            if (toggle) {
                mTvLatinText.setVisibility(View.VISIBLE);
            } else {
                mTvLatinText.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Analagous to PhotoAdapter in PhotoGallery.
     */
    private class CardAdapter extends RecyclerView.Adapter<CardHolder> {
        private ArrayList<Card> mAdapterCards;

        public CardAdapter(ArrayList<Card> cards) {
            mAdapterCards = cards;
        }

        @Override
        public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.card, parent, false);
            return new CardHolder(view);
        }

        @Override
        public void onBindViewHolder(CardHolder holder, int position) {
            holder.bindCard(mAdapterCards.get(position));
        }

        @Override
        public int getItemCount() {
//            Log.i(TAG, "getItemCount: " + mAdapterCards.size());
            return mAdapterCards.size();
        }
    }

    /**
     * Analagous to FetchItemsTask in PhotoGallery.
     */
    private class FetchLessonsTask extends AsyncTask<Void, Void, Lesson> {
        private int mFetchNumber;

        public FetchLessonsTask(int i) {
            mFetchNumber = i;
        }

        @Override
        protected Lesson doInBackground(Void... voids) {
            return new CardCollector(getActivity()).getLesson(mFetchNumber);
        }

        @Override
        protected void onPostExecute(Lesson lesson) {
            mCards = lesson.getCards();
            //noinspection CollectionAddedToSelf
            mCards.addAll(mCards);
            setupRecyclerViewCacheSize(mRecyclerView, mCards.size());
            shuffleCards();
            setupAdapter();
        }
    }
}
