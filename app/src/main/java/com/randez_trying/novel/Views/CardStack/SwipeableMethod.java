package com.randez_trying.novel.Views.CardStack;

public enum SwipeableMethod {
    AutomaticAndManual,
    Automatic,
    Manual,
    None;

    boolean canSwipe() {
        return canSwipeAutomatically() || canSwipeManually();
    }

    boolean canSwipeAutomatically() {
        return this == AutomaticAndManual || this == Automatic;
    }

    boolean canSwipeManually() {
        return this == AutomaticAndManual || this == Manual;
    }
}