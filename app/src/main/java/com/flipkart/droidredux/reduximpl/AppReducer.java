package com.flipkart.droidredux.reduximpl;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.flipkart.droidredux.reduximpl.actions.ChangeScreenAction;
import com.flipkart.redux.core.Reducer;

public class AppReducer implements Reducer<AppState, AppAction> {
    @NonNull
    @Override
    public AppState reduce(@NonNull AppState oldState, @NonNull AppAction action) {
        AppState newState = new AppState();
        newState.sync(oldState);

        switch (action.getType()) {
            case ChangeScreenAction.ACTION_CHANGE_SCREEN: {
                if (action instanceof ChangeScreenAction) {
                    String newScreen = ((ChangeScreenAction) action).getScreenName();
                    newState.setScreenState(TextUtils.isEmpty(newScreen) ? "DEFAULT_SCREEN" : newScreen);
                    return newState;
                }
            }
        }
        return newState;
    }
}
