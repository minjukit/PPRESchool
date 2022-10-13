package org.tensorflow.lite.examples.classification.env;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.tensorflow.lite.examples.classification.Level3StudyActivity;
import org.tensorflow.lite.examples.classification.LoginActivty;
import org.tensorflow.lite.examples.classification.MyDictionaryRequest;
import org.tensorflow.lite.examples.classification.Papago;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.data.VocaData;
import org.tensorflow.lite.examples.classification.data.VocaResponse;
import org.tensorflow.lite.examples.classification.hamburger_toolbar;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;
import org.tensorflow.lite.examples.classification.tflite.Classifier;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Device;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Model;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Recognition;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class MyCamera extends AppCompatActivity{

    private Classifier classifier;
    private long lastProcessingTimeMs;
    private final int MY_PERMISSIONS_REQUEST_CAMERA=1001;
    private ImageButton buttonOk;
    private TextView processText;
    private TextView percentText;
    private Classifier.Model model = Model.QUANTIZED_EFFICIENTNET;
    private Classifier.Device device =Device.CPU;
    private int numThreads = -1;
    public String objectname; //파파고로 넘겨줄 객체-사물이름을 넘겨줌
    private String result; //번역스트링
    //network
    private ServiceApi service;
    int dailyNum = 1;
    String krText;

    ImageView imageView;
    Bitmap bitmap;
    File file;

    int level; //사용자 레벨을 체크해서 구분하여 다른 클래스로 전송

    //dictionary 변수
    private String worden;
    private String url;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_camera);
        this.context = context;
        //network
        service = RetrofitClient.getClient().create(ServiceApi.class);
        //인식된 결과텍스트
        processText = (TextView)findViewById(R.id.processText);
        percentText = (TextView)findViewById(R.id.percentText);

        level = ((hamburger_toolbar)hamburger_toolbar.hamContext).level;

        //권한 체크
        int permssionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permssionCheck!= PackageManager.PERMISSION_GRANTED) {

            //Toast.makeText(this,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                //Toast.makeText(this,"인식부분 사용을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
                //Toast.makeText(this,"인식부분 사용을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();

            }
        }

        //어플내부저장소
        file = new File(this.getFilesDir(), "capture.jpg");

        imageView = findViewById(R.id.mimageView);
        buttonOk= (ImageButton)findViewById(R.id.studybtn);
        ImageButton button = findViewById(R.id.mCameraBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });

        //팝업으로 튜토리얼 액티비티 띄우기 CameraPopUpActivity
        Intent intentPop = new Intent(getApplicationContext(),CameraPopUpActivity.class);
        startActivity(intentPop);

    }


    public void capture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "org.tensorflow.lite.examples.classification.env.fileprovider", file));
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101  && resultCode == Activity.RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            imageView.setImageBitmap(bitmap);
            //비트맵만들어지면 클래시피케이션 객체만들고 인식프로세싱
            recreateClassifier(getModel(), getDevice(), getNumThreads());
            processImage();
            //이미지찍으면 ok버튼 보이도록
            buttonOk.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"승인이 허가되어 있습니다.",Toast.LENGTH_LONG).show();

                } else {
                    //Toast.makeText(this,"아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    protected void processImage() { //분류기 호출 후 추론 결과가 processImage()함수에 의해 실행된다.
        if (classifier != null) {
            //final long startTime = SystemClock.uptimeMillis();
            final List<Recognition> results = classifier.recognizeImage(bitmap);
            //lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            showResults(results);
                        }
                    }
            );
            //showResults(results);

            //딜레이 없도록 여기서 free dictionary API 호출
            if(processText.getText().toString().length() == 0){
                Toast.makeText(context,"입력해", Toast.LENGTH_LONG).show();
            } else {
                worden = processText.getText().toString();
                url = inflections();
                if(level == 3){

                    MyDictionaryRequest myDictionaryRequest = new MyDictionaryRequest(this);
                    myDictionaryRequest.execute(url);
                }
            }
        }
    }

    @UiThread
    protected void showResults(List<Recognition> results) {
        if (results != null && results.size() >= 3) {
            Recognition recognition = results.get(0);
            processText.setText(recognition.getTitle());
            objectname = recognition.getTitle();
            //Toast.makeText(this, recognition.getTitle(), Toast.LENGTH_SHORT).show();
            if (recognition != null) {
                if (recognition.getTitle() != null) {
                    processText.setText(recognition.getTitle());
                    objectname = recognition.getTitle();
                    new BackgroundTask().execute();
                }
                /*퍼센트
                if (recognition.getConfidence() != null)
                    percentText.setText( String.format("%.2f", (100 * recognition.getConfidence())) + "%");
                 */
            }

        }



    }

    protected Model getModel() {
        return model;
    }

    private void setModel(Classifier.Model model) {
        if (this.model != model) {

            this.model = model;
        }
    }

    protected Device getDevice() {
        return device;
    }

    private void setDevice(Classifier.Device device) {
        if (this.device != device) {
            this.device = device;
            final boolean threadsEnabled = device == Device.CPU;

        }

    }

    protected int getNumThreads() {
        return numThreads;
    }

    private void setNumThreads(int numThreads) {
        if (this.numThreads != numThreads) {
            this.numThreads = numThreads;
        }
    }


    private void recreateClassifier(Classifier.Model model, Classifier.Device device, int numThreads) {
        if (classifier != null) {

            classifier.close();
            classifier = null;
        }
        if (device == Classifier.Device.GPU
                && (model == Classifier.Model.QUANTIZED_MOBILENET || model == Classifier.Model.QUANTIZED_EFFICIENTNET)) {

            runOnUiThread(
                    () -> {
                        Toast.makeText(this, R.string.tfe_ic_gpu_quant_error, Toast.LENGTH_LONG).show();
                    });
            return;
        }
        try {

            classifier = Classifier.create(this, model, device, numThreads);
        } catch (IOException | IllegalArgumentException e) {
            runOnUiThread(
                    () -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    });
            return;
        }
    }

    //온클릭 - Papago 액티비티로
    public void requestfreeDicApi(View v){


        if(level == 3){
            Intent intent = new Intent(getApplicationContext(),
                    Level3StudyActivity.class);
            intent.putExtra("Objectname", objectname);
            intent.putExtra("KrText", krText );
            startActivity(intent);

        }

        else{

            Intent intent = new Intent(getApplicationContext(),
                    Papago.class);
            intent.putExtra("Objectname", objectname );
            intent.putExtra("KrText", krText);
            startActivity(intent);
        }

    }


    private String inflections() {
        final String language = "en_US";

        final String word = worden;
        //final String word_lo = word.toLowerCase();
        return "https://api.dictionaryapi.dev/api/v2/entries/" + language + "/" + word;
    }


    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... integers) {
            StringBuilder output = new StringBuilder();
            //naver api app id/secret
            String clientId = "ucCPDClWHqvVvhvIwJf9";
            String clientSecret = "jitlzZD5Vq";
            try {
                //번역문을 utf-8로 인코딩
                String text = URLEncoder.encode(objectname);

                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";

                //파파고 api와 연결
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
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
                if(responseCode == 200){
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else{
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                while ((inputLine =br.readLine()) != null) { //가져온게 있다면
                    output.append(inputLine);
                }
                br.close();

            }catch (Exception e){
                Log.e("SampleHTTP", "Exception in processing response.",e);
                e.printStackTrace();
            }

            result = output.toString();
            return null;
        }
        protected void onPostExecute(Integer a){
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            if(element.getAsJsonObject().get("errorMessage") != null){
                Log.e("번역오류", "번역오류가 발생" + "[오류코드: "+element.getAsJsonObject().get("errorCode").getAsString() + "]");
            }else if (element.getAsJsonObject().get("message") != null){
                //번역결과 저장
                krText = element.getAsJsonObject().get("message").getAsJsonObject().get("result").getAsJsonObject().get("translatedText").getAsString();
                //db저장 쿼리보내기
                String id = ((LoginActivty)LoginActivty.login_con).id;
                saveVoca(new VocaData(id, objectname, krText, "2021-09-24")); //어차피 날짜는 서버에서 저장함 이거랑 노상관
            }
        }
        protected void onPreExecute(){

        }
    }


    //db 저장위한 레트로핏 통신
    private void saveVoca(VocaData data) {
        service.userVoca(data).enqueue(new Callback<VocaResponse>() {

            @Override
            public void onResponse(Call<VocaResponse> call, retrofit2.Response<VocaResponse> response) {
                //VocaResponse result = response.body();
                VocaResponse result = response.body();

                if (result.getResult()) {
                    Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<VocaResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"에러에러에러", Toast.LENGTH_SHORT).show();
                Log.e("에러 발생", t.getMessage() + t.toString() + t.getLocalizedMessage());

            }

        });
    }


}