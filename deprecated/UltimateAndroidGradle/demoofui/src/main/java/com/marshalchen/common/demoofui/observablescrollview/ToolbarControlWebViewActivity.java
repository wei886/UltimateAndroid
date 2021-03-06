/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marshalchen.common.demoofui.observablescrollview;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.marshalchen.common.demoofui.R;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class ToolbarControlWebViewActivity extends ActionBarActivity {

    private View mHeaderView;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private boolean mFirstScroll;
    private boolean mDragging;
    private int mBaseTranslationY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observable_scroll_view_activity_toolbarcontrolwebview);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mHeaderView = findViewById(R.id.header);
        ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbarView = findViewById(R.id.toolbar);

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(mScrollViewScrollCallbacks);

        ObservableWebView mWebView = (ObservableWebView) findViewById(R.id.web);
        mWebView.setScrollViewCallbacks(mWebViewScrollCallbacks);
        mWebView.loadUrl("file:///android_asset/lipsum.html");
    }

    private ObservableScrollViewCallbacks mScrollViewScrollCallbacks = new ObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
            if (mDragging) {
                int toolbarHeight = mToolbarView.getHeight();
                if (mFirstScroll) {
                    mFirstScroll = false;
                    float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
                    if (-toolbarHeight < currentHeaderTranslationY && toolbarHeight < scrollY) {
                        mBaseTranslationY = scrollY;
                    }
                }
                int headerTranslationY = Math.min(0, Math.max(-toolbarHeight, -(scrollY - mBaseTranslationY)));
                ViewPropertyAnimator.animate(mHeaderView).cancel();
                ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
            }
        }

        @Override
        public void onDownMotionEvent() {
        }

        @Override
        public void onUpOrCancelMotionEvent(ScrollState scrollState) {
            mDragging = false;
            mBaseTranslationY = 0;

            float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
            int toolbarHeight = mToolbarView.getHeight();
            if (scrollState == ScrollState.UP) {
                if (toolbarHeight < mScrollView.getCurrentScrollY()) {
                    if (headerTranslationY != -toolbarHeight) {
                        ViewPropertyAnimator.animate(mHeaderView).cancel();
                        ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
                    }
                }
            } else if (scrollState == ScrollState.DOWN) {
                if (toolbarHeight < mScrollView.getCurrentScrollY()) {
                    if (headerTranslationY != 0) {
                        ViewPropertyAnimator.animate(mHeaderView).cancel();
                        ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
                    }
                }
            }
        }
    };

    private ObservableScrollViewCallbacks mWebViewScrollCallbacks = new ObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        }

        @Override
        public void onDownMotionEvent() {
            // Workaround: WebView inside a ScrollView absorbs down motion events, so observing
            // down motion event from the WebView is required.
            mFirstScroll = mDragging = true;
        }

        @Override
        public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        }
    };
}
