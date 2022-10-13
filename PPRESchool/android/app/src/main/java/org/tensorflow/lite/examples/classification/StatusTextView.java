package org.tensorflow.lite.examples.classification;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import org.tensorflow.lite.examples.classification.StrokeManager.StatusChangedListener;

/**
 * Status bar for the test app.
 *
 * <p>It is updated upon status changes announced by the StrokeManager.
 */
public class StatusTextView extends AppCompatTextView implements StatusChangedListener {

    private StrokeManager strokeManager;

    public String mmresult;
    private String target;
    private TextView temp;
    private Context mContext;


    public StatusTextView(@NonNull Context context) {
        super(context);
    }

    public StatusTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void onStatusChanged() {
        mmresult = this.strokeManager.getStatus();
        this.setText(mmresult);
    }

    void setStrokeManager(StrokeManager strokeManager) {
        this.strokeManager = strokeManager;
    }


   /* public void check(){


        //result = strokeManager.recognitionTask.result().text;

        //StatusTextView
        String result = strokeManager.getStatus();
        if(result.equals(enString))
        {
            //Toast.makeText(MainActivity.this, "잘하셨어요!!!", Toast.LENGTH_SHORT).show();
            //3초간 정답 이미지 보여주기
            resultView.setImageResource(R.drawable.sttgood);
            resultView.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    resultView.setVisibility(View.INVISIBLE);
                }
            }, 3000);
        } else {
            //3초간 틀림 이미지 보여주기
            resultView.setImageResource(R.drawable.sttbad);
            resultView.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    resultView.setVisibility(View.INVISIBLE);
                }
            }, 3000);


            //tvresult.setText(strokeManager.getStatus());
        }

    */
}