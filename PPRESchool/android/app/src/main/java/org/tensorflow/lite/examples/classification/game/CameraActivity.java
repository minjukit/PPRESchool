/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.classification.game;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.tensorflow.lite.examples.classification.CameraConnectionFragment;
import org.tensorflow.lite.examples.classification.LegacyCameraConnectionFragment;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.env.ImageUtils;
import org.tensorflow.lite.examples.classification.env.Logger;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Device;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Model;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Recognition;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

//전면 카메라 지원 X
public abstract class CameraActivity extends AppCompatActivity
        implements OnImageAvailableListener, //새 이미지를 사용할 수 있다는 알림을 받기위한 콜백 인터페이스입니다 onImageAvailable은 이미지 단위로 호출됩니다. 즉, ImageReader에서 사용할 수있는 모든 새 프레임에 대해 콜백이 실행됩니다.
        Camera.PreviewCallback, //미리보기 프레임이 표시 될 때 사본을 전달하는 데 사용되는 콜백 인터페이스입니다
        View.OnClickListener, //보기를 클릭 할 때 호출되는 콜백에 대한 인터페이스 정의입니다.
        AdapterView.OnItemSelectedListener { //이보기의 항목이 선택되었을 때 호출 될 콜백에 대한 인터페이스 정의입니다
  private static final Logger LOGGER = new Logger(); //Logger 개체는 특정 시스템 또는 응용 프로그램 구성 요소에 대한 메시지를 기록하는 데 사용됩니다

  private static final int PERMISSIONS_REQUEST = 1; //True
  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA; //Device에 접근하기 위한 permission
  protected int previewWidth = 0;
  protected int previewHeight = 0;
  private Handler handler; //핸들러를 사용하면 Message스레드와 관련된 Runnable 개체 를 보내고 처리 할 수 있습니다
  private HandlerThread handlerThread; //Thread갖는다 Looper. 는 Looper다음 만들 수 있습니다 Handler들. 일반 Thread에서 와 마찬가지로 Thread.start()여전히 호출되어야합니다.
  private boolean useCamera2API;
  private boolean isProcessingFrame = false; //특정 frame의 처리를 마치면 isProcessingFrame의 변수가 false로 설정되고 다음 frame이 처리가 되도록 전달해야한다.
  private byte[][] yuvBytes = new byte[3][]; //YUV는 컬러 이미지 파이프라인의 일부로 사용되는 색 인코딩 시스템이다
  private int[] rgbBytes = null;
  private int yRowStride; //마찬가지로 color 관련으로 추정
  ////Runnable인터페이스는 스레드가 해당 인스턴스를 실행하는 모든 클래스에 의해 구현되어야한다.
  private Runnable postInferenceCallback; //특정 프레임의 처리를 마치면 postInferenceCallback.run () 메서드를 호출하여 isProcessingFrame 변수가 false로 설정되고 다음 프레임이 처리를 위해 전달되도록해야합니다.
  private Runnable imageConverter;
  private LinearLayout bottomSheetLayout; //클릭시 아래에서 호출되는 layout
  private LinearLayout gestureLayout;
  private BottomSheetBehavior<LinearLayout> sheetBehavior;
  protected TextView recognitionTextView,
          recognition1TextView,
          recognition2TextView,
          recognitionValueTextView,
          recognition1ValueTextView,
          recognition2ValueTextView;

  protected TextView frameValueTextView,
          cropValueTextView,
          cameraResolutionTextView,
          rotationTextView,
          inferenceTimeTextView,
          resultsTextView;

  ListView listView1;
  ArrayAdapter<String> adapter;
  ArrayList<String> results_db;

  protected ImageView bottomSheetArrowImageView;
  private ImageView plusImageView, minusImageView;
  private Spinner modelSpinner; //스피너는 값 집합에서 하나의 값을 선택할 수있는 빠른 방법을 제공합니다.
  private Spinner deviceSpinner;
  private TextView threadsTextView;


  private Button send_btn;

  private Model model = Model.QUANTIZED_EFFICIENTNET;
  private Device device = Device.CPU;
  private int numThreads = -1;
  private int REQUEST_ACT = 0;

  String obj1;
  String obj2;
  String obj3;
  //힌트 파일사진
  File hintFile;
  String filename;
  Context context;
  Fragment fragment;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    LOGGER.d("onCreate " + this); //Debug 로그 남기기
    super.onCreate(null);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //유저의 입력을 받지 않아도 화면을 꺼지지 않게 한다.

    setContentView(R.layout.camera); //원하는 디자인을 parameter로 사용하여 Oncreate()를 호출한다.

    if (hasPermission()) { //특정 작업을 수행 할 수있는 권한이 부여되었는지 확인합니다.
      setFragment();
    } else {
      requestPermission();
    }

 context = this;



    //threadsTextView = findViewById(R.id.threads);
    //plusImageView = findViewById(R.id.plus); //threads 개수 조절
    //minusImageView = findViewById(R.id.minus);
    //modelSpinner = findViewById(R.id.model_spinner);
    //deviceSpinner = findViewById(R.id.device_spinner);
    //bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
    //gestureLayout = findViewById(R.id.gesture_layout);
//    sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
    //bottomSheetArrowImageView = findViewById(R.id.bottom_sheet_arrow); //모두 tfe_ic_layout_bottom_sheet.xml에 있다.

    //추가코드
    //

    results_db = new ArrayList<>();
    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,results_db);


    send_btn = (Button) findViewById(R.id.send);

    send_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);

        intent.putExtra("results", results_db);
        startActivity(intent);


      }
    });


  }





  protected int[] getRgbBytes() {
    imageConverter.run();
    return rgbBytes;
  }

  protected int getLuminanceStride() {
    return yRowStride;
  }

  protected byte[] getLuminance() {
    return yuvBytes[0];
  }

  /** Callback for android.hardware.Camera API */
  @Override
  public void onPreviewFrame(final byte[] bytes, final Camera camera) { //미리보기 프레임이 표시 될 때 호출됩니다.
    if (isProcessingFrame) {
      LOGGER.w("Dropping frame!");
      return;
    }

    try {
      // Initialize the storage bitmaps once when the resolution is known.
      if (rgbBytes == null) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        previewHeight = previewSize.height;
        previewWidth = previewSize.width;
        rgbBytes = new int[previewWidth * previewHeight];
        onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
      }
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      return;
    }

    isProcessingFrame = true;
    yuvBytes[0] = bytes;
    yRowStride = previewWidth;

    imageConverter =
            new Runnable() {
              @Override
              public void run() { //data -> rgb로 변환
                ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
              }
            };

    postInferenceCallback =
            new Runnable() {
              @Override
              public void run() {
                camera.addCallbackBuffer(bytes); //미리 할당 된 버퍼를 미리보기 콜백 버퍼 큐에 추가합니다. 미리보기 프레임이 도착하고 사용 가능한 버퍼가 하나 이상 있으면 해당 버퍼가 사용되어 대기열에서 제거됩니다. 그런 다음 미리보기 콜백이 버퍼와 함께 호출됩니다. 프레임이 도착하고 남은 버퍼가 없으면 프레임이 삭제됩니다.
                isProcessingFrame = false;
              }
            };
    processImage();
  }

  /** Callback for Camera2 API */
  @Override
  public void onImageAvailable(final ImageReader reader) { //ImageReader에서 새 이미지를 사용할 수 있을 때 호출되는 콜백입니다
    // We need wait until we have some size from onPreviewSizeChosen
    if (previewWidth == 0 || previewHeight == 0) {
      return;
    }
    if (rgbBytes == null) {
      rgbBytes = new int[previewWidth * previewHeight];
    }
    try {
      final Image image = reader.acquireLatestImage();

      if (image == null) {
        return;
      }

      if (isProcessingFrame) {
        image.close();
        return;
      }
      isProcessingFrame = true;
      Trace.beginSection("imageAvailable");
      final Plane[] planes = image.getPlanes();
      fillBytes(planes, yuvBytes);
      yRowStride = planes[0].getRowStride();
      final int uvRowStride = planes[1].getRowStride();
      final int uvPixelStride = planes[1].getPixelStride();

      imageConverter =
              new Runnable() {
                @Override
                public void run() {
                  ImageUtils.convertYUV420ToARGB8888(
                          yuvBytes[0],
                          yuvBytes[1],
                          yuvBytes[2],
                          previewWidth,
                          previewHeight,
                          yRowStride,
                          uvRowStride,
                          uvPixelStride,
                          rgbBytes);
                }
              };

      postInferenceCallback =
              new Runnable() {
                @Override
                public void run() {
                  image.close();
                  isProcessingFrame = false;
                }
              };

      processImage();
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      Trace.endSection();
      return;
    }
    Trace.endSection();


  }

  @Override
  public synchronized void onStart() { //어플 시작시
    LOGGER.d("onStart " + this);
    super.onStart();
  }

  @Override
  public synchronized void onResume() { //어플 재개시
    LOGGER.d("onResume " + this);
    super.onResume();

    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }

  @Override
  public synchronized void onPause() { //어플 중지시
    LOGGER.d("onPause " + this);

    handlerThread.quitSafely();
    try {
      handlerThread.join();
      handlerThread = null;
      handler = null;
    } catch (final InterruptedException e) {
      LOGGER.e(e, "Exception!");
    }

    super.onPause();
  }

  @Override
  public synchronized void onStop() { //어플 멈췄을때
    LOGGER.d("onStop " + this);
    super.onStop();
  }

  @Override
  public synchronized void onDestroy() { //어플 파괴되었을때
    LOGGER.d("onDestroy " + this);
    super.onDestroy();
  }

  protected synchronized void runInBackground(final Runnable r) { //Background에서 실행되고있을때
    if (handler != null) {
      handler.post(r);
    }
  }

  @Override
  public void onRequestPermissionsResult( //권한 요청 결과를 받았을 때
          final int requestCode, final String[] permissions, final int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST) {
      if (allPermissionsGranted(grantResults)) {
        setFragment();
      } else {
        requestPermission();
      }
    }
  }

  private static boolean allPermissionsGranted(final int[] grantResults) {
    for (int result : grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) { //Permission_granted -> 앱 권한 O , Permission_denied -> 앱 권한 X
        return false;
      }
    }
    return true;
  }

  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED; //특정 변환 부여 확인
    } else {
      return true;
    }
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
        Toast.makeText(
                CameraActivity.this,
                "Camera permission is required for this demo",
                Toast.LENGTH_LONG)
                .show();
      }
      requestPermissions(new String[] {PERMISSION_CAMERA}, PERMISSIONS_REQUEST);
    }
  }

  // Returns true if the device supports the required hardware level, or better.
  private boolean isHardwareLevelSupported(
          CameraCharacteristics characteristics, int requiredLevel) {
    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
      return requiredLevel == deviceLevel;
    }
    // deviceLevel is not LEGACY, can use numerical sort
    return requiredLevel <= deviceLevel;
  }

  private String chooseCamera() {
    final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      for (final String cameraId : manager.getCameraIdList()) {
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        // We don't use a front facing camera in this sample.
        final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
          continue;
        }

        final StreamConfigurationMap map =
                characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        if (map == null) {
          continue;
        }

        // Fallback to camera1 API for internal cameras that don't have full support.
        // This should help with legacy situations where using the camera2 API causes
        // distorted or otherwise broken previews.
        useCamera2API =
                (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                        || isHardwareLevelSupported(
                        characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
        LOGGER.i("Camera API lv2?: %s", useCamera2API);
        return cameraId;
      }
    } catch (CameraAccessException e) {
      LOGGER.e(e, "Not allowed to access camera");
    }

    return null;
  }

  protected void setFragment() {
    String cameraId = chooseCamera();

    //Fragment fragment;
    if (useCamera2API) {
      CameraConnectionFragment camera2Fragment =
              CameraConnectionFragment.newInstance(
                      new CameraConnectionFragment.ConnectionCallback() {
                        @Override
                        public void onPreviewSizeChosen(final Size size, final int rotation) {
                          previewHeight = size.getHeight();
                          previewWidth = size.getWidth();
                          CameraActivity.this.onPreviewSizeChosen(size, rotation);
                        }
                      },
                      this,
                      getLayoutId(),
                      getDesiredPreviewFrameSize());

      camera2Fragment.setCamera(cameraId);
      fragment = camera2Fragment;
    } else {
      fragment =
              new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
    }

    getFragmentManager().beginTransaction().replace(R.id.gameFrame, fragment).commit();
  }

  protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (int i = 0; i < planes.length; ++i) {
      final ByteBuffer buffer = planes[i].getBuffer();
      if (yuvBytes[i] == null) {
        LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
        yuvBytes[i] = new byte[buffer.capacity()];
      }
      buffer.get(yuvBytes[i]);
    }
  }

  protected void readyForNextImage() {
    if (postInferenceCallback != null) {
      postInferenceCallback.run();
    }
  }

  protected int getScreenOrientation() {
    switch (getWindowManager().getDefaultDisplay().getRotation()) {
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_90:
        return 90;
      default:
        return 0;
    }
  }
