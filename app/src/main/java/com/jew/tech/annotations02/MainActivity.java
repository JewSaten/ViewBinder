package com.jew.tech.annotations02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.jew.tech.bindview.BinderView;
import com.jew.tech.bindview.annotations.BindView;
import com.jew.tech.bindview.annotations.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.btn)
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BinderView.inject(this);
        textView.setText("123");
        btn.setText("456");
    }

    @OnClick({R.id.btn, R.id.textView})
    void btnClick() {
        startActivity(new Intent(this,TestActivity.class));
    }
}