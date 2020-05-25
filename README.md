# Redux-Android 
###### (name TBD)

### Redux on Android
This is a port of the [Redux](https://redux.js.org/) paradigm for building apps, built specifically for use on the Android platform. It uses native Android constructs such as Lifecycle Aware components, LiveData, ViewModels etc to achieve the Redux spec.

<p align="center">
<img src="https://github.com/flipkart-incubator/redux-android/blob/master/Redux%20Example.gif" width="550" height="400" style=""/> </p>

### Extra stuff

A lot of the things seen above belong outside the Redux implementation, and are hence explained here:

#### ReduxViewModel
An extension of the Android ViewModel API. Composes the actual Redux Store & subscribes to it. The core Redux implementation is agnostic of Android lifecycles (for Activities/Fragments). This is the bridge between the Redux world and the Android view layer. It is also responsible for initializing the Store.

#### ReduxController
API for subscribing to and receiving updates from the 'Store'. Composes 'ReduxViewModel' to hook up & manage things internally. Should be composed inside your Activity/Fragment.

#### DispatchManager
It's responsibilities include maintaining the middleware chain & handling the flow of actions through it.

#### DispatchThreadProcessor
Since Android is multi-threaded, we take advantage of it & execute actions on the entire dispatch chain in a background thread. The results are posted back to the main thread, where the Action originated.

##### StateManager
Handles maintaining multiple instances of the state. A crass object pool with just two values, which it keeps alternating. Debateably necessary for ensuring that no one holds the state/modifies it.

## API

To begin with, create an implementation of the `State` interface:
````java
public class AppState implements State<AppState> {

    public String screenState;

    @Override
    public void sync(@NonNull AppState currentState) {
        this.screenState = currentState.screenState;
    }
}
````
As well as the `Action` interface:
````java
public class AppAction implements Action {

    @NonNull
    public String type;

    @NonNull
    public HashMap<String, String> payload;

    public AppAction(@NonNull String type, @NonNull HashMap<String, String> payload) {
        this.type = type;
        this.payload = payload;
    }

    @NonNull
    @Override
    public String getType() {
        return type;
    }
}
````
Then (optionally), create your `Middleware` as per your use case:

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

Then, extend `ReduxViewModel` & implement the `initializeStore()` method:

````java
public class AppReduxViewModel extends ReduxViewModel<AppState, AppAction> {

    @NonNull
    @Override
    protected Store<AppState, AppAction> initializeStore() {
        AppState initState = new AppState();
        initState.screenState = "DEFAULT";
        return new Store<>(initState, new AppStateCreator(), new AppReducer(), new LoggingMiddleware());
    }
}
````

The Store constructor takes in the following arguments:
````java
Store(@NonNull S initialState, @NonNull StateCreator<S> stateCreator, @NonNull Reducer<S, A> reducer, @Nullable Middleware<S, A>... middlewareList)
````

Finally, implement the `ReduxComponent` interface & instantiate a new `ReduxController` inside your Activity/Fragment:
````java
public class MainActivity extends FragmentActivity implements ReduxComponent<AppState, AppAction, AppReduxViewModel> {
 ...
    @Nullable
    ReduxController<AppState, AppAction, AppReduxViewModel> reduxController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        reduxController = new ReduxController<>(this, this, this);
    }

    @NonNull
    @Override
    public Class<AppReduxViewModel> getReduxViewModelClass() {
        return AppReduxViewModel.class;
    }

    @Override
    public void updateState(@Nullable AppState state) {...}
}
````

Then, to dispatch an action, use the `dispatch` function provided by the controller, wherever necessary:

````java
   button.setOnClickListener(v -> {
        HashMap<String, String> payload = new HashMap<>();
        String currentScreen = "NEW_SCREEN";
        payload.put("screen", currentScreen);
        reduxController.dispatch(new AppAction("CHANGE_SCREEN", payload));
    });
````

Once this dispatch is consumed by the Store & a new state has been reduced, the Activity/Fragment will be notified via the `updateState` callback implemented via the redux component:
````java

    @Override
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

