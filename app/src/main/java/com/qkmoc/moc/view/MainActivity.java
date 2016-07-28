package com.qkmoc.moc.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qkmoc.moc.R;
import com.qkmoc.moc.core.Service;

/**
 * Created by hugeterry(http://hugeterry.cn)
 * Date: 16/7/23 10:05
 */
public class MainActivity extends AppCompatActivity {
    private Intent intent;
    private Button bt_startService, bt_stopService;
    private TextView tv_phonenum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        if (intent == null) {
            intent = new Intent(MainActivity.this, Service.class);
//            intent.setAction("jp.co.cyberagent.stf.ACTION_START");
        }

        bt_startService = (Button) findViewById(R.id.bt_startService);
        bt_startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(intent);
            }
        });

        bt_stopService = (Button) findViewById(R.id.bt_stopService);
        bt_stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intent);
            }
        });

        tv_phonenum = (TextView) findViewById(R.id.tv_phonenum);
        tv_phonenum.setText("");

    }
}
