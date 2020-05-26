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

package com.flipkart.redux.util;

import android.os.Build;

import com.flipkart.redux.core.Action;
import com.flipkart.redux.core.ReduxStore;
import com.flipkart.redux.core.State;
import com.flipkart.redux.core.Store;

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
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.O)
public class DispatchProcessorTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    DispatchProcessor<State, Action> dispatchProcessor;

    @Mock
    Store<State, Action> store;
    @Mock
    DispatchManager<State, Action> dispatchManager;
    @Mock
    ReduxStore.StateUpdateCallback<State> stateUpdateCallback;

    private HandlerProvider handlerProvider = new HandlerProvider();

    @Before
    public void setUp() {
        dispatchProcessor = new DispatchProcessor<>(store, dispatchManager, stateUpdateCallback, handlerProvider);
    }

    @Before
    public void setUpMockito() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void dispatch() {
        Action action = () -> "STUB";
        ShadowLooper shadowLooper = Shadows.shadowOf(dispatchProcessor.getDispatchThreadHandler().getLooper());
        dispatchProcessor.dispatch(action);
        shadowLooper.runOneTask();
        Mockito.verify(dispatchManager, Mockito.times(1)).handleDispatch(store, action);
    }

    @Test
    public void setNewState() {
        State state = (oldState) -> {
        };
        ShadowLooper shadowLooper = Shadows.shadowOf(dispatchProcessor.getMainThreadHandler().getLooper());
        dispatchProcessor.setNewState(state);
        shadowLooper.runOneTask();
        Mockito.verify(stateUpdateCallback, Mockito.times(1)).notifyStateUpdate(state);
    }

    @Ignore("TODO: Fix with CI")
    public void destroy() {
        dispatchProcessor.destroy();
        Assert.assertFalse(dispatchProcessor.getDispatchThreadHandler().getLooper().getThread().isAlive());
    }

    @After
    public void destroyInstances() {
        dispatchProcessor.destroy();
    }


    @After
    public void tearDownMockito() {
        Mockito.validateMockitoUsage();
    }
}