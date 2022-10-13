package org.tensorflow.lite.examples.classification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.tensorflow.lite.examples.classification.data.ChildData;
import org.tensorflow.lite.examples.classification.menu.Review;
import org.tensorflow.lite.examples.classification.menu.Study;
import org.tensorflow.lite.examples.classification.menu.Test;
import org.tensorflow.lite.examples.classification.network.RetrofitClient;
import org.tensorflow.lite.examples.classification.network.ServiceApi;
import org.tensorflow.lite.examples.classification.pmode.PmodePopUpActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class hamburger_toolbar extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    TextView profile_name;
    ImageView profile_picture;
    private String id;
    private ServiceApi service;
    List<ChildData> list = new ArrayList<>();
    ChildData childData;
    public static int level;
    public static Context hamContext;
    public static String nick;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        hamContext = this;
    }

    public void make_hamburger() {

        service = RetrofitClient.getClient().create(ServiceApi.class);

        Toolbar ham_toolbar = findViewById(R.id.ham_toolbar);
        setSupportActionBar(ham_toolbar);

        //drawer_header에 있는 profile name 바꾸기
        profile_name = (TextView) findViewById(R.id.profile_name);



        //이름 서버에서 가져와서 넣기
        id = ((LoginActivty)LoginActivty.login_con).id;
        getChild(new ChildData(id));


        //drawer_header에 있는 profile picture 바꾸기
        profile_picture = (ImageView) findViewById(R.id.profile_image);

        //main.xml에 있는 drawer_layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav);
        navigationView.setItemIconTintList(null);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, ham_toolbar, R.string.tfe_ic_app_name, R.string.tfe_ic_app_name);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_bar);

        //제목 표시되지 않도록
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        View nav_header_view = navigationView.getHeaderView(0);

        //drawer_header에 있는 profile name 바꾸기
        profile_name = (TextView) nav_header_view.findViewById(R.id.profile_name);

        //drawer_header에 있는 profile picture 바꾸기

        profile_picture = (ImageView) nav_header_view.findViewById(R.id.profile_image);
        profile_picture.setImageResource(R.drawable.basic_profile);

        //네비게이션 뷰 아이템 클릭시 이뤄지는 이벤트
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();

                int id = item.getItemId();

                switch (id) {
                    //학습하기
                    case R.id.menu_1:
                        Intent menu_1 = new Intent(getApplicationContext(), Study.class);
                        startActivity(menu_1);
                        //Toast.makeText(hamburger_toolbar.this,item.getTitle(), Toast.LENGTH_LONG).show();
                        break;
                    //복습하기
                    case R.id.menu_2:
                        Intent menu_2 = new Intent(getApplicationContext(), Review.class);
                        startActivity(menu_2);
                        //Toast.makeText(hamburger_toolbar.this,item.getTitle(), Toast.LENGTH_LONG).show();
                        break;
                    //시험보기
                    case R.id.menu_3:
                        Intent menu_3 = new Intent(getApplicationContext(), Test.class);
                        startActivity(menu_3);
                        //Toast.makeText(hamburger_toolbar.this,item.getTitle(), Toast.LENGTH_LONG).show();
                        break;

                    //부모모드
                    case R.id.menu_4:
                        Intent menu_4 = new Intent(getApplicationContext(), PmodePopUpActivity.class);
                        startActivity(menu_4);
                        //Toast.makeText(hamburger_toolbar.this,item.getTitle(), Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });

    }

    //서버에 child 테이블 조회
    public void getChild(ChildData data) {
        service.getChild(data).enqueue(new Callback<List<ChildData>>() {
            @Override
            public void onResponse(Call<List<ChildData>> call, retrofit2.Response<List<ChildData>> response) {

                if (response.isSuccessful() && response.body() != null)
                {
                    Log.d("데이터 조회 응답",response.body().toString());
                    childData = response.body().get(0);
                    profile_name.setText(childData.getUserNick());
                    level = childData.getUserLevel();
                    nick = profile_name.getText().toString();
                }


            }

            @Override
            public void onFailure(Call<List<ChildData>> call, Throwable t) {
                Toast.makeText(hamburger_toolbar.this, "데이터 조회 서버 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("데이터 조회 서버 에러 발생", t.getMessage());
            }
        });
    }

}
