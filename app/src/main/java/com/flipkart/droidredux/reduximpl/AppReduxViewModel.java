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

package com.flipkart.droidredux.reduximpl;

import android.app.Application;
import androidx.annotation.NonNull;

import com.flipkart.redux.core.ReduxStore;
import com.flipkart.redux.lifecycle.ReduxViewModel;

public class AppReduxViewModel extends ReduxViewModel<AppState, AppAction> {

    //Todo: Use DB instead of shared preferences for the example.
    private static final String PREFERENCES = "REDUX_PREF";

    public AppReduxViewModel(@NonNull Application applicationContext) {
        super(applicationContext);
    }

    @NonNull
    @Override
    protected ReduxStore<AppState, AppAction> initializeStore() {
        AppState initialState = new AppState();
        initialState.setScreenState("DEFAULT");
        return new ReduxStore<>(initialState, new AppReducer(), new LoggingMiddleware(11), new LoggingMiddleware(22), new LoggingMiddleware(33));
    }
}
