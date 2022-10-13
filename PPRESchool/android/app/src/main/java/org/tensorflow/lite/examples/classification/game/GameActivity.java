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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.AudioManager;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.env.Logger;
import org.tensorflow.lite.examples.classification.menu.Study;
import org.tensorflow.lite.examples.classification.tflite.Classifier;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Device;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Model;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Recognition;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends CameraActivity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
    private static final float TEXT_SIZE_DIP = 10;
    private Bitmap rgbFrameBitmap = null;
    private long lastProcessingTimeMs;
    private Integer sensorOrientation;
    private Classifier classifier;
    /**
     * Input image size of the model along x axis.
     */
    private int imageSizeX;
    /**
     * Input image size of the model along y axis.
     */
    private int imageSizeY;
    private TextView tvkr;

    List<String> results;
    String result;

    String obj1;
    String obj2;
    String obj3;

    String krkr;

    TextView text1;
    TextView text2;
    TextView text3;

    ImageView resultView1;
    ImageView resultView2;
    ImageView resultView3;

    Button tr_pause;

    SoundPool soundPool;
    int soundID;

    List<String> gamelist;
    float game_percent;
    int num;

    boolean one, two, three;

    protected void onCreate(final Bundle savedInstanceState) {
        LOGGER.d("onCreate " + this); //Debug 로그 남기기
        super.onCreate(null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //유저의 입력을 받지 않아도 화면을 꺼지지 않게 한다.

        setContentView(R.layout.game); //원하는 디자인을 parameter로 사용하여 Oncreate()를 호출한다.
        tvkr = findViewById(R.id.gameTvKr);

        Intent intent = getIntent();
        obj1 = intent.getStringExtra("obj1");
        obj2 = intent.getStringExtra("obj2");
        obj3 = intent.getStringExtra("obj3");

        text1 = (TextView) findViewById(R.id.textview1);
        text2 = (TextView) findViewById(R.id.textview2);
        text3 = (TextView) findViewById(R.id.textview3);

        tr_pause = (Button) findViewById(R.id.tr_pause);

        text1.setText(obj1);
        text2.setText(obj2);
        text3.setText(obj3);

        one = false;
        two = false;
        three = false;

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundID = soundPool.load(this, R.raw.shalala, 1);

        num = ResultActivity.amount;

        switch (num) {
            case 1:
                two = true;
                three = true;
            case 2:
                three = true;
            case 3:
        }

        tr_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit(v);
            }
        });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.tfe_ic_camera_connection_fragment;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE; //미리보기 프레임 사이즈 (600, 480)
    }

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        //borderedText = new BorderedText(textSizePx);
        //borderedText.setTypeface(Typeface.MONOSPACE);

        recreateClassifier(getModel(), getDevice(), getNumThreads());

        if (classifier == null) {
            LOGGER.e("No classifier on preview!");
            return;
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation(); //화면에 상대적인 카메라 방향
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
    }

    @Override
    protected void processImage() { //분류기 호출 후 추론 결과가 processImage()함수에 의해 실행된다.
        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
        final int cropSize = Math.min(previewWidth, previewHeight);

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        if (classifier != null) {
                            final long startTime = SystemClock.uptimeMillis();

                            final List<Recognition> results =
                                    classifier.recognizeImage(rgbFrameBitmap);
                            lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                            LOGGER.v("Detect: %s", results);
                            //다 정답이면 액티비티 전환
                            if (one == true && two == true && three == true) {
                                Handler hh = new Handler();
                                hh.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent gameWinIntent = new Intent(getApplicationContext(), GameWinActivity.class);
                                        startActivity(gameWinIntent);
                                        overridePendingTransition(0,0);
                                    }
                                }, 2000);
                            }


                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            showResultsInBottomSheet(results);
                                            gamelist = new ArrayList<String>();

                                            if (results != null ) {
                                                Recognition recognition = results.get(0);

                                                if (recognition != null) {

                                                    if (recognition.getTitle() != null && recognition.getConfidence() > 0.40) {


                                                        gamelist.add(recognition.getTitle());

                                                        //text3.setText(recognition.getTitle());
                                                        //recognitionTextView.setText(recognition.getTitle());
                                                        //game_percent = 100 * recognition.getConfidence();
                                                    }
                                                }}
                                            if(gamelist != null){
                                                resultView1 = (ImageView)findViewById(R.id.result1);
                                                resultView2= (ImageView)findViewById(R.id.result2);
                                                resultView3= (ImageView)findViewById(R.id.result3);


                                                if(gamelist.contains(obj1)){
                                                    if(one == false){
                                                        soundPool.play(soundID,1f,1f,0,0,1f);
                                                        new BackgroundTask(obj1).execute();
                                                        resultView1.setImageResource(R.drawable.sttgood);
                                                        resultView1.setVisibility(View.VISIBLE);
                                                        text1.setText("");
                                                        one = true;
                                                    }



                                                    Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            resultView1.setVisibility(View.INVISIBLE);
                                                            tvkr.setText("");
                                                        }
                                                    }, 3000);
                                                }
                                                if(gamelist.contains(obj2)){

                                                    if(two == false){
                                                        soundPool.play(soundID,1f,1f,0,0,1f);
                                                        new BackgroundTask(obj2).execute();
                                                        resultView2.setImageResource(R.drawable.sttgood);
                                                        resultView2.setVisibility(View.VISIBLE);
                                                        text2.setText("");
                                                        two = true;
                                                    }

                                                    Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            resultView2.setVisibility(View.INVISIBLE);
                                                            tvkr.setText("");
                                                        }
                                                    }, 3000);
                                                }
                                                if(gamelist.contains(obj3)){
                                                    if(three == false){
                                                        soundPool.play(soundID,1f,1f,0,0,1f);
                                                        new BackgroundTask(obj3).execute();
                                                        resultView3.setImageResource(R.drawable.sttgood);
                                                        resultView3.setVisibility(View.VISIBLE);
                                                        text3.setText("");
                                                        three = true;
                                                    }


                                                    Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            resultView3.setVisibility(View.INVISIBLE);
                                                            tvkr.setText("");
                                                        }
                                                    }, 3000);
                                                }



                                            }

                                            //showFrameInfo(previewWidth + "x" + previewHeight);
                                            //showCropInfo(imageSizeX + "x" + imageSizeY);
                                            //showCameraResolution(cropSize + "x" + cropSize);
                                            //showRotationInfo(String.valueOf(sensorOrientation));
                                            //showInference(lastProcessingTimeMs + "ms");
                                        }
                                    }
                            );
                        }
                        readyForNextImage();
                    }
                });
    }

    @Override
    protected void onInferenceConfigurationChanged() {
        if (rgbFrameBitmap == null) {
            // Defer creation until we're getting camera frames. -> 카메라 frame 가져올때 까지 생성하지 않음 (null이라는 것은 frame에 아무것도 없다는 것이다.)
            return;
        }
        final Device device = getDevice();
        final Model model = getModel();
        final int numThreads = getNumThreads();

        runInBackground(() -> recreateClassifier(model, device, numThreads));

    }

    private void recreateClassifier(Model model, Device device, int numThreads) {
        if (classifier != null) {
            LOGGER.d("Closing classifier.");
            classifier.close();
            classifier = null;
        }
        if (device == Device.GPU
                && (model == Model.QUANTIZED_MOBILENET || model == Model.QUANTIZED_EFFICIENTNET)) {
            LOGGER.d("Not creating classifier: GPU doesn't support quantized models.");
            runOnUiThread(
                    () -> {
                        Toast.makeText(this, R.string.tfe_ic_gpu_quant_error, Toast.LENGTH_LONG).show();
                    });
            return;
        }
        try {
            LOGGER.d(
                    "Creating classifier (model=%s, device=%s, numThreads=%d)", model, device, numThreads);
            classifier = Classifier.create(this, model, device, numThreads);
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.e(e, "Failed to create classifier.");
            runOnUiThread(
                    () -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    });
            return;
        }


        // Updates the input image size.
        imageSizeX = classifier.getImageSizeX();
        imageSizeY = classifier.getImageSizeY();
    }


    public void exit (View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("게임 종료").setMessage("이대로 게임을 끝낼까요?");

        builder.setPositiveButton("끝내기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Study.class);
                startActivity(intent);
                finish();

            }
        });



        builder.setNeutralButton("게임 계속하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Toast.makeText(getApplicationContext(), "Neutral Click", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }




    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        String str;

        public BackgroundTask(String s) {
            str = s;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            StringBuilder output = new StringBuilder();
            //naver api app id/secret
            String clientId = "Y9j_2nCSM2FbqG4zvVzw";
            String clientSecret = "N9qvfGokGj";
            try {
                //번역문을 utf-8로 인코딩
                String text = URLEncoder.encode(str);

                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";

                //파파고 api와 연결
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                //영어를 한글로 번역 후 가져온 텍스트를 파라미터로
                String postParams = "source=en&target=ko&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();

                //번역 결과를 받아옴
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                while ((inputLine = br.readLine()) != null) { //가져온게 있다면
                    output.append(inputLine);
                }
                br.close();

            } catch (Exception e) {
                Log.e("SampleHTTP", "Exception in processing response.", e);
                e.printStackTrace();
            }

            result = output.toString();
            return null;
        }

        protected void onPostExecute(Integer a) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            if (element.getAsJsonObject().get("errorMessage") != null) {
                Log.e("번역오류", "번역오류가 발생" + "[오류코드: " + element.getAsJsonObject().get("errorCode").getAsString() + "]");
            } else if (element.getAsJsonObject().get("message") != null) {
                //번역결과 출력
                krkr = element.getAsJsonObject().get("message").getAsJsonObject().get("result").getAsJsonObject().get("translatedText").getAsString();
                tvkr.setText(krkr + "를 찾았어요");
            }
        }

        protected void onPreExecute() {

        }
    }
}
