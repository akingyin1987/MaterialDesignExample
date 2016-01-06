package com.zlcdgroup.photos;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.aswifter.material.R;

public class ListItem extends LinearLayout {

	
	
	public ListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public ListItem(Context context) {
		super(context);
		initView(context);
	}
	
	private void initView(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.list_item, null);
		
		addView(view);
	}
	
}
