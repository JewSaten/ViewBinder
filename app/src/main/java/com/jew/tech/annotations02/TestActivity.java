package com.jew.tech.annotations02;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jew.tech.bindview.BinderView;
import com.jew.tech.bindview.annotations.BindView;
import com.jew.tech.bindview.annotations.OnClick;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.btn2) Button btn;

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        BinderView.inject(this);
    }

    @OnClick(R.id.btn2) void onBtnClick(){
        Toast.makeText(this, "bindClick", Toast.LENGTH_LONG).show();
    }
}
