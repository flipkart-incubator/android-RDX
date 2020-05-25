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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flipkart.redux.core.Action;
import com.flipkart.redux.core.Dispatcher;
import com.flipkart.redux.core.Middleware;
import com.flipkart.redux.core.ReduxStore;
import com.flipkart.redux.core.State;
import com.flipkart.redux.core.Store;

public class DispatchManager<S extends State, A extends Action> {

    private Node<Middleware<S, A>> startNode;

    private Middleware<S, A>[] middlewareList;

    public DispatchManager(@NonNull Dispatcher<A> dispatchToReducer, @Nullable Middleware<S, A>[] middlewareList) {
        this.middlewareList = middlewareList;
        Node<Middleware<S, A>> reducerNode = wrapReducerDispatch(dispatchToReducer);
        if (middlewareList != null) {
            startNode = constructDispatchChain(middlewareList, 0, reducerNode);
        } else {
            startNode = reducerNode;
        }
    }

    public void handleDispatch(@NonNull Store<S, A> store, @NonNull A action) {
        handle(startNode, store, action);
    }

    public Middleware<S, A>[] getMiddleware() {
        return middlewareList;
    }


    private void handle(@NonNull Node<Middleware<S, A>> node, @NonNull Store<S, A> store, @NonNull A originalAction) {
        node.item.dispatch(originalAction, store, (action) -> {
            Node<Middleware<S, A>> nextNode = node.next;
            if (nextNode != null) {
                handle(nextNode, store, action);
            }
        });
    }

    private Node<Middleware<S, A>> wrapReducerDispatch(@NonNull Dispatcher<A> dispatchToReducer) {
        return new Node<>((action, store, next) -> dispatchToReducer.dispatch(action), null);
    }

    private Node<Middleware<S, A>> constructDispatchChain(@NonNull Middleware<S, A>[] middlewareList, int middlewareIndex, @NonNull Node<Middleware<S, A>> lastNode) {
        if (middlewareIndex == middlewareList.length) {
            return lastNode;
        } else {
            return new Node<>(middlewareList[middlewareIndex], constructDispatchChain(middlewareList, ++middlewareIndex, lastNode));
        }
    }

    private static class Node<E> {
        @NonNull
        E item;
        @Nullable
        Node<E> next;

        Node(@NonNull E element, @Nullable Node<E> next) {
            this.item = element;
            this.next = next;
        }
    }
}
