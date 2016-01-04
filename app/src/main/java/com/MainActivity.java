package com;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;

import com.aswifter.material.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.view.ClipImageLayout;
/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 * @author zhy
 *
 */
public class MainActivity extends Activity
{
	private ClipImageLayout mClipImageLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crile_image);

		mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
		ImageLoader  imageLoader =  ImageLoader.getInstance();
		String   path ="file://"+ Environment.getExternalStorageDirectory().getAbsolutePath();
		mClipImageLayout.setImageRes(path+File.separator+"1.jpg");


	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.id_action_clip:
			Bitmap bitmap = mClipImageLayout.clip();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
			byte[] datas = baos.toByteArray();
			
			Intent intent = new Intent(this, ShowImageActivity.class);
			intent.putExtra("bitmap", datas);
			startActivity(intent);

			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
