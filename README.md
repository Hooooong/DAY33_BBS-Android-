# Module 형식으로 게시판 만들기 (클라이언트)

### 설명
____________________________________________________

- Module 형식으로 게시판 만들기 ( read, create 만 작성 )

### KeyPoint
____________________________________________________

- `HttpURLConnection` OR `Retrofit2`

  - HttpURLConnection 사용하기

      - 참조 : [HttpURLConnection](https://github.com/Hooooong/DAY25_HTTPConnect)

  - Retrofit2 사용(RxJava)

      - 참조 : [Retrofit2](https://github.com/Hooooong/DAY41_SeoulWeather#keypoint), [RxJava](https://github.com/Hooooong/DAY40_RxJava2)

- RecyclerView 스크롤 시 데이터 호출하기

  ```java
  private int page = 1;
  // addOnScrollListener를 달아서 호출하기
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

          // 마지막 Position 이 totalCount-1 일때 load 한다
          if (lastPosition == totalCount-1) {
              load();
          }
      }
  });

  // load 시
  if (page == 1) {
      setAdapter(data.getData());
  } else {
      addAdapter(data.getData());
  }
  page++;
  ```

### Code Review
____________________________________________________

- MainActivity.java

  - RecyclerView 를 통해 데이터 목록을 보여준다.

  ```java
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
  ```

- WriteActivity.java

  - 글을 작성하는 Activity 이다. String 데이터들을 JSON 으로 변환하고 Body에 담아 보낸다.

  ```java
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
    }

    private void initView() {
        btnPost = (Button) findViewById(R.id.btnPost);
        editTitle = (EditText) findViewById(R.id.editTitle);
        editContent = (EditText) findViewById(R.id.editContent);
    }
  }
  ```

- ServiceGenerator.java

  - Retrofit2 Service 를 생성하는 class

  ```java
  public class ServiceGenerator {

      public static <T> T createBbs(Class<T> className){
          Retrofit retrofit = new Retrofit.Builder()
                  .baseUrl(Const.BBS_URL)
                  .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                  .addConverterFactory(GsonConverterFactory.create())
                  .build();

          return retrofit.create(className);
      }
  }
  ```

- iBbs.java

  - url 정의

  ```java
  public interface iBbs {

      @GET("bbs")
      Observable<Result> getData(@Query("type") String sort, @Query("page") int page);

      @POST("bbs")
      Observable<Result> sendPost(@Body Data data);

  }
  ```
