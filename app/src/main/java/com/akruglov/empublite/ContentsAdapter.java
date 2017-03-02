package com.akruglov.empublite;

import android.app.Activity;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Created by akruglov on 02.03.17.
 */

public class ContentsAdapter extends FragmentStatePagerAdapter {

    final BookContents contents;

    public ContentsAdapter(Activity context, BookContents contents) {
        super(context.getFragmentManager());

        this.contents = contents;
    }

    @Override
    public Fragment getItem(int position) {
        String path = contents.getChapterFile(position);

        return SimpleContentFragment.newInstance("file:///android_asset/book/" + path);
    }

    @Override
    public int getCount() {
        return contents.getChapterCount();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return contents.getChapterTitle(position);
    }
}
