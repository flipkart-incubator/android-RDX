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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flipkart.redux.immutable.FreezableState;

import java.io.Serializable;

public class AppStateFreezable implements FreezableState<AppStateFreezable>, Serializable {

    @Nullable
    private String screenState;

    private volatile boolean isFrozen = false;

    @Nullable
    public String getScreenState() {
        return screenState;
    }

    public void setScreenState(@Nullable String screenState) {
        checkIfFrozen();
        this.screenState = screenState;
    }

    private void checkIfFrozen() {
        if (isFrozen()) {
            throw new UnsupportedOperationException("Cannot modify frozen object!");
        }
    }

    @Override
    public void sync(@NonNull AppStateFreezable currentState) {
        isFrozen = false;
        this.screenState = currentState.screenState;
    }

    @Override
    public boolean isFrozen() {
        return isFrozen;
    }

    @Override
    public AppStateFreezable freeze() {
        isFrozen = true;
        return this;
    }

    @Override
    public AppStateFreezable cloneAsThawed() {
        AppStateFreezable state = new AppStateFreezable();
        state.sync(this);
        return state;
    }
}
