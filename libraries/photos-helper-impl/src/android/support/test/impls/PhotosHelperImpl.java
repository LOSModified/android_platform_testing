/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.support.test.helpers;

import android.app.Instrumentation;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.Until;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import java.util.regex.Pattern;

import junit.framework.Assert;

public class PhotosHelperImpl extends AbstractPhotosHelper {
    private static final String LOG_TAG = PhotosHelperImpl.class.getSimpleName();

    private static final long TRANSITION_TIMEOUT = 5000;

    private static final String UI_PACKAGE_NAME = "com.google.android.apps.photos";

    public PhotosHelperImpl(Instrumentation instr) {
        super(instr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackage() {
        return "com.google.android.apps.photos";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getLauncherName() {
        return "Photos";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dismissInitialDialogs() {
        long original = Configurator.getInstance().getWaitForIdleTimeout();
        Configurator.getInstance().setWaitForIdleTimeout(1000);
        // Target Photos version 1.4.0.102264174
        // Press 'GET STARTED'
        Pattern getStartedWords = Pattern.compile("GET STARTED", Pattern.CASE_INSENSITIVE);
        UiObject2 getStartedButton = mDevice.findObject(By.text(getStartedWords));
        if (getStartedButton != null) {
            getStartedButton.click();
        }
        // Press 'CONTINUE' twice
        for (int repeat = 0; repeat < 2; repeat++) {
            Pattern continueWords = Pattern.compile("CONTINUE", Pattern.CASE_INSENSITIVE);
            UiObject2 continueButton = mDevice.wait(Until.findObject(By.text(continueWords)),
                    TRANSITION_TIMEOUT);
            if (continueButton != null) {
                continueButton.click();
                mDevice.waitForIdle();
            }
        }
        for (int repeat = 0; repeat < 4; repeat++) {
            UiObject2 nextButton = mDevice.wait(Until.findObject(By.desc("Next")),
                    TRANSITION_TIMEOUT);
            if (nextButton != null) {
                nextButton.click();
                mDevice.waitForIdle();
            }
        }
        Configurator.getInstance().setWaitForIdleTimeout(original);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openFirstClip() {
        for (int i = 0; i < 3; i++) {
            UiObject2 clip = getFirstClip();
            if (clip != null) {
                clip.click();
                mDevice.wait(Until.findObject(
                        By.res(UI_PACKAGE_NAME, "photos_videoplayer_play_button_holder")), 2000);
                return;
            } else {
                scrollContainer(Direction.DOWN);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pauseClip() {
        UiObject2 holder = mDevice.findObject(
                By.res(UI_PACKAGE_NAME, "photos_videoplayer_play_button_holder"));
        if (holder != null) {
            holder.click();
        } else {
            Assert.fail("Adequate failure message.");
        }

        UiObject2 pause = mDevice.wait(Until.findObject(
                By.res(UI_PACKAGE_NAME, "photos_videoplayer_pause_button")), 2500);
        if (pause != null) {
            pause.click();
            mDevice.wait(Until.findObject(By.desc("Play video")), 2500);
        } else {
            Assert.fail("Adequate failure message.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playClip() {
        UiObject2 play = mDevice.findObject(By.desc("Play video"));
        if (play != null) {
            play.click();
            mDevice.wait(Until.findObject(
                    By.res(UI_PACKAGE_NAME, "photos_videoplayer_pause_button")), 2500);
        } else {
            Assert.fail("Adequate failure message.");
        }
    }

    private void scrollContainer(Direction dir) {
        UiObject2 container = mDevice.findObject(By.res(UI_PACKAGE_NAME, "photo_container"));
        if (container != null) {
            container.scroll(dir, 1.0f);
            mDevice.waitForIdle();
        } else {
            Assert.fail("No valid scrolling mechanism found.");
        }
    }

    private UiObject2 getFirstClip() {
        return mDevice.findObject(By.descStartsWith("Video"));
    }
}
