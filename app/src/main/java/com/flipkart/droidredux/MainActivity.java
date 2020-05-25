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

package com.flipkart.droidredux;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flipkart.droidredux.reduximpl.AppAction;
import com.flipkart.droidredux.reduximpl.AppReduxViewModel;
import com.flipkart.droidredux.reduximpl.AppState;
import com.flipkart.droidredux.reduximpl.actions.ChangeScreenAction;
import com.flipkart.redux.lifecycle.ReduxController;

import java.io.Serializable;

public class MainActivity extends FragmentActivity {

    private static final String KEY_STATE = "state";

    TextView textView = null;
    EditText editText = null;
    Button button = null;

    @Nullable
    private AppState state;

    @Nullable
    private ReduxController<AppState, AppAction, AppReduxViewModel> reduxController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reduxController = new ReduxController<>(AppReduxViewModel.class, this::onStateChanged, this, this, true);

        if (savedInstanceState != null) {
            Serializable state = savedInstanceState.getSerializable(KEY_STATE);
            if (state instanceof AppState) {
                reduxController.restoreState((AppState) state);
            }
        }

        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        editText = findViewById(R.id.editText);

        button.setOnClickListener(v -> {
            String currentScreen = "NEW_SCREEN";
            if (editText != null && !TextUtils.isEmpty(editText.getText())) {
                currentScreen = editText.getText().toString();
            }
            reduxController.dispatch(new ChangeScreenAction(currentScreen));
        });
    }

    public void onStateChanged(@Nullable AppState state) {
        this.state = state;
        if (textView != null && state != null) {
            textView.setText(state.getScreenState());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (state != null) {
            outState.putSerializable(KEY_STATE, state);
        }
    }
}
