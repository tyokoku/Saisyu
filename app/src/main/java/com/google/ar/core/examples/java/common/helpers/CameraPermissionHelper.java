/*
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.core.examples.java.common.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/** Helper to ask camera permission. */
//カメラの許可を得るヘルパー
public final class CameraPermissionHelper {
  private static final int CAMERA_PERMISSION_CODE = 0;
  private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;

  /** Check to see we have the necessary permissions for this app. */
  //このアプリに必要な許可を得ているかどうかを確認します。
  public static boolean hasCameraPermission(Activity activity) {
    return ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION)
        == PackageManager.PERMISSION_GRANTED;
  }

  /** Check to see we have the necessary permissions for this app, and ask for them if we don't. */
  /*このアプリに必要な許可を得ているかどうかを確認し、
  許可を得ていない場合は許可を求めます。
  */
  public static void requestCameraPermission(Activity activity) {
    ActivityCompat.requestPermissions(
        activity, new String[] {CAMERA_PERMISSION}, CAMERA_PERMISSION_CODE);
  }

  /** Check to see if we need to show the rationale for this permission. */
  //この許可の根拠を示す必要があるかどうかを確認します。
  public static boolean shouldShowRequestPermissionRationale(Activity activity) {
    return ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION);
  }

  /** Launch Application Setting to grant permission. */
  //起動アプリケーションの設定で許可を与える
  public static void launchPermissionSettings(Activity activity) {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
    activity.startActivity(intent);
  }
}
