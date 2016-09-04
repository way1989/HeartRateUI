package com.sherchen.heartrate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.sherchen.heartrate.views.WaveView;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        final WaveView waveView = (WaveView) findViewById(R.id.wv_start);
        final Button btn = (Button) findViewById(R.id.button1);
        btn.setText("start");
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                waveView.startPaint(54000, 600);
            }
        });
        waveView.setOnPainterTrickListener(new WaveView.OnPainterTrickListener() {

            @Override
            public void onPainterTrick(int value) {
                btn.setText("onPainterTrick");
            }

            @Override
            public void onPainterFinished() {
                btn.setText("start");
            }

            @Override
            public int getPainterValue() {
                return 0;
            }
        });
    }

}
