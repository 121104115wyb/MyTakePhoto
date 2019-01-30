package com.btzh.mytakephoto.view.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.btzh.androidphpfiles.R;

import java.util.ArrayList;

public class TestMultiActivity extends Activity {
    //该显示几个数据的内容
    final String string[] = new String[]{"老鼠", "猫", "鸭子", "鹅", "田鸡"};
    //默认情况下，哪些内容会被勾选
    final boolean b[] = new boolean[]{false, false, false, false, false};
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> list1 = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_multi_activity);
        addList();
    }

    void addList() {
        for (int i = 0; i < 20; i++) {
            list.add("add" + i);
        }
        System.out.println("------------------>list" + list);

        for (int i = 0; i < 19; i++) {
            list1.add("add" + i);
        }
        System.out.println("------------------>list1--" + list1);

        list.removeAll(list1);
        System.out.println("------------------>" + list);

    }

    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("你喜欢什么动物？");

        builder.setMultiChoiceItems(string, b, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            //which 为用户点击的下标
            //isChecked用户是否被勾选中
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                //    将客户是否被勾选的记录保存到集合中
                b[which] = isChecked;  //保存客户选择的属性是否被勾选
            }
        });
        //设置一个确定和取消按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            //保存数据
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String item = "";
                //取出被勾选中的内容
                for (int i = 0; i < 5; i++) {
                    if (b[i]) {             //如果被勾线则保存数据
                        item += string[i] + ",";
                    }
                }
                if (item.endsWith(",")) {
                    item = item.substring(0, item.length() - 1);
                }
                Toast.makeText(TestMultiActivity.this, item, Toast.LENGTH_SHORT).show();
                //对话框消失
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
