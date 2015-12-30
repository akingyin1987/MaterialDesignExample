package com.aswifter.material.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aswifter.material.R;
import com.aswifter.material.net.BookServerApi;
import com.aswifter.material.net.RetrofitUtils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Chenyc on 2015/6/29.
 */
public class DetailFragment extends Fragment {

    public BookServerApi   bookServerApi;





    public static DetailFragment newInstance(String info,boolean  ishtml) {
        Bundle args = new Bundle();
        DetailFragment fragment = new DetailFragment();
        args.putString("info", info);
        args.putBoolean("html",ishtml);
        fragment.setArguments(args);
        return fragment;
    }
    public static DetailFragment newInstance(int  bookId,boolean  ishtml) {
        Bundle args = new Bundle();
        DetailFragment fragment = new DetailFragment();
        args.putInt("bookId", bookId);
        args.putBoolean("html", ishtml);
        fragment.setArguments(args);

        return fragment;
    }


    TextView tvInfo = null;
    int   bookId=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, null);
         tvInfo = (TextView) view.findViewById(R.id.tvInfo);
        boolean  ishtml = getArguments().getBoolean("html");
        if(ishtml){
            bookId = getArguments().getInt("bookId");
            bookServerApi = RetrofitUtils.createStringApi(getContext(), BookServerApi.class);
            Observable.just(bookId).map(new Func1<Integer, String>() {
                @Override
                public String call(Integer integer) {

                    try{
                        System.out.println("bookId="+bookId);
                        Object  obj = bookServerApi.findAllAnnotationsByBook(bookId,"html").execute().body();
                        return  obj.toString()  ;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if(!TextUtils.isEmpty(s)){
                            tvInfo.setText(Html.fromHtml(s));
                        }

                    }
                });
        }else{
            tvInfo.setText(getArguments().getString("info"));
        }

//        tvInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(v,"hello",Snackbar.LENGTH_SHORT).show();
//            }
//        });
        return view;
    }
}
