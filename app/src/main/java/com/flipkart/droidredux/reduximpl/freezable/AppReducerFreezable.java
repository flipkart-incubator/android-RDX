/*
 * The Apache License
 *
 * Copyright (c) 2020 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.droidredux.reduximpl.freezable;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.flipkart.droidredux.reduximpl.AppAction;
import com.flipkart.droidredux.reduximpl.actions.ChangeScreenAction;
import com.flipkart.redux.immutable.FreezableReducer;

public class AppReducerFreezable extends FreezableReducer<AppStateFreezable, AppAction> {

    public static final String DEFAULT_SCREEN = "DEFAULT";

    @Override
    protected AppStateFreezable reduceThawed(@NonNull AppStateFreezable frozenState, @NonNull AppStateFreezable thawedState, @NonNull AppAction action) {
        switch (action.getType()) {
            case ChangeScreenAction.ACTION_CHANGE_SCREEN: {
                if (action instanceof ChangeScreenAction) {
                    String newScreen = ((ChangeScreenAction) action).getScreenName();
                    thawedState.setScreenState(TextUtils.isEmpty(newScreen) ? DEFAULT_SCREEN : newScreen);
                    return thawedState;
                }
            }
        }
        return thawedState;
    }
}
