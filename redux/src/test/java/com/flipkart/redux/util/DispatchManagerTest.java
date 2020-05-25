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

import com.flipkart.redux.core.Action;
import com.flipkart.redux.core.Dispatcher;
import com.flipkart.redux.core.Middleware;
import com.flipkart.redux.core.State;
import com.flipkart.redux.core.Store;
import com.flipkart.redux.testutil.TestUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class DispatchManagerTest {

    private DispatchManager<State, Action> dispatchManager;

    @Mock
    TestUtils.StateActionMiddleware middleware1;
    @Mock
    TestUtils.StateActionMiddleware middleware2;
    @Mock
    TestUtils.StateActionMiddleware middleware3;
    @Mock
    Dispatcher<Action> reducerDispatch;
    @Mock
    Store<State, Action> store;

    @Before
    public void setUp() {
        hookUpDispatch(middleware1);
        hookUpDispatch(middleware2);
        hookUpDispatch(middleware3);
        dispatchManager = new DispatchManager<>(reducerDispatch, new TestUtils.StateActionMiddleware[]{middleware1, middleware2, middleware3});
    }

    @Test
    public void handleDispatch() {
        dispatchManager.handleDispatch(store, () -> "STUB");
        InOrder order = Mockito.inOrder(middleware1, middleware2, middleware3, reducerDispatch);
        order.verify(middleware1, times(1)).dispatch(any(), any(), any());
        order.verify(middleware2, times(1)).dispatch(any(), any(), any());
        order.verify(middleware3, times(1)).dispatch(any(), any(), any());
        order.verify(reducerDispatch, times(1)).dispatch(any());

        dispatchManager = new DispatchManager<>(reducerDispatch, null);
        Mockito.verify(reducerDispatch, times(1)).dispatch(any());

    }

    @Test
    public void getMiddlewareExisting() {
        Middleware<State, Action>[] middleware = dispatchManager.getMiddleware();
        Assert.assertEquals(3, middleware.length);
        Assert.assertEquals("First Middleware", middleware[0], middleware1);
        Assert.assertEquals("Second Middleware", middleware[1], middleware2);
        Assert.assertEquals("Third Middleware", middleware[2], middleware3);
    }

    @Test
    public void getMiddlewareNone() {
        dispatchManager = new DispatchManager<>(reducerDispatch, null);
        Assert.assertNull(dispatchManager.getMiddleware());
    }


    private void hookUpDispatch(Middleware<State, Action> middleware) {
        doAnswer(invocation -> {
            Dispatcher<Action> dispatcher = invocation.getArgument(2);
            dispatcher.dispatch(invocation.getArgument(0));
            return null;
        }).when(middleware).dispatch(any(), any(), any());
    }
}