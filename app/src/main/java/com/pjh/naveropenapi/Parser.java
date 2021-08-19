package com.pjh.naveropenapi;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vo.BookVO;

public class Parser {
    //웹에서 요소(제목, 저자, 가격...)를 검색하여 준비해둔 vo에 저장하자
    //이같은 작업을 'xml파싱' 이라고 한다

    BookVO vo;
    String query = ""; //EditText에서 검색된 단어를 담는 변수

    //서버통신 메서드
    public ArrayList<BookVO> connectNaver( ArrayList<BookVO> list ){

        try {
            query = URLEncoder.encode(MainActivity.search.getText().toString(), "UTF8");
            String urlStr = "https://openapi.naver.com/v1/search/book.xml?query="+query+"&display=100"; //display=100 ---> 검색결과 100개 돌려줌
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            //서버연결객체를 통해 발급받은 id와 secret을 검증
            connection.setRequestProperty("X-Naver-Client-Id", "vJ2fMjmo4Hv3B2KildC_");
            connection.setRequestProperty("X-Naver-Client-Secret", "lPY9bKSZMP");

            //파싱을 위한 객체들
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(connection.getInputStream(), null);

            //본격적인 파싱작업
            int parserEvent = parser.getEventType();

            while( parserEvent != XmlPullParser.END_DOCUMENT ){
                //서버측 xml문서의 끝을 만날때까지 반복하시오

                if( parserEvent == XmlPullParser.START_TAG ){
                    String tagName = parser.getName(); //현재태그의 이름을 가져옴

                    //순차적으로 정보를 검색(title -> image -> author -> price)
                    if( tagName.equalsIgnoreCase("title") ){
                        vo = new BookVO();
                        String title = parser.nextText(); //nextText() ---> 태그안으로 들어옴
                        
                        //검색어를 강조하기 위해 타이틀에 추가되어 있는 <b>, </b>태그를 제거
                        Pattern pattern = Pattern.compile("<.*?>"); //정규식
                        Matcher matcher = pattern.matcher(title);

                        if( matcher.find() ){
                            String s_title = matcher.replaceAll("");
                            vo.setB_title(s_title); //vo에 값 담기
                        }else{
                            vo.setB_title(title); //vo에 값 담기
                        }

                    }else if( tagName.equalsIgnoreCase("image") ){
                        String img = parser.nextText();
                        vo.setB_img(img);

                    }else if( tagName.equalsIgnoreCase("author") ){
                        String author = parser.nextText();
                        vo.setB_author(author);

                    }else if( tagName.equalsIgnoreCase("price") ){
                        String price = parser.nextText();
                        vo.setB_price(price);
                        list.add(vo); //list에 책정보 담기
                    }
                }
                parserEvent = parser.next(); //다음요소

            }//while

        }catch (Exception e){

        }//try-catch

        return list;

    }//connectNaver()

}
