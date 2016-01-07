package com.aswifter.material;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.camera.CropImageIntentBuilder;
import com.aswifter.material.book.BooksFragment;
import com.aswifter.material.widget.BackHandledFragment;
import com.chat.ChatFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.zlcdgroup.photos.SelectPhotoActivity;

import java.io.File;
import java.util.List;


import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class MainActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private BackHandledFragment selectedFragment;
    private NavigationView mNavigationView;

    private static final int ANIM_DURATION_TOOLBAR = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        setupDrawerContent(mNavigationView);

        //profile Image


        switchToBook();
        setUpProfileImage();
    }


    private void switchToBook() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new BooksFragment()).commit();
        mToolbar.setTitle(R.string.navigation_book);
    }

    private void switchToExample() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new ExampleFragment()).commit();
        mToolbar.setTitle(R.string.navigation_example);
    }

    private void switchToBlog() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new BlogFragment()).commit();
        mToolbar.setTitle(R.string.navigation_my_blog);
    }

    public  void  switchToChat(){
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, ChatFragment.newInstance()).commit();
        mToolbar.setTitle(R.string.navigation_chat);
    }


    private void switchToAbout() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new AboutFragment()).commit();
        mToolbar.setTitle(R.string.navigation_about);
    }

    ImageView   imageView;
    private void setUpProfileImage() {
        View  headerView = mNavigationView.getHeaderView(0);

        imageView = (ImageView)headerView.findViewById(R.id.profile_image);
     //   System.out.println("image="+(null == findViewById(R.id.profile_image)));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // switchToBlog();
                mDrawerLayout.closeDrawers();
                Intent mintent = new Intent(MainActivity.this, com.MainActivity.class);
                startActivity(mintent);
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MaterialDialog.Builder(MainActivity.this).title("提示").content("确定要更换头像吗")
                    .autoDismiss(true)  // notice attr is used instead of none or res for attribute resolving
                  .positiveText("确定").negativeText("取消").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        SelectPhotoActivity.isSingle = true;

                        Intent   intent  =  new Intent(MainActivity.this, MultiImageSelectorActivity.class);
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE,MultiImageSelectorActivity.MODE_SINGLE);
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA,false);

                        startActivityForResult(intent, REQUEST_PICTURE);
                    }
                }).show();
                return true;
            }
        });
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {

                            case R.id.navigation_item_book:
                                switchToBook();
                                break;
                            case R.id.navigation_item_example:
                                switchToExample();
                                break;
                            case R.id.navigation_item_blog:
                                switchToBlog();
                                break;
                            case R.id.navigation_item_about:
                                switchToAbout();
                                break;
                            case R.id.navigation_item_chat:
                                switchToChat();
                                break;

                        }
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        this.selectedFragment = backHandledFragment;
    }


    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment == null || !selectedFragment.onBackPressed()) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                doExitApp();
            }
        }

    }

    private static int REQUEST_PICTURE = 1;
    private static int REQUEST_CROP_PICTURE = 2;
    private String  temp;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PICTURE){
            try{
                List<String>  result = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                Uri croppedImage = Uri.fromFile(new File(result.get(0)));
                temp = result.get(0);
                CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 200, croppedImage);
                cropImage.setOutlineColor(0xFF03A9F4);
                cropImage.setSourceImage(data.getData());

                startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);

            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(requestCode == REQUEST_CROP_PICTURE){
            ImageLoader   imageLoader = ImageLoader.getInstance();

                imageLoader.displayImage("file://"+temp,imageView);

        }
    }
}
