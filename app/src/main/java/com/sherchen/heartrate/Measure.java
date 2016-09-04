package com.sherchen.heartrate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.sherchen.heartrate.control.MeasureControl;
import com.sherchen.heartrate.control.util.CommonUtil;
import com.sherchen.heartrate.views.ProgressWheelFitButton;
import com.sherchen.heartrate.views.TwinkleDrawable;
import com.sherchen.heartrate.views.WaveView;
import com.todddavies.components.progressbar.ProgressWheel;


//The WaveView is bad ,and not smooth, so the mark here is stimulate me to make a good performance
public class Measure extends AppCompatActivity implements View.OnClickListener {
    //	disable hardware test
//    private static final int MEASURE_DURATION = 120;//s
    private static final float MEASURE_DURATION = 120f;//s
    private static final float MEASURE_INTERVAL = 1;//s
    private static final int ONE_SECOND = 1000;//ms

    private static final int MEASURE_STATE_START = 0;
    private static final int MEASURE_STATE_SAVE = 1;
    private static final int MEASURE_STATE_FAIL = 2;
    private static final int MEASURE_STATE_ING = 3;
    private static final String TAG = "Start";
    private static final boolean DEBUG = true;
    private final BroadcastReceiver mScreenOnOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {

                //Log.e(TAG, "screen on...");
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                //Log.e(TAG, "screen off...");
            }
        }
    };
    private int mMeasureState = MEASURE_STATE_START;
    private ProgressWheel mProgressWheel;
    private ImageView mIvSettings;
    private ProgressWheelFitButton mBtnToggle;
    private WaveView mWaveView;
    private ImageView mIvLine;
    private ImageView mIvHeart;
    private TextView mTvLabel;
    private TextView mTvDoneTime;
    private TwinkleDrawable mHeartDrawable;
    private MeasureControl mControl;
    private boolean isMeasureSuccess = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mHeartDrawable != null) {
            mHeartDrawable.recycle();
        }
        if (mControl != null) {
            mControl.onDestroy();
        }

        if (mWaveView != null) {
            mWaveView.stopPaint();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeUiToInitial();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measure);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setViews();
        setValues();
        setListeners();
    }

    private void setViews() {
        mIvSettings = (ImageView) findViewById(R.id.iv_history);
        mProgressWheel = (ProgressWheel) findViewById(R.id.pw_heartrate);
        mBtnToggle = (ProgressWheelFitButton) findViewById(R.id.btn_toggle);
        mWaveView = (WaveView) findViewById(R.id.wv_start);
        mIvLine = (ImageView) findViewById(R.id.iv_line_start);
        mIvHeart = (ImageView) findViewById(R.id.iv_heart_measure);
        mTvLabel = (TextView) findViewById(R.id.tv_data_measure);
        mTvDoneTime = (TextView) findViewById(R.id.tv_done_time_measure);
    }

    private void setValues() {
        mControl = new MeasureControl(this);
        mProgressWheel.setMax((int) (MEASURE_DURATION / MEASURE_INTERVAL));
        mHeartDrawable = new TwinkleDrawable(mIvHeart);
        mHeartDrawable.addDrawable(getResources().getDrawable(R.drawable.ic_heart_big), true);
        mHeartDrawable.addDrawable(getResources().getDrawable(R.drawable.ic_heart_small), false);
    }

    private void setListeners() {
        mIvSettings.setOnClickListener(this);
        mBtnToggle.setOnClickListener(this);
        mProgressWheel.setOnSizeChangedListener(new ProgressWheel.SizeChangedListener() {
            @Override
            public void onSizeChanged(ProgressWheel wheel) {
                mBtnToggle.clip(wheel.getWidth(), wheel.getHeight(), wheel.getRimWidth());
            }
        });
        mWaveView.setOnPainterTrickListener(new WaveView.OnPainterTrickListener() {
            @Override
            public int getPainterValue() {
                return -1;
            }

            @Override
            public void onPainterTrick(int value) {
                mProgressWheel.incrementProgress();
                if (value != 0) {// if no value stay same.
                    changeUiOnLabel(true, String.format("%03d", value));
                }
            }

            @Override
            public void onPainterFinished() {
                int average = mWaveView.getAverage();
                onMeasureFinished(average);
            }
        });
    }

    public void onMeasureFinished(int average) {
        mHeartDrawable.stopTwinkle();
        if (average != -1) {
            changeUiOnLabel(true, String.format("%03d", average));
            changeUiOnSuccessFinishMeasure();
        } else {
            changeUiOnLabel(false, getString(R.string.measure_rate_result_error));
            changeUiOnFailFinishMeasure();
        }
    }

    public void  onMeasureStart() {
        mHeartDrawable.startTwinkle();
        changeUiOnStartMeasure();
        mWaveView.startPaint((long) (MEASURE_DURATION * ONE_SECOND), (long) (MEASURE_INTERVAL * ONE_SECOND));
    }

    private void changeUiOnLabel(boolean success, String text) {
        mTvLabel.setText(text);
    }

    private void changeUiToInitial() {
        changeUiOnLabel(true, getString(R.string.measure_rate_default));
        mProgressWheel.resetCount(false);
        mBtnToggle.setVisibility(View.VISIBLE);
        mIvLine.setVisibility(View.VISIBLE);
        mWaveView.setVisibility(View.INVISIBLE);
        mBtnToggle.setText(R.string.measure_btn_start);
        mTvDoneTime.setVisibility(View.GONE);

        mMeasureState = MEASURE_STATE_START;
    }

    private void changeUiOnStartMeasure() {
        changeUiOnLabel(true, getString(R.string.measure_rate_default));
        mProgressWheel.resetCount(false);
        mBtnToggle.setVisibility(View.INVISIBLE);
        mIvLine.setVisibility(View.INVISIBLE);
        mWaveView.setVisibility(View.VISIBLE);
        mTvDoneTime.setVisibility(View.GONE);

        mMeasureState = MEASURE_STATE_ING;
    }

    private void changeUiOnSuccessFinishMeasure() {
        mProgressWheel.resetCount(false);
        mBtnToggle.setVisibility(View.VISIBLE);
        mIvLine.setVisibility(View.VISIBLE);
        mWaveView.setVisibility(View.INVISIBLE);
        mBtnToggle.setText(R.string.measure_btn_save);
        mTvDoneTime.setVisibility(View.VISIBLE);
        mTvDoneTime.setText(CommonUtil.getReadableDateTime(System.currentTimeMillis()));

        mMeasureState = MEASURE_STATE_SAVE;
    }

    private void changeUiOnFailFinishMeasure() {
        mProgressWheel.resetCount(false);
        mBtnToggle.setVisibility(View.VISIBLE);
        mIvLine.setVisibility(View.VISIBLE);
        mWaveView.setVisibility(View.INVISIBLE);
        mBtnToggle.setText(R.string.measure_btn_fail);
        mTvDoneTime.setVisibility(View.GONE);

        mMeasureState = MEASURE_STATE_FAIL;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_history:
                viewHistory();
                break;
            case R.id.btn_toggle:
                switch (mMeasureState) {
                    case MEASURE_STATE_START:
                        onBtnClickStart();
                        break;
                    case MEASURE_STATE_SAVE:
                        onBtnClickSave();
                        break;
                    case MEASURE_STATE_FAIL:
                        onBtnClickFail();
                        break;
                }
                break;
        }
    }

    private void viewHistory() {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }

    private void onBtnClickStart() {
        onMeasureStart();
    }

    private void onBtnClickSave() {
        mControl.saveMeasure(mWaveView.getMeasureStartTime(), mWaveView.getAverage());
        viewHistory();
    }

    private void onBtnClickFail() {
        onBtnClickStart();
    }

    @Override
    public void onBackPressed() {
        if (mMeasureState == MEASURE_STATE_SAVE || mMeasureState == MEASURE_STATE_FAIL) {
            changeUiToInitial();
        } else {
            super.onBackPressed();
        }
    }

    public void log(String msg) {
        if (DEBUG) android.util.Log.v(TAG, msg);
    }
}
