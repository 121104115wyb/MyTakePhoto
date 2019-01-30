package com.btzh.mytakephoto.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.btzh.androidphpfiles.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main2Activity extends SimpleImage {

    @BindView(R.id.addressIp)
    TextView addressIp;
    @BindView(R.id.addphoto)
    Button addphoto;
    @BindView(R.id.photo_rcycle)
    RecyclerView photoRcycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        initPhotoView(photoRcycle);
    }


    public void onClick(View view) {
        showImageDialog("test");
    }

    public void clickMulti(View view) {
        startActivity(new Intent(this,TestMultiActivity.class));
    }



}
