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

import android.os.Build;

import com.flipkart.redux.util.DispatchProcessor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.O)
public class ReduxStoreTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private ReduxStore<State, Action> reduxStore;

    @Mock
    Reducer<State, Action> reducer;

    @Mock
    StateSubscriber<State> stateSubscriber;

    @Mock
    DispatchProcessor<State, Action> dispatchProcessor;

    @Mock
    State state;

    @Mock
    Action action;

    @Before
    public void setUp() {
        reduxStore = new ReduxStore<>(state, reducer);
        reduxStore.setDispatchProcessor(dispatchProcessor);
    }

    @Before
    public void setUpMockito() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void subscribeNew() {
        Assert.assertTrue(reduxStore.subscribe(stateSubscriber));
        Mockito.verify(stateSubscriber, Mockito.times(1)).updateState(state);
    }

    @Test
    public void subscribeExisting() {
        reduxStore.subscribe(stateSubscriber);
        Assert.assertFalse(reduxStore.subscribe(stateSubscriber));
    }

    @Test
    public void unsubscribeValid() {
        reduxStore.subscribe(stateSubscriber);
        Assert.assertTrue(reduxStore.unsubscribe(stateSubscriber));
    }
    @Test
    public void unsubscribeInvalid() {
        Assert.assertFalse(reduxStore.unsubscribe(stateSubscriber));
    }

    @Test
    public void dispatch() {
        reduxStore.dispatch(action);

        Mockito.verify(dispatchProcessor, Mockito.times(1)).dispatch(action);
    }

    @Test
    public void dispatchToReducer() {
        Mockito.when(reducer.reduce(Mockito.any(State.class), Mockito.any(Action.class))).thenReturn(state);

        reduxStore.replaceReducer(reducer);
        reduxStore.dispatchToReducer(action);

        Mockito.verify(reducer, Mockito.times(1)).reduce(state, action);
        Mockito.verify(dispatchProcessor, Mockito.times(1)).setNewState(state);
    }


    @Test
    public void restoreState() {
        State next = (nextState) -> {
        };
        reduxStore.subscribe(stateSubscriber);
        reduxStore.restoreState(next);
        Assert.assertEquals(reduxStore.getState(), next);
        Mockito.verify(stateSubscriber, Mockito.times(1)).updateState(next);
    }

    @Test
    public void destroy() {
        reduxStore.destroy();
        Mockito.verify(dispatchProcessor, Mockito.times(1)).destroy();
    }

    @After
    public void tearDown() {
        reduxStore = null;
    }

    @After
    public void tearDownMockito() {
        Mockito.validateMockitoUsage();
    }


    @Test
    @Ignore("Getter.")
    public void getState() {
    }

    @Test
    @Ignore("Setter.")
    public void replaceReducer() {
    }

}