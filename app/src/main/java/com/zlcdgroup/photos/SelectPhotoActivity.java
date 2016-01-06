package com.zlcdgroup.photos;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aswifter.material.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SelectPhotoActivity extends Activity {


	private GridView gridview;
	GridAdapter gridAdatper;

	TextView group_text, total_text;
	ListView group_listview;

	private ProgressDialog mProgressDialog;

	private ProgressDialog mDirDialog;
	private ImageLoader mImageLoader;

	private HashMap<String, ArrayList<String>> mGruopMap = new HashMap<String, ArrayList<String>>();
	private ArrayList<ImageBean> imgBeanLists = new ArrayList<ImageBean>();

	// 所有的图片
	private ArrayList<String> mAllImgs;
	private final static int SCAN_OK = 1;

	private final static int SCAN_FOLDER_OK = 2;
	private RelativeLayout list_layout;
	private DisplayImageOptions options;

	private ListAdapter listAdapter;

	private int limit_count ;

	public    static   boolean  isSingle=false;//是否是单选

	Animation toUp, toDown;

	// private GridAdapter gridAdatper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photos_main);
		initView();
		initData();
		setListener();
	}

	private void setListener() {

		total_text.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//addedPath返回给上个页面-----这里只选择相册里的
				if(!isSingle){
					Intent dataIntent = new Intent();
					Bundle dataBundle = new Bundle();
					dataBundle.putStringArrayList("pic_paths", addedPath);
					dataIntent.putExtras(dataBundle);
					setResult(RESULT_OK, dataIntent);
					SelectPhotoActivity.this.finish();
				}

			}
		});

		group_text.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (list_layout.getVisibility() == View.VISIBLE) {
					list_layout.setVisibility(View.GONE);
					list_layout.startAnimation(toDown);
				} else {
					list_layout.setVisibility(View.VISIBLE);
					list_layout.startAnimation(toUp);
				}
			}
		});

		group_listview
			.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
										int position, long arg3) {
					// 点击刷新对应的视图
					if (chooseItem.get(0) == position) {
						// 不做操作，返回
						list_layout.setVisibility(View.GONE);
					} else {
						if(isSingle && listAdapter.getItem(position).getImageCounts() == 1){
							Intent dataIntent = new Intent();
							Bundle dataBundle = new Bundle();
							dataBundle.putString("result", listAdapter.getItem(position).getTopImagePath());
							dataIntent.putExtras(dataBundle);
							setResult(RESULT_OK, dataIntent);
							SelectPhotoActivity.this.finish();
							return;
						}
						chooseItem.clear();
						chooseItem.add(position);
						listAdapter.notifyDataSetChanged();
						list_layout.setVisibility(View.GONE);

						// 获取到mAllImgs；并显示到数据中

						gridAdatper.setData(new ArrayList<String>());

						// 得到当前的来刷新
						if (0 == position) {
							getImages();
						} else {
							// 刷新当前的GridView
							mDirDialog = ProgressDialog.show(SelectPhotoActivity.this, null, "正在加载...");
							nowStrs.clear();
							String fa_path = imgBeanLists.get(position).getFa_filepath();
							nowStrs.addAll(mGruopMap.get(fa_path));
							Log.e("cxm", "fa_path="+fa_path+",nowStrs.size="+nowStrs.size());
							mHandler.sendEmptyMessageDelayed(SCAN_FOLDER_OK, 1000);
//								// 通知Handler扫描图片完成
//								getFolderImages(imageBean.getFa_filepath());
						}
					}
				}
			});

		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
									long arg3) {
				if(chooseItem.get(0) == 0 && 0 == position) {
					//调用系统相机
					//判断是否已满8张图片
					if(addedPath.size() >= limit_count) {
						Toast.makeText(SelectPhotoActivity.this, "最多选8张，请取消后再点击拍照", Toast.LENGTH_SHORT).show();
						return;
					}

					tempCameraPath = IndexActivity.CAMERA_PATH + "/"
						+ UUID.randomUUID().toString() + ".jpg";
					Log.e("cxm", "path============"+tempCameraPath);
					PickPhotoUtil.getInstance().takePhoto(
						SelectPhotoActivity.this, "tempUser", tempCameraPath);
				}else{
					if(isSingle){
						Intent dataIntent = new Intent();
						Bundle dataBundle = new Bundle();
						dataBundle.putString("result",gridAdatper.getItem(position));
						dataIntent.putExtras(dataBundle);
						setResult(RESULT_OK, dataIntent);
						SelectPhotoActivity.this.finish();
					}else{
						addedPath.add(gridAdatper.getItem(position));
						mYhandler.sendEmptyMessage(0);
						gridAdatper.notifyDataSetChanged();

					}
				}
			}
		});
	}

	private static  String tempCameraPath = "";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK) {
			switch (requestCode) {
				case PickPhotoUtil.PickPhotoCode.PICKPHOTO_TAKE:

					File fi = new File("");
					PickPhotoUtil.getInstance().takeResult(this,
						data, fi);

					//相机的图片

					Intent dataIntent = new Intent();
					Bundle dataBundle = new Bundle();
					dataBundle.putString("result",tempCameraPath);
					dataIntent.putExtras(dataBundle);
					setResult(RESULT_OK, dataIntent);
					SelectPhotoActivity.this.finish();
					break;

				default:
					break;
			}
		}
	}



	ArrayList<String> nowStrs = new ArrayList<>();



	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case SCAN_OK:
					// 关闭进度条
					mProgressDialog.dismiss();

					imgBeanLists = subGroupOfImage(mGruopMap);
					listAdapter = new ListAdapter();
					listAdapter.setData(imgBeanLists);
					group_listview.setAdapter(listAdapter);
					// 获取到mAllImgs；并显示到数据中
					 gridAdatper = new GridAdapter();
					gridAdatper.setData(mAllImgs);
					gridview.setAdapter(gridAdatper);

					break;
				case SCAN_FOLDER_OK:
					mDirDialog.dismiss();
					// 获取到mAllImgs；并显示到数据中
					gridAdatper = new GridAdapter();
					gridAdatper.setData(nowStrs);
					gridview.setAdapter(gridAdatper);

					break;
			}
		}

	};

	private void initView() {
		gridview = (GridView) findViewById(R.id.gridview);
		group_text = (TextView) findViewById(R.id.group_text);
		total_text = (TextView) findViewById(R.id.total_text);
		group_listview = (ListView) findViewById(R.id.group_listview);

		list_layout = (RelativeLayout) findViewById(R.id.list_layout);
	}

	private void initData() {
		// 初始化数据，所有图片应在281张以内
		chooseItem.add(0);

		mImageLoader = ImageLoader.getInstance();

		options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.mipmap.friends_sends_pictures_no)
			.showImageOnFail(R.mipmap.friends_sends_pictures_no)
			.build();

		mAllImgs = new ArrayList<String>(281);
		addedPath = new ArrayList<String>();
		limit_count = 8;
		total_text.setText("0/"+limit_count+"张");
		toUp = AnimationUtils.loadAnimation(SelectPhotoActivity.this, R.anim.act_bottom_to_top);
		toDown = AnimationUtils.loadAnimation(SelectPhotoActivity.this, R.anim.act_top_to_bottom);
		// listAdapter = new ListAdapter();
		// group_listview.setAdapter(listAdapter);
		//
		// gridAdatper = new GridAdapter();
		// gridview.setAdapter(gridAdatper);
		getImages();
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
			Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}

		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		new Thread(new Runnable() {

			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = SelectPhotoActivity.this
					.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
					MediaStore.Images.Media.MIME_TYPE + "=? or "
						+ MediaStore.Images.Media.MIME_TYPE + "=?",
					new String[] { "image/jpeg", "image/png" },
					MediaStore.Images.Media.DATE_MODIFIED);

				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor
						.getColumnIndex(MediaStore.Images.Media.DATA));

					// 获取该图片的父路径名
					File pa_file = new File(path).getParentFile();
					String parentName = pa_file.getAbsolutePath();
					if (mAllImgs.size() < 281) {
						mAllImgs.add(path);
					}
					// 根据父路径名将图片放入到mGruopMap中
					if (!mGruopMap.containsKey(parentName)) {
						ArrayList<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						mGruopMap.get(parentName).add(path);
					}
				}
				mCursor.close();
				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(SCAN_OK);

			}
		}).start();

	}

	/**
	 * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中 所以需要遍历HashMap将数据组装成List
	 *
	 * @param
	 * @return
	 */
	private ArrayList<ImageBean> subGroupOfImage(
		HashMap<String, ArrayList<String>> gruopMap) {
		if (gruopMap.size() == 0) {
			return null;
		}
		ArrayList<ImageBean> list = new ArrayList<ImageBean>();
		Iterator<Map.Entry<String, ArrayList<String>>> it = gruopMap.entrySet()
			.iterator();
		ImageBean ig0 = new ImageBean();
		ig0.setFolderName("所有图片");
		ig0.setImageCounts(0);
		ig0.setTopImagePath("");
		list.add(0, ig0);
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();
			File dir_file = new File(key);
			mImageBean.setFolderName(dir_file.getName());
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));// 获取该组的第一张图片
			mImageBean.setFa_filepath(key);
			list.add(mImageBean);
		}

		return list;

	}

	private ArrayList<String> addedPath = null;

	// gridview的Adapter
	class GridAdapter extends BaseAdapter {
		// 根据三种不同的布局来应用
		final int VIEW_TYPE = 2;
		final int TYPE_1 = 0;
		final int TYPE_2 = 1;
		LayoutInflater inflater;
		private ArrayList<String> gridStrings;/**
		 * 用来存储图片的选中情况
		 */
		private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();

		public GridAdapter() {
			gridStrings = new ArrayList<>();
			inflater = LayoutInflater.from(SelectPhotoActivity.this);
		}

		public void setData(ArrayList<String> strs) {
			if (null != strs) {
				gridStrings.clear();
				gridStrings.addAll(strs);
				notifyDataSetChanged();
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return gridStrings.size();
		}

		@Override
		public String getItem(int position) {
			if (chooseItem.get(0) == 0) {
				return gridStrings.get(position - 1);
			} else {
				Log.e("cxm", "position===="+position+",path="+gridStrings.get(position));
				return gridStrings.get(position);
			}
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			if (chooseItem.get(0) == 0) {
				if (position == 0) {
					return TYPE_1;
				} else {
					return TYPE_2;
				}
			} else {
				return TYPE_2;
			}
		}

		@Override
		public int getViewTypeCount() {
			if (chooseItem.get(0) == 0) {
				return VIEW_TYPE;
			} else {
				return 1;
			}
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			GridHolder gridHolder = null  ;
			PhotoHolder photoHodler = null;
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
					case TYPE_1:
						// 显示拍照
						photoHodler = new PhotoHolder();
						convertView = inflater.inflate(R.layout.take_photo, null);
						convertView.setTag(photoHodler);
						break;
					case TYPE_2:
						convertView = inflater.inflate(R.layout.grid_item, null);
						gridHolder = new GridHolder();
						gridHolder.grid_image = (ImageView) convertView
							.findViewById(R.id.grid_image);
						gridHolder.grid_img = (ImageView) convertView
							.findViewById(R.id.grid_img);
						convertView.setTag(gridHolder);
						break;
					default:
						break;
				}
			} else {
				switch (type) {
					case TYPE_1:
						// 显示拍照
						photoHodler = (PhotoHolder) convertView.getTag();
						break;
					case TYPE_2:
						gridHolder = (GridHolder) convertView.getTag();
						break;
					default:
						break;
				}
			}

			if (type == TYPE_2) {
				// 判断是否已经添加
				mImageLoader.displayImage("file://" + getItem(position),
					gridHolder.grid_image, options);


				gridHolder.grid_img.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						if(addedPath.contains(getItem(position))) {
							//已经包含这个path了，则干掉
							addedPath.remove(getItem(position));
							((ImageView)view).setImageResource(R.mipmap.friends_sends_pictures_select_icon_unselected);
						} else {
							//判断大小
							if(addedPath.size() < limit_count) {
								addedPath.add(getItem(position));
								((ImageView)view).setImageResource(R.mipmap.friends_sends_pictures_select_icon_selected);
								//添加图片，显示出来张数
							}
						}
						mYhandler.sendEmptyMessage(0);
					}
				});

				if (addedPath.contains(getItem(position))) {
					// 已经添加过了
					gridHolder.grid_img.setImageResource(R.mipmap.friends_sends_pictures_select_icon_selected);
				} else {
					gridHolder.grid_img.setImageResource(R.mipmap.friends_sends_pictures_select_icon_unselected);
				}
			}

			return convertView;
		}

		class PhotoHolder {

		}

		class GridHolder {
			ImageView grid_image;
			public ImageView grid_img;
		}

	}

	Handler mYhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					total_text.setText(addedPath.size()+"/"+limit_count+"张");
					break;

				default:
					break;
			}
		}
	};

	private ArrayList<Integer> chooseItem = new ArrayList<>();

	class ListAdapter extends BaseAdapter {
		private ArrayList<ImageBean> beans = null;
		LayoutInflater inflater;

		public ListAdapter() {
			inflater = LayoutInflater.from(SelectPhotoActivity.this);
			beans = new ArrayList<>();
		}

		public void setData(ArrayList<ImageBean> listBeans) {
			if (listBeans != null) {
				beans.clear();
				beans.addAll(listBeans);
				notifyDataSetChanged();
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return beans.size();
		}

		@Override
		public ImageBean getItem(int arg0) {
			// TODO Auto-generated method stub
			return beans.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			final ListViewHolder listHoder;
			ImageBean imageBean = beans.get(position);
			if (convertView == null) {
				listHoder = new ListViewHolder();
				convertView = inflater.inflate(R.layout.list_item, null);
				listHoder.myimage_view = (ImageView) convertView
					.findViewById(R.id.myimage_view);
				listHoder.choose_img = (ImageView) convertView
					.findViewById(R.id.choose_img);
				listHoder.folder_text = (TextView) convertView
					.findViewById(R.id.folder_text);
				listHoder.count_text = (TextView) convertView
					.findViewById(R.id.count_text);
				convertView.setTag(listHoder);
			} else {
				listHoder = (ListViewHolder) convertView.getTag();
			}
			int cho_posi = chooseItem.get(0);
			if (position == cho_posi) {
				// 相等则显示
				listHoder.choose_img.setVisibility(View.VISIBLE);
			} else {
				listHoder.choose_img.setVisibility(View.GONE);
			}
			String img_path = "";
			if (position == 0) {
				img_path = beans.get(1).getTopImagePath();
				listHoder.count_text.setVisibility(View.GONE);
			} else {
				img_path = imageBean.getTopImagePath();
				listHoder.count_text.setVisibility(View.VISIBLE);
				listHoder.count_text.setText(imageBean.getImageCounts()+"张");
			}
			listHoder.folder_text.setText(imageBean.getFolderName());
			mImageLoader.displayImage("file://" + img_path,
				listHoder.myimage_view, options);
			return convertView;
		}

		class ListViewHolder {
			ImageView myimage_view;
			ImageView choose_img;
			TextView folder_text, count_text;
		}
	}
}
