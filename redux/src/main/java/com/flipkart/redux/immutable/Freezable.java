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

package com.flipkart.redux.immutable;

/**
 * https://developer.android.com/reference/android/icu/util/Freezable
 * <p>
 * Provides a flexible mechanism for controlling access, without requiring that a class be immutable.
 * Once frozen, an object can never be unfrozen, so it is thread-safe from that point onward.
 * Once the object has been frozen, it must guarantee that no changes can be made to it.
 * Any attempt to alter it must raise an UnsupportedOperationException exception.
 * This means that when the object returns internal objects, or if anyone has references to those
 * internal objects, that those internal objects must either be immutable, or must also raise exceptions
 * if any attempt to modify them is made. Of course, the object can return clones of internal objects,
 * since those are safe.
 * </p>
 */

public interface Freezable<T> extends Cloneable {

    boolean isFrozen();

    T freeze();

    T cloneAsThawed();
}

