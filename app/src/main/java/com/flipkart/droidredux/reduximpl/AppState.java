package com.flipkart.droidredux.reduximpl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flipkart.redux.core.State;

import java.io.Serializable;
import java.util.Objects;

public class AppState implements State<AppState>, Serializable {

    @Nullable
    private String screenState;

    @Nullable
    public String getScreenState() {
        return screenState;
    }

    public void setScreenState(@Nullable String screenState) {
        this.screenState = screenState;
    }

    @Override
    public void sync(@NonNull AppState currentState) {
        this.screenState = currentState.getScreenState();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppState)) return false;
        AppState appState = (AppState) o;
        return Objects.equals(getScreenState(), appState.getScreenState());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getScreenState());
    }
}
