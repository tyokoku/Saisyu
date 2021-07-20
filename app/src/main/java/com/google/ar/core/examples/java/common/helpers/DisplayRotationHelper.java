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

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import com.google.ar.core.Session;

/**
 * Helper to track the display rotations. In particular, the 180 degree rotations are not notified
 * by the onSurfaceChanged() callback, and thus they require listening to the android display
 * events.
 */
/*ディスプレイの回転を追跡するヘルパーです。
  特に、180度の回転はonSurfaceChanged()コールバックでは通知されないので、
  androidのディスプレイイベントを聞く必要があります。
 */
public final class DisplayRotationHelper implements DisplayListener {
  private boolean viewportChanged;
  private int viewportWidth;
  private int viewportHeight;
  private final Display display;
  private final DisplayManager displayManager;
  private final CameraManager cameraManager;

  /**
   * Constructs the DisplayRotationHelper but does not register the listener yet.
   *DisplayRotationHelperを構築しますが、リスナーの登録はまだ行いません。
   * @param context the Android {@link Context}.
   */
  public DisplayRotationHelper(Context context) {
    displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
    cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    display = windowManager.getDefaultDisplay();
  }

  /** Registers the display listener. Should be called from {@link Activity#onResume()}. */
  //表示リスナーを登録します。{@link Activity#onResume()}から呼び出される必要があります。
  public void onResume() {
    displayManager.registerDisplayListener(this, null);
  }

  /** Unregisters the display listener. Should be called from {@link Activity#onPause()}. */
  //ディスプレイリスナーの登録を解除します。{@link Activity#onPause()}から呼び出す必要があります。
  public void onPause() {
    displayManager.unregisterDisplayListener(this);
  }

  /**
   * Records a change in surface dimensions. This will be later used by {@link
   * #updateSessionIfNeeded(Session)}. Should be called from {@link
   * android.opengl.GLSurfaceView.Renderer
   * #onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)}.
   *
   * @param width the updated width of the surface.
   * @param height the updated height of the surface.
   */
  /*表面寸法の変更を記録する。
  この記録は後に{@link #updateSessionIfNeeded(Session)}で使用されます。
  これは {@link android.opengl.GLSurfaceView.Renderer #onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)}
  から呼び出される必要があります。
   */
  public void onSurfaceChanged(int width, int height) {
    viewportWidth = width;
    viewportHeight = height;
    viewportChanged = true;
  }

  /**
   * Updates the session display geometry if a change was posted either by {@link
   * #onSurfaceChanged(int, int)} call or by {@link #onDisplayChanged(int)} system callback. This
   * function should be called explicitly before each call to {@link Session#update()}. This
   * function will also clear the 'pending update' (viewportChanged) flag.
   *
   * @param session the {@link Session} object to update if display geometry changed.
   */
  /*{@link #onSurfaceChanged(int, int)}の呼び出しや
  {@link #onDisplayChanged(int)}のシステムコールバックによって変更が投稿された場合、
  セッションの表示ジオメトリを更新します。
  この関数は、{@link Session#update()}の各呼び出しの前に
  明示的に呼び出す必要があります。
  この関数は、「更新待ち」（viewportChanged）フラグもクリアします。
   */
  public void updateSessionIfNeeded(Session session) {
    if (viewportChanged) {
      int displayRotation = display.getRotation();
      session.setDisplayGeometry(displayRotation, viewportWidth, viewportHeight);
      viewportChanged = false;
    }
  }

  /**
   *  Returns the aspect ratio of the GL surface viewport while accounting for the display rotation
   *  relative to the device camera sensor orientation.
   */
  //GLサーフェスビューポートのアスペクト比を、
  // デバイスのカメラセンサーの向きに対するディスプレイの回転を考慮して返します。
  public float getCameraSensorRelativeViewportAspectRatio(String cameraId) {
    float aspectRatio;
    int cameraSensorToDisplayRotation = getCameraSensorToDisplayRotation(cameraId);
    switch (cameraSensorToDisplayRotation) {
      case 90:
      case 270:
        aspectRatio = (float) viewportHeight / (float) viewportWidth;
        break;
      case 0:
      case 180:
        aspectRatio = (float) viewportWidth / (float) viewportHeight;
        break;
      default:
        throw new RuntimeException("Unhandled rotation: " + cameraSensorToDisplayRotation);
    }
    return aspectRatio;
  }

  /**
   * Returns the rotation of the back-facing camera with respect to the display. The value is one of
   * 0, 90, 180, 270.
   */
  /*ディスプレイを基準とした背面カメラの回転を返します。
  値は、0、90、180、270のいずれかです。
   */
  public int getCameraSensorToDisplayRotation(String cameraId) {
    CameraCharacteristics characteristics;
    try {
      characteristics = cameraManager.getCameraCharacteristics(cameraId);
    } catch (CameraAccessException e) {
      throw new RuntimeException("Unable to determine display orientation", e);
    }

    // Camera sensor orientation.カメラセンサーの向き。
    int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

    // Current display orientation.現在のディスプレイの向き。
    int displayOrientation = toDegrees(display.getRotation());

    // Make sure we return 0, 90, 180, or 270 degrees.
    //0度、90度、180度、270度のいずれかを返すようにします。
    return (sensorOrientation - displayOrientation + 360) % 360;
  }

  private int toDegrees(int rotation) {
    switch (rotation) {
      case Surface.ROTATION_0:
        return 0;
      case Surface.ROTATION_90:
        return 90;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_270:
        return 270;
      default:
        throw new RuntimeException("Unknown rotation " + rotation);
    }
  }

  @Override
  public void onDisplayAdded(int displayId) {}

  @Override
  public void onDisplayRemoved(int displayId) {}

  @Override
  public void onDisplayChanged(int displayId) {
    viewportChanged = true;
  }
}
