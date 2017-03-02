package com.akruglov.empublite;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

import static android.R.attr.host;

/**
 * Created by akruglov on 02.03.17.
 */

public class ModelFragment extends Fragment {

    final private AtomicReference<BookContents> contents = new AtomicReference<>();
    final private AtomicReference<SharedPreferences> preferences = new AtomicReference<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        EventBus.getDefault().register(this);

        if (contents.get() == null) {
            new LoadThread(context).start();
        }
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);

        super.onDetach();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBookUpdated(BookUpdatedEvent event) {
        if (getActivity() != null) {
            new LoadThread(getActivity()).start();
        }
    }

    public BookContents getBook() {
        return contents.get();
    }

    public SharedPreferences getPreferences() {
        return preferences.get();
    }

    private class LoadThread extends Thread {

        final private Context context;

        LoadThread(Context context) {
            super();

            this.context = context.getApplicationContext();
        }

        @Override
        public void run() {
            preferences.set(PreferenceManager.getDefaultSharedPreferences(context));

            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            Gson gson = new Gson();
            File baseDir = new File(context.getFilesDir(), DownloadCheckService.UPDATE_BASEDIR);

            try {
                InputStream is;

                if (baseDir.exists()) {
                    is = new FileInputStream(new File(baseDir, "contents.json"));
                } else {
                    is = context.getAssets().open("book/contents.json");
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                contents.set(gson.fromJson(reader, BookContents.class));

                if (baseDir.exists()) {
                    contents.get().setBaseDir(baseDir);
                }

                EventBus.getDefault().post(new BookLoadedEvent(getBook()));

            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Exception pasrsing JSON", e);
            }
        }
    }
}
