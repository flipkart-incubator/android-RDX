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

package com.flipkart.redux.immutable;

import androidx.annotation.NonNull;

import com.flipkart.redux.core.Action;
import com.flipkart.redux.core.Reducer;

public abstract class FreezableReducer<S extends FreezableState<S>, A extends Action> implements Reducer<S, A> {

    private StateManager<S> stateManager;

    public FreezableReducer() {
        stateManager = new StateManager<>();
    }

    @NonNull
    @Override
    public S reduce(@NonNull S frozenState, @NonNull A action) {
        S thawedState = stateManager.getState(frozenState);
        thawedState = reduceThawed(frozenState, thawedState, action);
        return thawedState.freeze();
    }

    protected abstract S reduceThawed(@NonNull S frozenState, @NonNull S thawedState, @NonNull A action);

}