//추가코드


  float result_percent1;

  @UiThread

  protected void showResultsInBottomSheet(List<Recognition> results) {
    if (results != null && results.size() >= 3) {
      Recognition recognition = results.get(0);
      if (recognition != null) {
        if (recognition.getTitle() != null) {
          //recognitionTextView.setText(recognition.getTitle());
          result_percent1 = 100 * recognition.getConfidence();

          if (result_percent1 > 45) {
            if (!results_db.contains(recognition.getTitle())) {
                /*
              //힌트사진 저장할때 이름은 타이틀로
              filename = recognition.getTitle()+ ".png";
              //어플내부저장소 - 1006 삭제할것
              hintFile = new File(this.getFilesDir(), recognition.getTitle().toString());
              //전체화면 가져와서 캡쳐
              //final FrameLayout capture = (FrameLayout) findViewById(R.id.gameFrame);//캡쳐할영역
              File screenShot = ScreenShot(this.getCurrentFocus());
              if(screenShot!=null){ //캡쳐한 이미지가 있다면
                //갤러리에 추가
                Log.d("게임캡쳐", "저장성공"+filename);
                //Toast.makeText(this, "저장성공"+filename,Toast.LENGTH_LONG).show();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
              }

                 */
              results_db.add(recognition.getTitle());
              adapter.notifyDataSetChanged();
            }
          }
        }


      }

    }
  }
