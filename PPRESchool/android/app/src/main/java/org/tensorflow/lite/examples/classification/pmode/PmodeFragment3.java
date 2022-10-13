package org.tensorflow.lite.examples.classification.pmode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kakao.auth.ApiErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

import org.tensorflow.lite.examples.classification.LoginActivty;
import org.tensorflow.lite.examples.classification.R;
import org.tensorflow.lite.examples.classification.data.ChildData;
import org.tensorflow.lite.examples.classification.kakaologin.kakao_main;
import org.tensorflow.lite.examples.classification.network.ServiceApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class PmodeFragment3 extends Fragment {

    Button kakao_logout;
    Button kakao_signout;
    TextView kakao_account;
    ImageView imageView3;
    ImageView network_image;
    TextView network;

    SeekBar sound_seekbar = null;
    MediaPlayer mediaPlayer;
    AudioManager audioManager = null;
    private final static int MAX_VOLUME = 100;

    private String id;
    private ServiceApi service;
    List<ChildData> list = new ArrayList<>();
    ChildData childData;

    private static final String DEBUG_TAG = "NetworkStatusExample";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.pmode_fragment3, container, false);

        Context context= getContext();

        kakao_logout = (Button) rootView.findViewById(R.id.kakao_logout);
        kakao_signout = (Button) rootView.findViewById(R.id.kakao_signout);
        kakao_account = (TextView) rootView.findViewById(R.id.kakao_account);
        sound_seekbar = (SeekBar) rootView.findViewById(R.id.sound_seekbar);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        network = (TextView) rootView.findViewById(R.id.network);
        network_image = (ImageView) rootView.findViewById(R.id.network_image);
        imageView3 = (ImageView) rootView.findViewById(R.id.imageView3);

        id = ((LoginActivty)LoginActivty.login_con).id;
        kakao_account.setText(id);

        imageView3.setImageResource(R.drawable.ppreschool_logo);

        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiConn = false;
        boolean isMobileConn = false;
        for (Network network : connMgr.getAllNetworks()) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
            }
        }

        if(isWifiConn == true) {
            network_image.setImageResource(R.drawable.wifi_network);
            network.setText("WIFI에 연결되었습니다.");
        } else if (isMobileConn == true) {
            network_image.setImageResource(R.drawable.lte_network);
            network.setText("LTE 사용중입니다.");
        }

        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
        Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);

        setSound_seekbar();

        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.main_bgm);


        //카카오 로그아웃
        kakao_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();

                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent i = new Intent(getActivity(), kakao_main.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mediaPlayer.pause();
                        startActivity(i);
                    }
                });
            }
        });

        //카카오 회원탈퇴
        kakao_signout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) { //회원탈퇴 버튼 클릭 시
                new AlertDialog.Builder(getActivity()) //탈퇴 여부를 묻는 팝업창 실행
                        .setMessage("탈퇴하시겠습니까?") //팝업창의 메세지 설정
                        .setPositiveButton("네", new DialogInterface.OnClickListener() { //"예" 버튼 클릭 시 -> 회원탈퇴 수행
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() { //회원탈퇴 실행
                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {

                                    }

                                    @Override
                                    public void onFailure(ErrorResult errorResult) { //회원탈퇴 실패 시
                                        int result = errorResult.getErrorCode();

                                        if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                                            Toast.makeText(getActivity(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(), "회원탈퇴에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                    @Override
                                    public void onSuccess(Long result) { //회원탈퇴에 성공하면
                                        //"회원탈퇴에 성공했습니다."라는 Toast 메세지를 띄우고 로그인 창으로 이동함
                                        Toast.makeText(getActivity(), "회원탈퇴에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getActivity(), kakao_main.class);
                                        startActivity(intent);
                                    }
                                });

                                dialog.dismiss(); //팝업 창을 닫음
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() { //"아니요" 버튼 클릭 시 -> 팝업 창을 닫음
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); //팝업 창을 닫음
                            }
                        }).show();
            }
        });


        return rootView;
    }

    public void setSound_seekbar() {
        int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int nCurrentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        sound_seekbar.setMax(nMax);
        sound_seekbar.setProgress(nCurrentVolume);

        sound_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }
        });
    }

    //서버에 child 테이블 조회
    private void getChild(ChildData data) {
        service.getChild(data).enqueue(new Callback<List<ChildData>>() {
            @Override
            public void onResponse(Call<List<ChildData>> call, retrofit2.Response<List<ChildData>> response) {

                if (response.isSuccessful() && response.body() != null)
                {
                    Log.d("데이터 조회 응답",response.body().toString());
                    childData = response.body().get(0);
                    kakao_account.setText(childData.getUserEmail());
                }
            }

            @Override
            public void onFailure(Call<List<ChildData>> call, Throwable t) {
                Toast.makeText(getActivity(), "데이터 조회 서버 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("데이터 조회 서버 에러 발생", t.getMessage());
            }
        });
    }

}