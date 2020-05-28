# Android-RDX
[![](https://jitpack.io/v/flipkart-incubator/android-RDX.svg)](https://jitpack.io/#flipkart-incubator/android-RDX)
[![Build Status](https://travis-ci.org/flipkart-incubator/android-RDX.svg?branch=master)](https://travis-ci.org/flipkart-incubator/android-RDX)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


### Redux on Android
This is a port of the [Redux](https://redux.js.org/) paradigm for building apps, built specifically for use on the Android platform. It uses native Android constructs including Lifecycle Aware components, LiveData, ViewModels etc to achieve the Redux spec.

<p align="center">
<img src="https://github.com/flipkart-incubator/redux-android/blob/master/Redux%20Example.gif" width="550" height="400" style=""/> </p>
<sub>(Image source: https://github.com/reduxjs/redux/issues/653#issuecomment-216844781)</sub>

#### NOTE: This library is currently in beta stage. The APIs are mostly frozen, but internals may change. 

## Getting Started

Add the jitpack repository to your root build.gradle

```kotlin
allprojects {
  repositories {
    maven { url "https://jitpack.io" }
  }
}
```

Add the android-RDX dependency:

```kotlin
dependencies {
        implementation 'com.github.flipkart-incubator:android-RDX:v1.0.0'
    }
```


## API

### Setup

To begin with, create implementations of the `State`  & `Action` interfaces:
````java
public class AppState implements State<AppState> {

    public String screenState;
    
    // All internal objects/primitives must be synced here, for preventing leaks & changing state references on every update.
    @Override
    public void sync(@NonNull AppState currentState) {
        this.screenState = currentState.screenState;
    }
}
````

````java
public class AppAction implements Action {

    @Nullable
    public HashMap<String, String> payload; // This can be anything. The Action interface only enforces the #getType() method.

    @NonNull
    @Override
    public String getType() {
        return "EXAMPLE_ACTION";
    }
}
````

Then, create a `Reducer` as follows:
````java
public class AppReducer implements Reducer<AppState, AppAction> {
    @NonNull
    @Override
    public AppState reduce(@NonNull AppState oldState, @NonNull AppAction action) {
        AppState newState = new AppState();
        newState.sync(oldState);

        switch (action.getType()) {
            case "CHANGE_SCREEN": {
                String newScreen = ((ChangeScreenAction) action).getScreenName();
                newState.setScreenState(newScreen);
                return newState;
            }
        }
        return newState;
    }
}
````


To initialize the Redux `Store`, extend `ReduxViewModel` & implement the `initializeStore()` method:

````java
public class AppReduxViewModel extends ReduxViewModel<AppState, AppAction> {

    @NonNull
    @Override
    protected Store<AppState, AppAction> initializeStore() {
        AppState initState = new AppState(); //initialize your state here.
        return new Store<>(initState, new AppReducer(), new LoggingMiddleware());
    }
}
````

The Store constructor takes in the following arguments:
````java
Store(@NonNull S initialState, @NonNull Reducer<S, A> reducer, @Nullable Middleware<S, A>... middlewareList)
````

Finally, instantiate a new `ReduxController` inside your Activity/Fragment:
````java
public class MainActivity extends FragmentActivity {

    @Nullable
    ReduxController<AppState, AppAction, AppReduxViewModel> reduxController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        reduxController = new ReduxController<>(AppReduxViewModel.class, this::updateState, this, this, true);
    }

    public void updateState(@Nullable AppState state) {...}
}
````
The ReduxController constructor takes in the following arguments:
````java
ReduxController(@NonNull Class<T> reduxViewModelClass, Observer<S> observer, @NonNull FragmentActivity activity, @NonNull LifecycleOwner lifecycleOwner, boolean distinctUntilChanged)
````
*Note that for enabling the `distinctUntilChanged` functionality (only getting redux store updates when the state is modified), your app's state MUST implement `equals()` & `hashcode()` correctly.

You can also (optionally) create your `Middleware` as per your use case, and provide it to your `ReduxViewModel` implementation via the `initializeStore()` method above, for eg:

````java
public class LoggingMiddleware implements Middleware<AppState, AppAction> {

    @Override
    public void dispatch(@NonNull AppAction action, @NonNull ReduxStore<AppState, AppAction> store, @Nullable Dispatcher<AppAction> next) {
        Log.d("TEST", " The current action type is: " + action.type + " Payload: " + action.payload);
        if (next != null) {
            next.dispatch(action);
        }
    }
}
````


### Usage

After the setup flow above, we are now ready to 'dispatch' an action & observe on state updates.
To dispatch an action, use the `dispatch` function provided by the controller, wherever necessary.:

````java
   button.setOnClickListener(v -> {
        HashMap<String, String> payload = new HashMap<>();
        String currentScreen = "NEW_SCREEN";
        payload.put("screen", currentScreen);
        reduxController.dispatch(new AppAction("CHANGE_SCREEN", payload));
    });
````
You can also expose the redux controller to any MVC/VM/Whatever instance via the Activity `context`, for executing dispatches appropriately.

Once this dispatch is consumed by the Store & a new state has been reduced, the Activity/Fragment will be notified via the `Observer<S extends State>` callback provided to the `ReduxController`:

````java
    // handle state updates here.
    public void updateState(@Nullable AppState state) {
        if (textView != null && state != null) {
            textView.setText(state.screenState);
        }
    }
````

## License

    The Apache License

    Copyright (c) 2020 Flipkart Internet Pvt. Ltd.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.

    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

    See the License for the specific language governing permissions and
    limitations under the License.

