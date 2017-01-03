package com.example.maxenia.knowweather.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.maxenia.knowweather.R;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    private EditText mET_phone, mET_description;
    private Button mBtn_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initView();
    }

    private void initView() {
        mET_phone = (EditText) findViewById(R.id.ET_phone);
        mET_description = (EditText) findViewById(R.id.ET_description);
        mBtn_feedback = (Button) findViewById(R.id.Btn_feedback);
        mBtn_feedback.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Btn_feedback:

                if (TextUtils.isEmpty(mET_phone.getText()) ||
                        TextUtils.isEmpty(mET_description.getText())) {
                    Toast.makeText(FeedbackActivity.this, R.string.input_not_null, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(FeedbackActivity.this, R.string.thankyou_feedback, Toast.LENGTH_SHORT).show();

                    //这里可以调用反馈的接口

                    finish();
                }
                break;
        }
    }
}
