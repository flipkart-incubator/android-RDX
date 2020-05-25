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

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.flipkart.redux.core.Action;
import com.flipkart.redux.core.Reducer;
import com.flipkart.redux.core.ReduxStore;
import com.flipkart.redux.core.State;
import com.flipkart.redux.core.StateSubscriber;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.O)
public class ReduxViewModelTest {

    private ReduxViewModel<State, Action> reduxViewModel;

    private LifecycleOwner lifecycleOwner;

    @Mock
    ReduxStore<State, Action> store;

    @Mock
    Observer<State> stateObserver;

    @Mock
    private MutableLiveData<State> mutableLiveData;

    @Mock
    private MediatorLiveData<State> distinctLiveData;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void setUpMockito() {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void setup() {
        store.subscribe(mutableLiveData::postValue);
        reduxViewModel = new ReduxViewModel<State, Action>(ApplicationProvider.getApplicationContext()) {
            @NonNull
            @Override
            protected ReduxStore<State, Action> initializeStore() {
                return store;
            }
        };
        reduxViewModel.setMutableStateLiveData(mutableLiveData);
        reduxViewModel.setDistinctLiveData(distinctLiveData);
        reduxViewModel.setSubscriber(mutableLiveData::postValue);
        lifecycleOwner = () -> new Lifecycle() {
            @Override
            public void addObserver(@NonNull LifecycleObserver observer) {

            }

            @Override
            public void removeObserver(@NonNull LifecycleObserver observer) {

            }

            @NonNull
            @Override
            public State getCurrentState() {
                return State.RESUMED;
            }
        };
    }

    @Test
    @Ignore("Abstract method.")
    public void initializeStore() {
    }

    @Test
    public void subscribeDistinctUpdates() {
        reduxViewModel.subscribe(lifecycleOwner, stateObserver, true);
        Mockito.verify(distinctLiveData, Mockito.times(1)).observe(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void subscribeAllUpdates() {
        reduxViewModel.subscribe(lifecycleOwner, stateObserver, false);
        Mockito.verify(mutableLiveData, Mockito.times(1)).observe(lifecycleOwner, stateObserver);
    }

    @Test
    public void unsubscribe() {
        reduxViewModel.unsubscribe(stateObserver);
        Mockito.verify(mutableLiveData, Mockito.times(1)).removeObserver(stateObserver);
    }

    @Test
    public void dispatch() {
        Action action = () -> "STUB";
        reduxViewModel.dispatch(action);
        Mockito.verify(store, Mockito.times(1)).dispatch(action);
    }

    @Test
    public void replaceReducer() {
        Reducer<State, Action> reducer = (state, action) -> state;
        reduxViewModel.replaceReducer(reducer);
        Mockito.verify(store, Mockito.times(1)).replaceReducer(reducer);
    }

    @Test
    public void getState() {
        reduxViewModel.getState();
        //noinspection ResultOfMethodCallIgnored
        Mockito.verify(store, Mockito.times(1)).getState();
    }

    @Test
    public void onCleared() {
        reduxViewModel.subscribe(lifecycleOwner, stateObserver, false);
        reduxViewModel.onCleared();
        //noinspection unchecked
        Mockito.verify(store, Mockito.times(1)).unsubscribe(ArgumentMatchers.any(StateSubscriber.class));
        Mockito.verify(store, Mockito.times(1)).destroy();
    }

    @Test
    public void restoreState() {
        State state = currentState -> {
        };
        reduxViewModel.restoreState(state);
        Mockito.verify(store, Mockito.times(1)).restoreState(state);
    }

    @After
    public void tearDownMockito() {
        Mockito.validateMockitoUsage();
    }
}