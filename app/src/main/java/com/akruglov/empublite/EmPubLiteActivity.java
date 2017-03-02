package com.akruglov.empublite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.karim.MaterialTabs;

public class EmPubLiteActivity extends Activity {

    private static final String MODEL = "model";
    private static final String PREF_LAST_POSITION = "lastPosition";
    private static final String PREF_SAVE_LAST_POSITION = "saveLastPosition";
    private static final String PREF_KEEP_SCREEN_ON = "keepScreenOn";

    private ViewPager pager;
    private ContentsAdapter adapter;
    private ModelFragment modelFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setupStrictMode();
        pager = (ViewPager) findViewById(R.id.pager);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookLoader(BookLoadedEvent event) {
        setupPager(event.getBook());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent i = new Intent(this, SimpleContentActivity.class);
                i.putExtra(SimpleContentActivity.EXTRA_FILE,
                        "file:///android_asset/misc/about.html");
                startActivity(i);
                return true;

            case R.id.help:
                i = new Intent(this, SimpleContentActivity.class);
                i.putExtra(SimpleContentActivity.EXTRA_FILE,
                        "file:///android_asset/misc/help.html");
                startActivity(i);
                return true;

            case R.id.settings:
                startActivity(new Intent(this, Preferences.class));
                return true;

            case R.id.notes:
                startActivity(new Intent(this, NoteActivity.class)
                    .putExtra(NoteActivity.EXTRA_POSITION, pager.getCurrentItem()));
                return true;

            case R.id.update:
                startService(new Intent(this, DownloadCheckService.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        if (adapter == null) {
            modelFragment = (ModelFragment) getFragmentManager().findFragmentByTag(MODEL);

            if (modelFragment == null) {
                modelFragment = new ModelFragment();
                getFragmentManager().beginTransaction().add(new ModelFragment(), MODEL).commit();
            } else if (modelFragment.getBook() != null) {
                setupPager(modelFragment.getBook());
            }
        }

        if (modelFragment.getPreferences() != null) {
            pager.setKeepScreenOn(modelFragment.getPreferences().getBoolean(PREF_KEEP_SCREEN_ON, false));
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);

        if (modelFragment.getPreferences() != null) {
            int position = pager.getCurrentItem();

            modelFragment.getPreferences().edit().putInt(PREF_LAST_POSITION, position);
        }

        super.onStop();
    }

    private void setupPager(BookContents contents) {
        adapter = new ContentsAdapter(this, contents);
        pager.setAdapter(adapter);

        MaterialTabs tabs = (MaterialTabs) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        SharedPreferences preferences = modelFragment.getPreferences();

        if (preferences != null) {
            if (preferences.getBoolean(PREF_SAVE_LAST_POSITION, false)) {
                pager.setCurrentItem(preferences.getInt(PREF_LAST_POSITION, 0));
            }

            pager.setKeepScreenOn(preferences.getBoolean(PREF_KEEP_SCREEN_ON, false));
        }
    }

    private void setupStrictMode() {
        StrictMode.ThreadPolicy.Builder builder =
                new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog();

        if (BuildConfig.DEBUG) {
            builder.penaltyFlashScreen();
        }

        StrictMode.setThreadPolicy(builder.build());
    }
}
