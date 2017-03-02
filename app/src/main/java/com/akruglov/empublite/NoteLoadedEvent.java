package com.akruglov.empublite;

/**
 * Created by akruglov on 02.03.17.
 */

class NoteLoadedEvent {

    int position;
    String prose;

    NoteLoadedEvent(int position, String prose) {
        this.position = position;
        this.prose = prose;
    }

    int getPosition() {
        return position;
    }

    String getProse() {
        return prose;
    }
}
