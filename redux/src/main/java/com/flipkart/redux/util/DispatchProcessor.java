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

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;

import com.flipkart.redux.core.Action;
import com.flipkart.redux.core.ReduxStore;
import com.flipkart.redux.core.State;
import com.flipkart.redux.core.Store;

import java.lang.ref.WeakReference;


public class DispatchProcessor<S extends State, A extends Action> {

    private Handler mainThreadHandler;
    private Handler dispatchThreadHandler;
    private HandlerThread dispatchThread;

    private WeakReference<Store<S, A>> storeWeakReference;

    private static final int MESSAGE_CODE_NEW_STATE = 42;
    private static final int MESSAGE_CODE_NEW_DISPATCH = 21;
    private static final String DISPATCH_THREAD = "DispatchThread";

    public DispatchProcessor(@NonNull Store<S, A> store, @NonNull DispatchManager<S, A> dispatchManager, @NonNull ReduxStore.StateUpdateCallback<S> stateUpdateCallback, @NonNull HandlerProvider handlerProvider) {
        this.storeWeakReference = new WeakReference<>(store);
        dispatchThread = new HandlerThread(DISPATCH_THREAD, Process.THREAD_PRIORITY_BACKGROUND);
        dispatchThread.start();
        this.dispatchThreadHandler = handlerProvider.getHandler(dispatchThread.getLooper(), message -> dispatchToDispatchManager(message, dispatchManager));
        this.mainThreadHandler = handlerProvider.getHandler(Looper.getMainLooper(), message -> postToStore(message, stateUpdateCallback));
    }

    @MainThread
    public void dispatch(@NonNull A action) {
        getDispatchThreadHandler().obtainMessage(MESSAGE_CODE_NEW_DISPATCH, action).sendToTarget();
    }

    @WorkerThread
    public void setNewState(@NonNull S state) {
        getMainThreadHandler().obtainMessage(MESSAGE_CODE_NEW_STATE, state).sendToTarget();
    }

    @SuppressWarnings("unused")
    public void destroy() {
        if (dispatchThread != null && dispatchThread.isAlive()) {
            dispatchThread.quit();
        }
    }

    private void postToStore(Message message, @NonNull ReduxStore.StateUpdateCallback<S> stateUpdateCallback) {
        if (message.what == MESSAGE_CODE_NEW_STATE) {
            //noinspection unchecked
            stateUpdateCallback.notifyStateUpdate((S) message.obj);
        }
    }

    private void dispatchToDispatchManager(Message message, @NonNull DispatchManager<S, A> dispatchManager) {
        if (message.what == MESSAGE_CODE_NEW_DISPATCH) {
            if (storeWeakReference != null) {
                Store<S, A> store = storeWeakReference.get();
                if (store != null) {
                    //noinspection unchecked
                    dispatchManager.handleDispatch(store, (A) message.obj);
                }
            }
        }
    }

    @VisibleForTesting
    Handler getDispatchThreadHandler() {
        return dispatchThreadHandler;
    }

    @VisibleForTesting
    Handler getMainThreadHandler() {
        return mainThreadHandler;
    }
}
