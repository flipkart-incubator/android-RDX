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

package com.flipkart.redux.lifecycle;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.flipkart.redux.core.Action;
import com.flipkart.redux.core.State;
import com.flipkart.redux.core.Store;

public class ReduxController<S extends State, A extends Action, T extends ReduxViewModel<S, A>> {

    private final T reduxViewModel;

    @MainThread
    public ReduxController(@NonNull Class<T> reduxViewModelClass, Observer<S> observer, @NonNull FragmentActivity activity, @NonNull LifecycleOwner lifecycleOwner, boolean distinctUntilChanged) {
        reduxViewModel = ViewModelProviders.of(activity).get(reduxViewModelClass);
        reduxViewModel.subscribe(lifecycleOwner, observer, distinctUntilChanged);
    }

    @MainThread
    public ReduxController(@NonNull Class<T> reduxViewModelClass, Observer<S> observer, @NonNull Fragment fragment, @NonNull LifecycleOwner lifecycleOwner, boolean distinctUntilChanged) {
        reduxViewModel = ViewModelProviders.of(fragment).get(reduxViewModelClass);
        reduxViewModel.subscribe(lifecycleOwner, observer, distinctUntilChanged);
    }

    @MainThread
    public void restoreState(@NonNull S state) {
        if (reduxViewModel != null) {
            reduxViewModel.restoreState(state);
        }
    }

    public Store<S, A> getStore() {
        return reduxViewModel;
    }

    @MainThread
    public void dispatch(@NonNull A action) {
        reduxViewModel.dispatch(action);
    }

    @MainThread
    public void subscribe(@NonNull LifecycleOwner lifecycleOwner, @NonNull Observer<S> observer, boolean distinctUntilChanged) {
        reduxViewModel.subscribe(lifecycleOwner, observer, distinctUntilChanged);
    }

    @MainThread
    public void unsubscribe(@NonNull Observer<S> observer) {
        reduxViewModel.unsubscribe(observer);
    }
}
