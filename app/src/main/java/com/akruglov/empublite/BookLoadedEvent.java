package com.akruglov.empublite;

/**
 * Created by akruglov on 02.03.17.
 */

public class BookLoadedEvent {

    private BookContents contents = null;

    public BookLoadedEvent(BookContents contents) {
        this.contents = contents;
    }

    public BookContents getBook() {
        return contents;
    }
}
