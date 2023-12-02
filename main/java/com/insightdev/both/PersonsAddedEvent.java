package com.insightdev.both;

public class PersonsAddedEvent {
    private final boolean nextPage;

    public PersonsAddedEvent(boolean nextPage) {
        this.nextPage = nextPage;
    }

    public boolean isNextPage() {
        return nextPage;
    }
}
