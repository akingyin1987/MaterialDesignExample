package me.nereo.multi_image_selector.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.nereo.multi_image_selector.MultiImageSelectorFragment;
import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.bean.Image;


/**
 * Created by zlcd on 2016/1/5.
 */
public class ImageViewPageAdapter  extends PagerAdapter {

    public   Context   context;

    public ViewPager   viewPager;

    public List<Image>   images = new ArrayList<>();

    public   ViewHolder[]  mImageViews  = new ViewHolder[4];

    private List<Image> mSelectedImages = new ArrayList<>();

    public List<Image> getmSelectedImages() {
        return mSelectedImages;
    }

    public void setmSelectedImages(List<Image> mSelectedImages) {
        this.mSelectedImages = mSelectedImages;
    }

    private MultiImageSelectorFragment.Callback   callback;

    public MultiImageSelectorFragment.Callback getCallback() {
        return callback;
    }

    public void setCallback(MultiImageSelectorFragment.Callback callback) {
        this.callback = callback;
    }

    private    int    maxSize;

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public   void   addSelectedImages(List<Image> images){
        mSelectedImages.addAll(images);
    }

    public ImageViewPageAdapter(Context context,List<Image> images) {
        this.context = context;
        if(null != images){
            this.images = images;
        }



    }

    public   void  setData(List<Image>  images){
        this.images = images;
        notifyDataSetChanged();
    }

    public  void   addSelectItem(Image  item){
       for(Image  image : mSelectedImages){
           if(item.equals(image)){
               break;
           }
       }
        mSelectedImages.add(item);
    }

    public  void  removeSelectItem(Image item){
        for (Image  image : mSelectedImages){
            if(item.equals(image)){
                mSelectedImages.remove(image);
                break;
            }
        }
    }


    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewHolder   holder = mImageViews[position % mImageViews.length];
        if(null != holder){
            container.removeView(holder.view);
        }

    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {

      ViewHolder  holde = mImageViews[position % mImageViews.length];
       final Image  image = images.get(position);
        if(null == holde){
            View   view = LayoutInflater.from(context).inflate(R.layout.list_item_bigimage,null);
            holde = new ViewHolder();
            holde.view = view;
            holde.checkBox = (CheckBox)view.findViewById(R.id.checkmark);

            holde.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(Image  item : mSelectedImages){
                        if(item.equals(image)){
                            mSelectedImages.remove(item);
                            if(null != callback){
                                callback.onImageSelected(null);
                            }
                            return;
                        }
                    }
                    if(mSelectedImages.size() >= maxSize){
                        if(null != callback){
                            callback.onImageUnselected(null);
                        }
                        return;
                    }
                    mSelectedImages.add(image);
                    System.out.println("不存在");
                    if(null != callback){
                        callback.onImageSelected(null);
                    }

                }
            });
            holde.imageView=(ImageView)view.findViewById(R.id.image);
            ((ViewPager)container).addView(view, 0);
            mImageViews[position % mImageViews.length]=holde;
        } else {
            ((ViewPager)container).removeView(holde.view);
            ((ViewPager)container).addView(holde.view, 0);
        }
        if(mSelectedImages.contains(image)){
            holde.checkBox.setChecked(true);
        }else{
            holde.checkBox.setChecked(false);
        }
        Picasso.with(context)
            .load(new File(image.path))
            .placeholder(R.drawable.default_error)
            .error(R.drawable.default_error)
            .fit()
            .tag(context)
            .into(holde.imageView);
        return  holde.view;
    }

    /**
     * 选择某个图片，改变选择状态
     * @param image
     */
    public void select(Image image) {
        if(mSelectedImages.contains(image)){
            mSelectedImages.remove(image);
        }else{
            mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }


    public   static  class  ViewHolder{

        View    view;

        CheckBox   checkBox;

        ImageView   imageView;
    }
}
