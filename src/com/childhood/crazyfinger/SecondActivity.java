package com.childhood.crazyfinger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.waps.AppConnect;

import com.umeng.analytics.MobclickAgent;

public class SecondActivity extends Activity implements OnClickListener {

	Button btnRetry;
	Button btnShare;
	TextView tvScore;
	TextView tvBestScore;
	SharedPreferences sp;
	int score;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		if (AppConnect.getInstance(this).hasPopAd(this)) {
			AppConnect.getInstance(this).showPopAd(this);
		}

		sp = this.getSharedPreferences(Constant.PRE_FILE_NAME, 0);
		btnRetry = (Button) findViewById(R.id.btnRetry);
		btnRetry.setOnClickListener(this);

		btnShare = (Button) findViewById(R.id.btnShare);
		btnShare.setOnClickListener(this);

		tvScore = (TextView) findViewById(R.id.tvScore);
		tvScore.setText("最好成绩：" + sp.getInt(Constant.KEY_PRE_BEST_SORCE, 0)
				+ "次");

		tvBestScore = (TextView) findViewById(R.id.tvCurrentScore);
		tvBestScore.setText("当前成绩："
				+ this.getIntent().getIntExtra(Constant.KEY_EXTRA_SORCE, 0)
				+ "次");
		score = sp.getInt(Constant.KEY_PRE_BEST_SORCE, 0);
		// 互动广告调用方式
		LinearLayout adContainer = (LinearLayout) findViewById(R.id.AdLinearLayout);
		AppConnect.getInstance(this).setAdForeColor(Color.BLACK);
		AppConnect.getInstance(this).showBannerAd(this, adContainer);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnRetry:
			MobclickAgent.onEvent(this, "TRY_AGAIN_CLICK");
			finish();
			break;
		case R.id.btnShare:
			MobclickAgent.onEvent(this, "SHARE_CLICK");
			showShare();
			break;
		}
	}

	private void showShare() {
		String share_url = MobclickAgent.getConfigParams(this, "share_url");
		if (TextUtils.isEmpty(share_url)) {
			share_url = "http://www.weibo.com/kangyi";
		}
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.ic_launcher,
				getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(share_url);
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我在#疯狂的手指#中点击了" + score + "次，你敢挑战我吗？\n游戏下载地址：" + share_url);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath(Constant.ICON_PATH + "crazy_finger.png");
		// Log.i("kangyi", Constant.ICON_PATH + "crazy_finger.png");
		// oks.setImagePath(this.getFilesDir() + "/crazy_finger.png");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(share_url);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("挑战指尖的极限。");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(share_url);

		// 启动分享GUI
		oks.show(this);
	}
}
