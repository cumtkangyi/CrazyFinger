package com.childhood.crazyfinger;

import java.text.DecimalFormat;
import java.util.Timer;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.waps.AppConnect;
import cn.waps.extend.QuitPopAd;
import cn.waps.extend.SlideWall;

import com.umeng.analytics.MobclickAgent;

public class MainActivity extends Activity implements OnClickListener {

	TextView tvDateTime;
	TextView tvTimes;
	TextView tvScore;

	Button btnFinger;

	RoundProgressBar roundProgressBar;

	int times;
	float seconds = 10.00f;

	Timer timer;
	boolean flag = false;
	boolean isRunning = false;

	TimeThread mTimeThread;

	SharedPreferences sp;
	private float progress = 0.2f;

	int num = 0;
	// 抽屉广告布局
	private View slidingDrawerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MobclickAgent.updateOnlineConfig(this);
		Util.getInstance().copyAssetsFileToSDcard(this);
		initView();

		AppConnect.getInstance(this);
		// 初始化卸载广告
		AppConnect.getInstance(this).initUninstallAd(this);
		// 禁用错误报告
		AppConnect.getInstance(this).setCrashReport(false);
		// 初始化自定义广告数据
		AppConnect.getInstance(this).initAdInfo();

		// 初始化插屏广告数据
		AppConnect.getInstance(this).initPopAd(this);

		// 抽屉式应用墙
		// 1,将drawable-hdpi文件夹中的图片全部拷贝到新工程的drawable-hdpi文件夹中
		// 2,将layout文件夹中的detail.xml和slidewall.xml两个文件，拷贝到新工程的layout文件夹中
		// 获取抽屉样式的自定义广告
		slidingDrawerView = SlideWall.getInstance().getView(this);
		// 获取抽屉样式的自定义广告,自定义handle距左边边距为150
		// slidingDrawerView = SlideWall.getInstance().getView(this, 150);
		// 获取抽屉样式的自定义广告,自定义列表中每个Item的宽度480,高度150
		// slidingDrawerView = SlideWall.getInstance().getView(this, 480,
		// 150);
		// 获取抽屉样式的自定义广告,自定义handle距左边边距为150,列表中每个Item的宽度480,高度150
		// slidingDrawerView = SlideWall.getInstance().getView(this, 150,
		// 480,
		// 150);

		if (slidingDrawerView != null) {
			this.addContentView(slidingDrawerView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}

		// 互动广告调用方式
		LinearLayout adContainer = (LinearLayout) findViewById(R.id.AdLinearLayout);
		AppConnect.getInstance(this).setAdForeColor(Color.BLACK);
		AppConnect.getInstance(this).showBannerAd(this, adContainer);

		sp = this.getSharedPreferences(Constant.PRE_FILE_NAME, 0);
		tvScore.setText("最好成绩： " + sp.getInt(Constant.KEY_PRE_BEST_SORCE, 0));
	}

	void initView() {
		tvDateTime = (TextView) findViewById(R.id.tvDateTime);
		tvTimes = (TextView) findViewById(R.id.tvTimes);
		tvScore = (TextView) findViewById(R.id.tvScore);

		btnFinger = (Button) findViewById(R.id.btnFinger);
		btnFinger.setOnClickListener(this);

		roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
	}

	@Override
	protected void onDestroy() {
		AppConnect.getInstance(this).close();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		btnFinger.setEnabled(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btnFinger) {
			times++;

			if (!flag) {
				seconds = 10;
				times = 0;
				progress = 0.2f;
				flag = true;
				isRunning = true;
				mTimeThread = new TimeThread();
				mTimeThread.start();
				Animation anim = AnimationUtils.loadAnimation(this,
						R.anim.rotate_anim);
				LinearInterpolator lir = new LinearInterpolator();
				anim.setInterpolator(lir);
				btnFinger.startAnimation(anim);
			}

			tvTimes.setText(times + "次");
		}
	}

	private static final int TIMER_MSG = 10000;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == TIMER_MSG) {
				if (!isRunning) {
					return;
				}
				num++;
				if (progress <= 100) {
					progress += 0.1;
				}
				if (BuildConfig.DEBUG) {
					Log.i("kangyi", "num = " + num);
					Log.i("kangyi", "progress = " + (int) progress);
				}
				seconds = seconds - 0.01f;
				roundProgressBar.setProgress((int) progress);
				if (seconds != 0) {
					tvDateTime.setText(new DecimalFormat("0.00").format(Math
							.abs(seconds)) + "秒");
				}
				if (seconds <= 0.01) {
					isRunning = false;
					flag = false;
					mTimeThread = null;
					int bestSorce = sp.getInt(Constant.KEY_PRE_BEST_SORCE, 0);
					if (times > bestSorce) {
						Editor edit = sp.edit();
						edit.putInt(Constant.KEY_PRE_BEST_SORCE, times);
						edit.commit();
					}
					tvScore.setText("最好成绩： "
							+ sp.getInt(Constant.KEY_PRE_BEST_SORCE, 0));
					btnFinger.setEnabled(false);
					Intent intent = new Intent(MainActivity.this,
							SecondActivity.class);
					intent.putExtra(Constant.KEY_EXTRA_SORCE, times);
					startActivity(intent);

					tvDateTime.setText("10.00 秒");
					tvTimes.setText("0 次");
					roundProgressBar.setProgress(0);
					if (BuildConfig.DEBUG) {
						Log.i("kangyi", "seconds = " + seconds);
					}
					return;
				}

			}
		}

	};

	class TimeThread extends Thread {
		@Override
		public void run() {
			while (isRunning) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				mHandler.obtainMessage(TIMER_MSG).sendToTarget();
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (SlideWall.getInstance().slideWallDrawer != null
					&& SlideWall.getInstance().slideWallDrawer.isOpened()) {

				// 如果抽屉式应用墙展示中，则关闭抽屉
				SlideWall.getInstance().closeSlidingDrawer();
			} else {
				// 调用退屏广告
				QuitPopAd.getInstance().show(this);
			}

		}
		return true;
	}

	// 建议加入onConfigurationChanged回调方法
	// 注:如果当前Activity没有设置android:configChanges属性,或者是固定横屏或竖屏模式,则不需要加入
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// 使用抽屉式应用墙,横竖屏状态切换时,重新加载抽屉,保证ListView重新加载,保证ListView中Item的布局匹配当前屏幕状态
		if (slidingDrawerView != null) {
			// 先remove掉slidingDrawerView
			((ViewGroup) slidingDrawerView.getParent())
					.removeView(slidingDrawerView);
			slidingDrawerView = null;
			// 重新获取抽屉样式布局,此时ListView重新设置了Adapter
			slidingDrawerView = SlideWall.getInstance().getView(this);
			if (slidingDrawerView != null) {
				this.addContentView(slidingDrawerView, new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			}
		}
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * 用于监听插屏广告的显示与关闭
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		Dialog dialog = AppConnect.getInstance(this).getPopAdDialog();
		if (dialog != null) {
			if (dialog.isShowing()) {
				// 插屏广告正在显示
			}
			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// 监听插屏广告关闭事件
				}
			});
		}
	}
}
