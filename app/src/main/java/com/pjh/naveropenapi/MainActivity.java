package com.pjh.naveropenapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import vo.BookVO;

public class MainActivity extends AppCompatActivity {

    static EditText search; //static으로 설정해두면 어떤 클래스에서든지 사용가능
    ListView myListView;
    Button search_btn;
    Parser parser;
    ArrayList<BookVO> list;
    BookVOAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = findViewById(R.id.search);
        myListView = findViewById(R.id.myListView);
        search_btn = findViewById(R.id.search_btn);

        parser = new Parser();

        //parser를 통해 네이버와 통신
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                list = new ArrayList<>();

                //Async클래스의 doInBackground()를 호출
                new NaverAsync().execute("박", "정", "현");

            }
        });

    }//onCreate()

    //서버통신 전용 클래스
    //AsyncTask클래스는 제너릭타입이 3개
    //1) doInBackground()의 파라미터 타입
    //2) onProgressUpdate()가 오버라이딩 되어있다면 여기에서 사용할 자료형 타입 -> 사실상 쓸 일이 거의 없음
    //3) 완료된 작업결과를 반영하는 onPostExecute()의 파라미터 타입이자 doInBackground의 반환형 -> 매우 중요!!
    class NaverAsync extends AsyncTask<String, Void, ArrayList<BookVO>>{

        @Override
        protected ArrayList<BookVO> doInBackground(String... strings) {
            //strings[0] = "박"
            //strings[1] = "정"
            //strings[2] = "현"
            
            //각종 반복, 백그라운드 통신 등의 주된 처리작업
            return parser.connectNaver(list);
        }

        @Override
        protected void onPostExecute(ArrayList<BookVO> bookVOS){
            //doInBackground()를 통해 통신을 마친 결과물을 돌려받는 메서드

            //서버와 통신이 끝났다면, bookVOS의 사이즈만큼 ListView에 항목을 만들어줘야 하므로
            //이 시점에서 Adapter클래스를 생성해준다

            //new BookVOAdapter(화면제어권자, ListView의 항목을 구성하는 item리소스파일, 검색이 완료된 ArrayList)
            adapter = new BookVOAdapter( MainActivity.this, R.layout.book_item, bookVOS );

            //ListView에 Adapter를 세팅
            myListView.setAdapter(adapter); //이 코드를 호출하는 순간 getView()가 자동으로 실행됨
            
            //ListView의 클릭이벤트 처리(상세보기)
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //ListView의 항목(item)클릭이벤트 감지자
                    String bookUrl = bookVOS.get(i).getB_img();
                    
                    //이미지경로에서 bid만 추출
                    String bookId = bookUrl.substring(bookUrl.lastIndexOf('/')+1, bookUrl.indexOf(".jpg"));

                    String bookLink = "https://book.naver.com/bookdb/book_detail.nhn?bid="+bookId;

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(bookLink));
                    startActivity(intent);
                }
            });
        }

    }//NaverAsync

}