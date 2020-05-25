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

import androidx.annotation.NonNull;
import android.util.Log;

import com.flipkart.droidredux.reduximpl.actions.ChangeScreenAction;
import com.flipkart.redux.core.Dispatcher;
import com.flipkart.redux.core.Middleware;
import com.flipkart.redux.core.Store;

public class LoggingMiddleware implements Middleware<AppState, AppAction> {

    private final int id;

    LoggingMiddleware(int id) {
        this.id = id;
    }

    @Override
    public void dispatch(@NonNull AppAction action, @NonNull Store<AppState, AppAction> store, @NonNull Dispatcher<AppAction> next) {
        if (action instanceof ChangeScreenAction) {
            Log.d("TEST_MW", "LOGGING ID: " + id + " The current action type is: " + action.getType() + " Screen: " + ((ChangeScreenAction) action).getScreenName());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        next.dispatch(action);
    }
}
