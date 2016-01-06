package me.nereo.multi_image_selector;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.adapter.ImageViewPageAdapter;

import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.bean.Images;

/**
 * Created by zlcd on 2016/1/5.
 */
public class MulitImageBrowseActivity  extends FragmentActivity implements MultiImageSelectorFragment.Callback {

    private boolean hasFolderGened = false;
    private boolean mIsShowCamera = false;

    // 不同loader定义
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;

    public List<Image>   imageList = new ArrayList<>();

    public ImageViewPageAdapter   adapter;

    public ViewPager    viewPager;

    public TextView  tv_left;

    public Button   commit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Images   images = (Images)getIntent().getSerializableExtra("selected");
        Images   preiveimages = (Images)getIntent().getSerializableExtra("previe");
        Image   temp = (Image)getIntent().getSerializableExtra("frist");
        setContentView(R.layout.activity_viewpage_select);
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        tv_left = (TextView)findViewById(R.id.tv_left);
        commit = (Button)findViewById(R.id.commit);

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Images images1 = new Images();
                images1.setImages(adapter.getmSelectedImages());
                Intent intent = new Intent();
                intent.putExtra("result", images1);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_left.setText((position + 1) + "/" + adapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        adapter=new ImageViewPageAdapter(this,null);
        viewPager.setAdapter(adapter);
        adapter.setCallback(this);
        adapter.setMaxSize(MultiImageSelectorFragment.mDesireImageCount);
        adapter.setmSelectedImages(images.getImages());
        if(MultiImageSelectorFragment.mode == MultiImageSelectorFragment.MODE_SINGLE){
            commit.setText("使用");
        }else{
            commit.setText("完成("+images.getImages().size()+"/"+MultiImageSelectorFragment.mDesireImageCount+")");
        }
        System.out.println("prive="+preiveimages.getImages().size());
        adapter.setData(preiveimages.getImages());
        System.out.println("tem="+(null ==temp));
        if(adapter.getmSelectedImages().size()>0){
            int postion = getSelectPostion(preiveimages.getImages(),null == temp?adapter.getmSelectedImages().get(0):temp);
            viewPager.setCurrentItem(postion);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




    public   int   getSelectPostion(List<Image> items,Image  image){
        int  postion = 0;
        for(Image  image1 : items){
            if(image1.equals(image)){
                return  postion;
            }
            postion++;
        }
        return  postion;

    }


    @Override
    public void onSingleImageSelected(String path) {

    }

    @Override
    public void onImageSelected(String path) {
        commit.setText("完成("+adapter.getmSelectedImages().size()+"/"+MultiImageSelectorFragment.mDesireImageCount+")");
    }

    @Override
    public void onImageUnselected(String path) {
        Toast.makeText(this,"选择数已超过最大数!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraShot(File imageFile) {

    }

    @Override
    public void onAllResult(List<Image> images) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("返回");
        if(keyCode == KeyEvent.KEYCODE_BACK ){
            System.out.println("返回");
            Images images1 = new Images();
            images1.setImages(adapter.getmSelectedImages());
            Intent intent = new Intent();
            intent.putExtra("result", images1);
            setResult(RESULT_CANCELED, intent);
            finish();

            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
