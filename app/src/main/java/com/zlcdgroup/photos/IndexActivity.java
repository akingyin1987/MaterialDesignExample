package com.zlcdgroup.photos;
import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aswifter.material.R;

public class IndexActivity extends Activity {

	public static ArrayList<String> mPicList;
	private ImageView thumb_img;
	TextView count;

	static String CAMERA_PATH = Environment
		.getExternalStorageDirectory().getPath() + "/testPhoto";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index_layout);
		initView();
		initData();
		setListener();
	}

	private void initData() {
		mPicList = new ArrayList<String>();
		File dir_camera = new File(CAMERA_PATH);
		if (!dir_camera.exists() && !dir_camera.isDirectory()) {
			boolean mkdir = dir_camera.mkdir();
			Log.e("cxm", "mkdir="+mkdir);
		}
	}

	private void setListener() {
		thumb_img.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(IndexActivity.this, SelectPhotoActivity.class), 100);
			}
		});
	}

	private void initView() {
		thumb_img = (ImageView) findViewById(R.id.thumb_img);
		count = (TextView) findViewById(R.id.count);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK) {
			if(100 == requestCode) {
				Log.e("cxm", "----------------------");
				//判断返回的数据
				ArrayList<String> pic_filse = data.getExtras().getStringArrayList("pic_paths");
				//显示到页面上
				if(!pic_filse.isEmpty()) {
					mPicList.addAll(pic_filse);
					count.setText(mPicList.size()+"张");
					//显示最后一张
					String pic_path = mPicList.get(mPicList.size()-1);
					try {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inSampleSize = 2;
						final Bitmap takeBitmap = BitmapFactory.decodeFile(pic_path,
							options);
						File f = new File(pic_path);
						thumb_img.setImageBitmap(takeBitmap);
					} catch (OutOfMemoryError e) {
						// TODO: handle exception
					}
				}
			}
		}
	}
}
