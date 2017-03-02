package com.akruglov.empublite;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ShareActionProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by akruglov on 02.03.17.
 */

public class NoteFragment extends Fragment implements TextWatcher {

    private static final String KEY_POSITION = "position";
    private EditText editor = null;
    private ShareActionProvider share = null;
    private Intent shareIntent = new Intent(Intent.ACTION_SEND).setType("text/plain");

    static NoteFragment newInstance(int position) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();

        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        shareIntent.putExtra(Intent.EXTRA_TEXT, s.toString());
    }

    public interface Contract {
        void closeNotes();
    }

    private Contract getContract() {
        return (Contract) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notes, menu);

        share = (ShareActionProvider) menu.findItem(R.id.share).getActionProvider();
        share.setShareIntent(shareIntent);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            editor.setText(null);
            getContract().closeNotes();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.editor, container, false);

        editor = (EditText) result.findViewById(R.id.editor);
        editor.addTextChangedListener(this);

        return result;
    }

    private int getPosition() {
        return getArguments().getInt(KEY_POSITION, -1);
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

        if (TextUtils.isEmpty(editor.getText())) {
            DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
            db.loadNote(getPosition());
        }
    }

    @Override
    public void onStop() {
        DatabaseHelper.getInstance(getActivity()).updateNote(getPosition(), editor.getText().toString());

        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoteLoaded(NoteLoadedEvent event) {
        if (event.getPosition() == getPosition()) {
            editor.setText(event.getProse());
        }
    }
}