/*
  //카메라 캡쳐하려고 했더니 우린 텐서 카메라 써서 화면 캡쳐하기
  public File ScreenShot(View view){
    view.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다
    Bitmap screenBitmap = view.getDrawingCache();   //캐시를 비트맵으로 변환

    File file = new File(Environment.getExternalStorageDirectory()+"/Pictures", filename);  //Pictures폴더 screenshot.png 파일
    FileOutputStream os = null;
    try{
      os = new FileOutputStream(file);
      screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);   //비트맵을 PNG파일로 변환
      os.close();
    }catch (IOException e){
      e.printStackTrace();
      return null;
    }

      view.setDrawingCacheEnabled(false);
    return file;
  }

*/
  protected Model getModel() {
    return model;
  }

  private void setModel(Model model) {
    if (this.model != model) {
      LOGGER.d("Updating  model: " + model);
      this.model = model;
      onInferenceConfigurationChanged();
    }
  }

  protected Device getDevice() {
    return device;
  }

  private void setDevice(Device device) {
    if (this.device != device) {
      LOGGER.d("Updating  device: " + device);
      this.device = device;
      final boolean threadsEnabled = device == Device.CPU;

      onInferenceConfigurationChanged();
    }
  }

  protected int getNumThreads() {
    return numThreads;
  }

  private void setNumThreads(int numThreads) {
    if (this.numThreads != numThreads) {
      LOGGER.d("Updating  numThreads: " + numThreads);
      this.numThreads = numThreads;
      onInferenceConfigurationChanged();
    }
  }

  protected abstract void processImage();

  protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

  protected abstract int getLayoutId();

  protected abstract Size getDesiredPreviewFrameSize();

  protected abstract void onInferenceConfigurationChanged();

  @Override
  public void onClick(View v) {

  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
   // if (parent == modelSpinner) {
      setModel(Model.valueOf(parent.getItemAtPosition(pos).toString().toUpperCase()));
   // } else if (parent == deviceSpinner) {
      setDevice(Device.valueOf(parent.getItemAtPosition(pos).toString()));
    //}
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }
}
