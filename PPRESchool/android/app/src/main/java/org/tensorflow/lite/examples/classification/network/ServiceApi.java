package org.tensorflow.lite.examples.classification.network;

import org.tensorflow.lite.examples.classification.data.ChildData;
import org.tensorflow.lite.examples.classification.data.ClassificationData;
import org.tensorflow.lite.examples.classification.data.ClassificationResponse;
import org.tensorflow.lite.examples.classification.data.JoinData;
import org.tensorflow.lite.examples.classification.data.JoinResponse;
import org.tensorflow.lite.examples.classification.data.LoginData;
import org.tensorflow.lite.examples.classification.data.LoginResponse;
import org.tensorflow.lite.examples.classification.data.ScoreData;
import org.tensorflow.lite.examples.classification.data.VisitorData;
import org.tensorflow.lite.examples.classification.data.VocaData;
import org.tensorflow.lite.examples.classification.data.VocaResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceApi {
    //단어장전체불러오기
    //@FormUrlEncoded
    //@Multipart
    @POST("/vocalist")
    Call<List<VocaData>> getVoca(@Body VocaData data);
    //Call<List<VocaData>> getVoca(@Field("set") int set, @Body VocaData data);
    //오답노트전체불러오기
    //@FormUrlEncoded
    @POST("/wronglist")
    Call<List<VocaData>> getWrong(@Body VocaData data);
    //단어장추가
    @POST("/voca")
    Call<VocaResponse> userVoca(@Body VocaData data);
    //오답노트추가
    @POST("/wrong")
    Call<VocaResponse> userWrong(@Body VocaData data);
    //부모회원가입
    @POST("/signUp")
    Call<JoinResponse> userJoin(@Body JoinData data);
    //아이정보추가
    @POST("/userChild")
    Call<JoinResponse> userChild(@Body ChildData data);
    @POST("/visitor")
    Call<VocaResponse> saveVisitor(@Body VisitorData data);
    //레벨추가
    @POST("/userLevel")
    Call<JoinResponse> userLevel(@Body ChildData data);
    //child 테이블 조회
    @POST("/getChild")
    Call<List<ChildData>> getChild(@Body ChildData data);
    //테스트 랜덤조회
    @POST("/test")
    Call<List<VocaData>> getTest(@Body VocaData data);
    //오답테스트 랜덤조회
    @POST("/wTest")
    Call<List<VocaData>> getWrongTest(@Body VocaData data);
    @POST("/login")
    Call<LoginResponse> userLogin(@Body LoginData data);
    //score db 에 시험점수 저장
    @POST("/score")
    Call<JoinResponse> saveScore(@Body ScoreData data);
    @POST("/wscore")
    Call<JoinResponse> saveWScore(@Body ScoreData data);
    //시험 db 조회
    @POST("/getScore")
    Call<List<ScoreData>> getScore(@Body ScoreData data);
    //오답노트 시험 db 조회
    @POST("/getWScore")
    Call<List<ScoreData>> getWScore(@Body ScoreData data);
    //db 정보업데이트
    @POST("/updateChild")
    Call<JoinResponse> updateChild(@Body ChildData data);

    @POST("/classification")
    Call<ClassificationResponse> saveTitle(@Body ClassificationData data);

}
