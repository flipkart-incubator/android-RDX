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

import android.app.Application;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.flipkart.redux.core.Action;
import com.flipkart.redux.core.Reducer;
import com.flipkart.redux.core.ReduxStore;
import com.flipkart.redux.core.State;
import com.flipkart.redux.core.StateSubscriber;
import com.flipkart.redux.core.Store;

public abstract class ReduxViewModel<S extends State, A extends Action> extends AndroidViewModel implements Store<S, A> {

    private MutableLiveData<S> mutableStateLiveData;

    private LiveData<S> distinctLiveData;

    private ReduxStore<S, A> store;

    private StateSubscriber<S> subscriber;

    public ReduxViewModel(@NonNull Application applicationContext) {
        super(applicationContext);
        setMutableStateLiveData(new MutableLiveData<>());
        distinctLiveData = Transformations.distinctUntilChanged(mutableStateLiveData);
        store = initializeStore();
        setSubscriber(mutableStateLiveData::setValue);
        store.subscribe(subscriber);
    }

    @NonNull
    protected abstract ReduxStore<S, A> initializeStore();

    @MainThread
    public void subscribe(@NonNull LifecycleOwner lifecycleOwner, Observer<S> observer, boolean distinctUntilChanged) {
        if (distinctUntilChanged) {
            distinctLiveData.observe(lifecycleOwner, observer);
        } else {
            mutableStateLiveData.observe(lifecycleOwner, observer);
        }
    }

    @MainThread
    public void unsubscribe(Observer<S> observer) {
        mutableStateLiveData.removeObserver(observer);
        if(distinctLiveData != null) {
            distinctLiveData.removeObserver(observer);
        }
    }

    @MainThread
    @Override
    public void dispatch(@NonNull A action) {
        store.dispatch(action);
    }

    @Override
    public void replaceReducer(@NonNull Reducer<S, A> nextReducer) {
        store.replaceReducer(nextReducer);
    }

    @NonNull
    @Override
    public S getState() {
        return store.getState();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        store.unsubscribe(subscriber);
        store.destroy();
    }

    @MainThread
    public void restoreState(@NonNull S state) {
        store.restoreState(state);
    }

    @VisibleForTesting
    void setMutableStateLiveData(MutableLiveData<S> mutableStateLiveData) {
        this.mutableStateLiveData = mutableStateLiveData;
    }

    @VisibleForTesting
    void setDistinctLiveData(@Nullable LiveData<S> distinctLiveData) {
        this.distinctLiveData = distinctLiveData;
    }

    @VisibleForTesting
    void setSubscriber(StateSubscriber<S> subscriber) {
        this.subscriber = subscriber;
    }
}
