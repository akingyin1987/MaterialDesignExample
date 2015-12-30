package com.aswifter.material.book;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.aswifter.material.R;
import com.aswifter.material.example.DetailFragment;
import com.aswifter.material.net.BookServerApi;
import com.aswifter.material.net.RetrofitUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Chenyc on 15/7/1.
 */
public class BookDetailActivity extends AppCompatActivity {


    private ViewPager mViewPager;
    private  Book mBook;
    private BookServerApi   serverApi;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appbar_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mBook = (Book) getIntent().getSerializableExtra("book");
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mBook.getTitle());

        ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
        Glide.with(ivImage.getContext())
                .load(mBook.getImages().getLarge())
                .fitCenter()
                .into(ivImage);
        serverApi = RetrofitUtils.createApi(this,BookServerApi.class);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

         tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);


        loadBookInfo(mBook.getId());
    }

    public   void   loadBookInfo(Integer  id){
        Observable.just(id).map(new Func1<Integer, Book>() {
            @Override
            public Book call(Integer integer) {

                try {
                   return serverApi.findBookById(integer).execute().body();
                }catch (Exception e){
                    e.printStackTrace();
                }

                    return null;
                }
            }

            ).
            observeOn(AndroidSchedulers.mainThread()
            )
                .
            subscribeOn(Schedulers.newThread()

            ).

            subscribe(new Action1<Book>() {
                          @Override
                          public void call(Book book) {
                              mBook = book;
                              setupViewPager(mViewPager);
                              tabLayout.addTab(tabLayout.newTab().setText("内容简介"));
                              tabLayout.addTab(tabLayout.newTab().setText("作者简介"));
                              tabLayout.addTab(tabLayout.newTab().setText("目录"));
                              tabLayout.setupWithViewPager(mViewPager);
                          }
                      }
            );
        }


    private void setupViewPager(ViewPager mViewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(DetailFragment .newInstance(mBook.getSummary(),false), "内容简介");
        adapter.addFragment(DetailFragment.newInstance(mBook.getAuthor_intro(), false), "作者简介");
        adapter.addFragment(DetailFragment.newInstance(mBook.getCatalog(),false), "目录");
        adapter.addFragment(DetailFragment.newInstance(mBook.getId(),true),"评论");
        mViewPager.setAdapter(adapter);
    }


    static class MyPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}
