package com.example.user.silver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.RenderScript;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import com.example.user.silver.GpsInfo;
import com.hhdd.messi.Daum;
import com.hhdd.messi.event.DaumEventListener;
import com.hhdd.messi.daum.object.shopping.SearchObject;
/**
 * Created by user on 2016-12-11.
 */
public class Silvermarket extends Activity {

    Handler handler = new Handler();
    String url3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.silvermarket);
       url3=new String();


       //�μ����Ϸ�����
        Button button = (Button) findViewById(R.id.adbuy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast toast = Toast.makeText(getApplicationContext(),url3, Toast.LENGTH_LONG);
               // toast.show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url3.trim()));
                startActivity(intent);
            }
        });

        //��
        Button button2 = (Button) findViewById(R.id.buy);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Silvermarket.this, shopping.class);
                startActivity(intent);
            }
        });
       /////////////////////
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuffer sb = new StringBuffer();

                try {
                    URL url = new URL("http://202.30.23.51/~sap16t7/adsite.html");
                    HttpURLConnection conn =
                            (HttpURLConnection)url.openConnection();// 접속
                    if (conn != null) {
                        conn.setConnectTimeout(2000);
                        conn.setUseCaches(false);
                        if (conn.getResponseCode()
                                ==HttpURLConnection.HTTP_OK){
                            //    데이터 읽기
                            BufferedReader br
                                    = new BufferedReader(new InputStreamReader
                                    (conn.getInputStream(),"euc-kr"));//"utf-8"
                            while(true) {
                                String line = br.readLine();
                                if (line == null) break;
                                sb.append(line+"\n");
                            }
                            br.close(); // 스트림 해제
                        }
                        conn.disconnect(); // 연결 끊기
                    }
                    // 값을 출력하기
                    Log.d("test", sb.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String url=new String();
                            String realurl;
                            String realurl2;
                            String[] temp;
                            String[] temp2;
                            url=sb.toString();
                            temp=url.split(">");
                            realurl=temp[3];
                            temp2=realurl.split("<");
                            realurl2=temp2[0];
                            url3=realurl2;
                            //          Toast toast = Toast.makeText(getApplicationContext(),realurl2, Toast.LENGTH_LONG);
                            //        toast.show();

                            //         Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(realurl2));
                            //         startActivity(intent);


                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start(); // 쓰레드 시작




        //////////////제휴 이미지
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {    // 오래 거릴 작업을 구현한다
                // TODO Auto-generated method stub
                try{
                    // 걍 외우는게 좋다 -_-;
                    final ImageView iv = (ImageView)findViewById(R.id.ad);
                    URL url = new URL("http://202.30.23.51/~sap16t7/ad.PNG");
                    InputStream is = url.openStream();
                    final Bitmap bm = BitmapFactory.decodeStream(is);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {  // 화면에 그려줄 작업
                            iv.setImageBitmap(bm);
                        }
                    });
                    iv.setImageBitmap(bm); //비트맵 객체로 보여주기
                } catch(Exception e){

                }

            }
        });

        t.start();

    }






}
