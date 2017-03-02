package com.akruglov.empublite;

import java.util.List;

/**
 * Created by akruglov on 02.03.17.
 */

public class BookContents {

    List<Chapter> chapters;

    int getChapterCount() {
        return chapters.size();
    }

    String getChapterFile(int position) {
        return chapters.get(position).file;
    }

    String getChapterTitle(int position) {
        return chapters.get(position).title;
    }

    static class Chapter {
        String file;
        String title;
    }
}
