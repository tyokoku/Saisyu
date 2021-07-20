/*
 * Copyright 2020 Google LLC
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

import android.content.Context;
import android.content.SharedPreferences;

/** Manages the Occlusion option setting and shared preferences. */
//オクルージョンオプションの設定と共有設定を管理します。
public class DepthSettings {
  public static final String SHARED_PREFERENCES_ID = "SHARED_PREFERENCES_OCCLUSION_OPTIONS";
  public static final String SHARED_PREFERENCES_SHOW_DEPTH_ENABLE_DIALOG_OOBE =
      "show_depth_enable_dialog_oobe";
  public static final String SHARED_PREFERENCES_USE_DEPTH_FOR_OCCLUSION = "use_depth_for_occlusion";

  // Current depth-based settings used by the app.
  //現在、アプリで使用されている深度ベースの設定。
  private boolean depthColorVisualizationEnabled = false;
  private boolean useDepthForOcclusion = false;
  private SharedPreferences sharedPreferences;

  /** Initializes the current settings based on when the app was last used. */
  //アプリが最後に使用されたときに基づいて、現在の設定を初期化します。
  public void onCreate(Context context) {
    sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_ID, Context.MODE_PRIVATE);
    useDepthForOcclusion =
        sharedPreferences.getBoolean(SHARED_PREFERENCES_USE_DEPTH_FOR_OCCLUSION, false);
  }

  /** Retrieves whether depth-based occlusion is enabled. */
  //奥行きベースのオクルージョンが有効かどうかを取得します。
  public boolean useDepthForOcclusion() {
    return useDepthForOcclusion;
  }

  public void setUseDepthForOcclusion(boolean enable) {
    if (enable == useDepthForOcclusion) {
      return; // No change.
    }

    // Updates the stored default settings.
    //保存されている初期設定を更新します。
    useDepthForOcclusion = enable;
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(SHARED_PREFERENCES_USE_DEPTH_FOR_OCCLUSION, useDepthForOcclusion);
    editor.apply();
  }

  /** Retrieves whether to render the depth map visualization instead of the camera feed. */
  //カメラの映像ではなく、
  //デプスマップを表示するかどうかを取得します。
  public boolean depthColorVisualizationEnabled() {
    return depthColorVisualizationEnabled;
  }

  public void setDepthColorVisualizationEnabled(boolean depthColorVisualizationEnabled) {
    this.depthColorVisualizationEnabled = depthColorVisualizationEnabled;
  }

  /** Determines if the initial prompt to use depth-based occlusion should be shown. */
  //深度ベースのオクルージョンを使用するかどうかの初期プロンプトを表示するかどうかを決定します。
  public boolean shouldShowDepthEnableDialog() {
    // Checks if this dialog has been called before on this device.
    //このダイアログがこのデバイスで以前に呼び出されたことがあるかどうかをチェックします。
    boolean showDialog =
        sharedPreferences.getBoolean(SHARED_PREFERENCES_SHOW_DEPTH_ENABLE_DIALOG_OOBE, true);

    if (showDialog) {
      // Only ever shows the dialog on the first time.  If the user wants to adjust these settings
      // again, they can use the gear icon to invoke the settings menu dialog.
      //初回時のみダイアログを表示します。
      // ユーザーがこれらの設定を再度調整したい場合は、歯車のアイコンを使って設定メニューダイアログを呼び出すことができます。
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putBoolean(SHARED_PREFERENCES_SHOW_DEPTH_ENABLE_DIALOG_OOBE, false);
      editor.apply();
    }

    return showDialog;
  }
}
