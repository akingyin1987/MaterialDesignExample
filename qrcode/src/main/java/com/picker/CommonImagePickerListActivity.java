/*
 * Copyright (c) 2015 [1076559197@qq.com | tchen0707@gmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.picker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.adapter.ListViewDataAdapter;
import com.adapter.ViewHolderBase;
import com.adapter.ViewHolderCreator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qrcode.R;
import com.qrcode.utils.CommonUtils;
import com.util.ImageLoaderHelper;

import java.util.List;


/**
 * Author:  Tau.Chen
 * Email:   1076559197@qq.com | tauchen1990@gmail.com
 * Date:    15/8/10 14:12
 * PkgName: com.github.obsessive.simplifyreader.ui.activity.picker
 * Description:
 */
public class CommonImagePickerListActivity extends AppCompatActivity {

    private static final int IMAGE_PICKER_DETAIL_REQUEST_CODE = 200;

    public static final String KEY_BUNDLE_ALBUM_PATH = "KEY_BUNDLE_ALBUM_PATH";
    public static final String KEY_BUNDLE_ALBUM_NAME = "KEY_BUNDLE_ALBUM_NAME";


    ListView mImagePickerListView;

    private ListViewDataAdapter<ImageBucket> mListViewAdapter = null;
    private AsyncTask<Void, Void, List<ImageBucket>> mAlbumLoadTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_image_picker_list);
        mImagePickerListView = (ListView)findViewById(R.id.common_image_picker_list_view);
        initViewsAndEvents();
    }




    protected void initViewsAndEvents() {


        mListViewAdapter = new ListViewDataAdapter<ImageBucket>(new ViewHolderCreator<ImageBucket>() {
            @Override
            public ViewHolderBase<ImageBucket> createViewHolder(int position) {
                return new ViewHolderBase<ImageBucket>() {

                    ImageView mItemImage;
                    TextView mItemTitle;

                    @Override
                    public View createView(LayoutInflater layoutInflater) {
                        View convertView = layoutInflater.inflate(R.layout
                                .list_item_common_image_picker, null);
                        mItemImage = (ImageView)convertView.findViewById(R.id
                                .list_item_common_image_picker_thumbnail);
                        mItemTitle =(TextView)convertView.findViewById( R.id
                                .list_item_common_image_picker_title);
                        return convertView;
                    }

                    @Override
                    public void showData(int position, ImageBucket itemData) {
                        if (null != itemData) {
                            String imagePath = itemData.bucketList.get(0).getImagePath();
                            if (!CommonUtils.isEmpty(imagePath)) {
                                ImageLoader.getInstance().displayImage("file://" + imagePath,
                                        mItemImage,
                                        ImageLoaderHelper.getInstance(CommonImagePickerListActivity.this).getDisplayOptions());
                            }

                            int count = itemData.count;
                            String title = itemData.bucketName;

                            if (!CommonUtils.isEmpty(title)) {
                                mItemTitle.setText(title + "(" + count + ")");
                            }
                        }
                    }
                };
            }
        });
        mImagePickerListView.setAdapter(mListViewAdapter);

        mImagePickerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mListViewAdapter && null != mListViewAdapter.getDataList() &&
                    !mListViewAdapter.getDataList().isEmpty() &&
                    position < mListViewAdapter.getDataList().size()) {

                    Bundle extras = new Bundle();
                    extras.putParcelableArrayList(KEY_BUNDLE_ALBUM_PATH, mListViewAdapter
                        .getDataList().get(position).bucketList);
                    extras.putString(KEY_BUNDLE_ALBUM_NAME, mListViewAdapter.getDataList().get
                        (position).bucketName);

                    readyGoForResult(CommonImagePickerDetailActivity.class,
                        IMAGE_PICKER_DETAIL_REQUEST_CODE, extras);
                }
            }
        });

        mAlbumLoadTask = new AsyncTask<Void, Void, List<ImageBucket>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                ImagePickerHelper.getHelper().init(CommonImagePickerListActivity.this);
            }

            @Override
            protected List<ImageBucket> doInBackground(Void... params) {
                return ImagePickerHelper.getHelper().getImagesBucketList();
            }

            @Override
            protected void onPostExecute(List<ImageBucket> list) {


                mListViewAdapter.getDataList().addAll(list);
                mListViewAdapter.notifyDataSetChanged();
            }
        };

        mAlbumLoadTask.execute();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mAlbumLoadTask && !mAlbumLoadTask.isCancelled()) {
            mAlbumLoadTask.cancel(true);
            mAlbumLoadTask = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == IMAGE_PICKER_DETAIL_REQUEST_CODE) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    /**
     * startActivityForResult with bundle
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }
}
