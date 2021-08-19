package com.pjh.naveropenapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.TaskExecutor;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;

import vo.BookVO;

public class BookVOAdapter extends ArrayAdapter<BookVO> {

    Context context;
    ArrayList<BookVO> list;
    BookVO vo;
    int resource;

    public BookVOAdapter(Context context, int resource, ArrayList<BookVO> list) {
        super(context, resource, list); //부모한테 list정보를 전달해주자!!
        
        this.list = list;
        this.context = context;
        this.resource = resource;
        
    }//생성자

    //ListView를 중간에서 연결해주는 Adapter일 경우
    //목록을 출력하기 위해 반드시 @Override해둬야 하는 메서드
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //myListView.setAdapter(adapter) 메서드 호출시 getView()메서드가 자동으로 실행
        //getView()메서드는 생성자에서 받아온 ArrayList의 사이즈만큼 자동으로 반복된다

        //LayoutInflater : xml문서를 view형태로 변환해주는 클래스
        LayoutInflater linf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = linf.inflate(resource, null); //xml파일을 직접 눈으로 확인가능한 형태로 구체화!

        vo = list.get(position);

        TextView title = convertView.findViewById(R.id.book_title);
        TextView author = convertView.findViewById(R.id.book_author);
        TextView price = convertView.findViewById(R.id.book_price);
        ImageView img = convertView.findViewById(R.id.book_img);

        title.setText(vo.getB_title());
        author.setText("저자 : " + vo.getB_author());
        price.setText("가격 : " + vo.getB_price());

        //이미지로드
        new ImgAsync(img).execute(vo.getB_img()); //strings로 들어감

        return convertView; //return이 될 때마다 항목을 한개씩 만든다!
    }//getView()

    class ImgAsync extends AsyncTask<String, Void, Bitmap>{

        ImageView img;
        public ImgAsync(ImageView img){ //생성자
            this.img = img;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            
            Bitmap bm = null;
            
            try{
                //가져올 이미지의 경로를 URL클래스에 전달
                URL img_url = new URL(strings[0]);

                BufferedInputStream bis = new BufferedInputStream(img_url.openStream());
                        
                //bis가 읽어온 정보로부터 Bitmap객체를 생성
                bm = BitmapFactory.decodeStream(bis);

                bis.close();
                
            }catch(Exception e){
                
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //ImageView에 서버로부터 가져온 bitmap을 세팅
            img.setImageBitmap(bitmap);
        }

    }//ImgAsync

}
