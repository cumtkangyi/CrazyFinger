package com.childhood.crazyfinger;

import java.text.DecimalFormat;
import java.util.Timer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	TextView tvDateTime;
	TextView tvTimes;
	TextView tvScore;

	Button btnFinger;

	int times;
	float seconds = 10.00f;

	Timer timer;
	boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	void initView() {
		tvDateTime = (TextView) findViewById(R.id.tvDateTime);
		tvTimes = (TextView) findViewById(R.id.tvTimes);
		tvScore = (TextView) findViewById(R.id.tvScore);

		btnFinger = (Button) findViewById(R.id.btnFinger);
		btnFinger.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btnFinger) {
			times++;
			tvTimes.setText(times + "次");

			if (!flag) {
				flag = true;
				task.start();
			}
		}
	}

	private static final int TIMER_MSG = 10000;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == TIMER_MSG) {
				if (seconds <= 0.01) {
					flag = false;
					timer.cancel();
					timer.purge();
				}
				seconds = seconds - 0.01f;
				if (seconds != 0) {
					tvDateTime.setText(new DecimalFormat("0.00")
							.format(seconds) + "");
				}
			}
		}

	};

	Thread task = new Thread() {
		@Override
		public void run() {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHandler.obtainMessage(TIMER_MSG).sendToTarget();
		}
	};
}
