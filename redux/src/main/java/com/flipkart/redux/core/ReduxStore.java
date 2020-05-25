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

package com.flipkart.redux.core;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;

import com.flipkart.redux.util.DispatchManager;
import com.flipkart.redux.util.DispatchProcessor;
import com.flipkart.redux.util.HandlerProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ReduxStore<S extends State, A extends Action> implements Store<S, A> {

    @NonNull
    private S state;

    @Nullable
    private Reducer<S, A> rootReducer;

    @NonNull
    private final Set<StateSubscriber<S>> subscribers;

    @Nullable
    private DispatchProcessor<S, A> dispatchProcessor;

    @SafeVarargs
    public ReduxStore(@NonNull S initialState, @NonNull Reducer<S, A> reducer, @Nullable Middleware<S, A>... middlewareList) {
        state = initialState;
        rootReducer = reducer;
        subscribers = Collections.synchronizedSet(new HashSet<>());
        setDispatchProcessor(new DispatchProcessor<>(this, new DispatchManager<>(this::dispatchToReducer, middlewareList), this::notifyStateUpdate, new HandlerProvider()));
    }

    @AnyThread
    public boolean subscribe(@NonNull StateSubscriber<S> stateSubscriber) {
        synchronized (subscribers) {
            boolean isAdded;
            isAdded = subscribers.add(stateSubscriber);
            stateSubscriber.updateState(state);
            return isAdded;
        }
    }

    @AnyThread
    public boolean unsubscribe(@NonNull StateSubscriber<S> stateSubscriber) {
        synchronized (subscribers) {
            return subscribers.remove(stateSubscriber);
        }
    }

    @MainThread
    @Override
    public void dispatch(@NonNull final A action) {
        if (dispatchProcessor != null) {
            dispatchProcessor.dispatch(action);
        }
    }

    @NonNull
    @Override
    public S getState() {
        return state;
    }

    @Override
    public void replaceReducer(@NonNull Reducer<S, A> nextReducer) {
        this.rootReducer = nextReducer;
    }

    @MainThread
    public void restoreState(@NonNull S nextState) {
        notifyStateUpdate(nextState);
    }

    @WorkerThread
    protected void dispatchToReducer(@NonNull A action) {
        if (rootReducer != null) {
            state = rootReducer.reduce(state, action);
            if (dispatchProcessor != null) {
                dispatchProcessor.setNewState(state);
            }
        }
    }

    @MainThread
    private void notifyStateUpdate(@NonNull S nextState) {
        state = nextState;
        synchronized (subscribers) {
            for (StateSubscriber<S> subscriber : subscribers) {
                subscriber.updateState(state);
            }
        }
    }

    @AnyThread
    public void destroy() {
        if (dispatchProcessor != null) {
            dispatchProcessor.destroy();
            dispatchProcessor = null;
        }
        rootReducer = null;
    }

    @VisibleForTesting
    void setDispatchProcessor(@Nullable DispatchProcessor<S, A> dispatchProcessor) {
        this.dispatchProcessor = dispatchProcessor;
    }

    public interface StateUpdateCallback<S extends State> {
        void notifyStateUpdate(@NonNull S state);
    }
}
