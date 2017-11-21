package com.hooooong.bbs.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.hooooong.bbs.R;
import com.hooooong.bbs.model.Const;
import com.hooooong.bbs.model.Data;
import com.hooooong.bbs.model.Result;
import com.hooooong.bbs.util.ServiceGenerator;
import com.hooooong.bbs.util.iBbs;
import com.hooooong.bbs.view.main.adapter.MainAdapter;
import com.hooooong.bbs.view.wrtie.WriteActivity;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int page = 1;
    private String url = "http://192.168.0.156:8090/bbs?type=all&page=";

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private MainAdapter adapter;
    private Intent write;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        write = new Intent(this, WriteActivity.class);
        initView();
        load();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalCount = recyclerView.getLayoutManager().getItemCount();
                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                if (lastPosition == totalCount-1) {
                    load();
                }
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        fab.setOnClickListener(this);
    }


    private void load() {
        /*new AsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                return Remote.getData(params[0]);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                progressBar.setVisibility(View.GONE);
                Gson gson = new Gson();
                Result r = gson.fromJson(result, Result.class);
                List<Data> dataList = Arrays.asList(r.getData());

                // 성공했다면
                if (r.isSuccess()) {
                    // page 가 1이라면
                    if (page == 1) {
                        // 첫번째 데이터를 adapter 에 넣는다.
                        setAdapter(dataList);
                    } else {
                        // 추가되는 데이터를 adapter 에 추가한다.
                        addAdapter(dataList);
                    }
                    page++;
                }
            }
        }.execute(url+ page );*/

        // 1. 서비스 생성
        iBbs service = ServiceGenerator.createBbs(iBbs.class);
        // 2. Observer 생성 ( Emitter 생성 )
        Observable<Result> observable = service.getData("all", page);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> {
                            if(data.isSuccess()){
                                if (page == 1) {
                                    setAdapter(data.getData());
                                } else {
                                    addAdapter(data.getData());
                                }
                                page++;
                            }
                        }
                );
    }

    private void setAdapter(List<Data> dataList) {
        adapter = new MainAdapter(dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void addAdapter(List<Data> dataList) {
        adapter.addDataAndRefresh(dataList);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                startActivityForResult(write, Const.REQ_WRITE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Const.REQ_WRITE:
                if (resultCode == RESULT_OK) {
                    page = 1;
                    load();
                }
                break;
        }
    }
}
