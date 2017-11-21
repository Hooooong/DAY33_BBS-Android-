package com.hooooong.bbs.view.wrtie;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hooooong.bbs.R;
import com.hooooong.bbs.model.Data;
import com.hooooong.bbs.model.Result;
import com.hooooong.bbs.util.ServiceGenerator;
import com.hooooong.bbs.util.iBbs;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WriteActivity extends AppCompatActivity {
    private Button btnPost;
    private EditText editTitle;
    private EditText editContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        initView();
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write();
            }
        });
    }

    private void write() {
        String title = editTitle.getText().toString();
        String content = editContent.getText().toString();

        Data data = new Data();
        data.setTitle(title);
        data.setContent(content);
        String dID = android.provider.Settings.Secure.getString(
                getContentResolver(), Settings.Secure.ANDROID_ID);
        data.setUser_id(dID);
        // 날짜는 서버에서 세팅

        iBbs service = ServiceGenerator.createBbs(iBbs.class);

        Observable<Result> observable = service.sendPost(data);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        setResult(RESULT_OK);
                    } else {
                        Toast.makeText(WriteActivity.this, "오류", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                });
        /*

        new AsyncTask<Data, Void, Result>() {

            @Override
            protected Result doInBackground(Data... params) {
                Gson gson = new Gson();
                String json = gson.toJson(params[0]);
                String result_string = Remote.sendPost(json, "http://192.168.0.156:8090/bbs");
                Result result = gson.fromJson(result_string, Result.class);
                return result;
            }

            @Override
            protected void onPostExecute(Result result) {
                if (result == null || !result.isSuccess()) {
                    Toast.makeText(WriteActivity.this, "오류", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                } else {
                    setResult(RESULT_OK);
                }
                finish();
            }
        }.execute(data);*/
    }

    private void initView() {
        btnPost = (Button) findViewById(R.id.btnPost);
        editTitle = (EditText) findViewById(R.id.editTitle);
        editContent = (EditText) findViewById(R.id.editContent);
    }
}
