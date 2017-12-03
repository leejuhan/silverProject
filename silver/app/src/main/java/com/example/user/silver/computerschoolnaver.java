package com.example.user.silver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.content.Context;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import com.example.user.silver.GpsInfo;


/**
 * Created by user on 2016-11-13.
 */
public class computerschoolnaver extends AppCompatActivity implements MapView.MapViewEventListener{

    String myJSON;
    private static final String TAG_RESULTS="result";
    private static final String TAG_NAME = "c_name";
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> locList;
    ListView list;
    ArrayList<String> names;
    HashMap<String, String> mapx;
    HashMap<String, String> mapy;
    HashMap<String, String> address;
    HashMap<String, String> phone;
    int nowlisttouch=0;///////현재 리스트뷰에서 클릭된 리스트



    ////////////다음지도
    net.daum.mf.map.api.MapView  mapView=null;
    private String daumkey="d0b79feb7431f90fccb82f7251923db0";
    /////////////////////////////
    //////////////네이버검색api
    String tagName="";
    boolean isItemTag;
    ////////////////
   //////////gps
    private GpsInfo gps;
    Double mylat=0.0,mylon=0.0;
    int init=0;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.computerschoolnaver);
        //////////다음지도
        mapView = new net.daum.mf.map.api.MapView(computerschoolnaver.this);
        mapView.setDaumMapApiKey(daumkey);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.computerschoolnavermap);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        /////////////////////////////////////////////////////////
        /////////Listview
        list = (ListView)findViewById(R.id.comlistView);
        locList = new ArrayList<HashMap<String, String>>();
        names=new ArrayList<String>();
        getData();
        mapx=new HashMap<String, String>();
        mapy=new HashMap<String, String>();
        address=new HashMap<String, String>();
        phone=new HashMap<String, String>();


        list.setOnItemClickListener(onClickListItem);
        list.setItemChecked(0, true);
        ////////////////////////////////나의 좌표가지고 오기
        gps = new GpsInfo(computerschoolnaver.this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {
            mylat = gps.getLatitude();
            mylon = gps.getLongitude();

        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }



         /////////////////////////
        //상세정보 버튼
        Button cptdetailbutton =(Button) findViewById(R.id.cpdetailbutton);
        cptdetailbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address.get(names.get(nowlisttouch)) == null) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(computerschoolnaver.this);

                    // dialog.setTitle("상세주소" + mapx.size() + "R" + mapy.size() + "R" + mapx.get(0) + "R" + mapy.get(0));
                    dialog.setTitle("상세주소");

                    dialog.setMessage("주소:" + "서울특별시 강남구 역삼동 718-5" + "\n" + "전화번호:" + "02-3429-5114");

                    // Cancel 버튼 이벤트
                    dialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(computerschoolnaver.this);

                    // dialog.setTitle("상세주소" + mapx.size() + "R" + mapy.size() + "R" + mapx.get(0) + "R" + mapy.get(0));
                    dialog.setTitle("상세주소");

                    dialog.setMessage("주소:" + address.get(names.get(nowlisttouch)) + "\n" + "전화번호:" + phone.get(names.get(nowlisttouch)));

                    // Cancel 버튼 이벤트
                    dialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();

                }

            }
        });


        /////////////////////////////구글 네비게이션
        //노래교실버튼
        Button musicshool =(Button) findViewById(R.id.cpnavigationbutton);
        musicshool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("google.navigation:q="+names.get(nowlisttouch)+"&mode=r");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });


        ////////////////////////다음 길찾기
        //노래교실버튼37.2507455, 127.0306161
        Button findload =(Button) findViewById(R.id.cpfindload);
        findload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    if (address.get(names.get(nowlisttouch)) == null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://route?sp="+mylat+","+mylon+"&ep=37.2507455, 127.0306161&by=CAR"));
                        startActivity(intent);


                    } else {
                        int i=Integer.parseInt(mapx.get(names.get(nowlisttouch)).trim());
                        int i2=Integer.parseInt(mapy.get(names.get(nowlisttouch)).trim());
                        GeoPoint oKA = new GeoPoint(i, i2);
                        GeoPoint oGeo = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, oKA);
                        Double lat2 =Double.parseDouble(String.format("%.8f", oGeo.getY()));
                        Double lng2 =Double.parseDouble(String.format("%.8f", oGeo.getX()));

                    /*Toast toast = Toast.makeText(getApplicationContext(),
                            "토스트창에 출력될 문자"+mylat+mylon+lat2+lng2, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                     */


                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://route?sp="+mylat+","+mylon+"&ep="+lat2+","+lng2+"&by=CAR"));
                        startActivity(intent);

                    }
                }catch (Exception e){

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"net.daum.android.map"));
                    startActivity(intent);

                }
                /*
                if(init==0){
                    try{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://route?sp="+mylat+","+mylon+"&ep=37.2507455, 127.0306161&by=CAR"));

                        startActivity(intent);

                    }catch (Exception e){

                    }
                    init=3;

                }else{
                    try{
                        int i=Integer.parseInt(mapx.get(names.get(nowlisttouch)).trim());
                        int i2=Integer.parseInt(mapy.get(names.get(nowlisttouch)).trim());
                        GeoPoint oKA = new GeoPoint(i, i2);
                        GeoPoint oGeo = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, oKA);
                        Double lat2 =Double.parseDouble(String.format("%.8f", oGeo.getY()));
                        Double lng2 =Double.parseDouble(String.format("%.8f", oGeo.getX()));


                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://route?sp="+mylat+","+mylon+"&ep="+mapx.get(names.get(nowlisttouch)).trim()+","+mapy.get(names.get(nowlisttouch)).trim()+"&by=CAR"));
                        startActivity(intent);

                    }catch (Exception e){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"net.daum.android.map"));
                        startActivity(intent);
                    }

                }*/




                /*   int i=Integer.parseInt(mapx.get(names.get(nowlisttouch)).trim());
                        int i2=Integer.parseInt(mapy.get(names.get(nowlisttouch)).trim());
                        GeoPoint oKA = new GeoPoint(i, i2);
                        GeoPoint oGeo = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, oKA);
                        Double lat2 =Double.parseDouble(String.format("%.8f", oGeo.getY()));
                        Double lng2 =Double.parseDouble(String.format("%.8f", oGeo.getX()));

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://route?sp="+mylat+","+mylon+"&ep="+lat2+","+lng2+"&by=CAR"));
                        startActivity(intent);*/


            }
        });



        ///지도보기 버튼
        Button musicshool2 =(Button) findViewById(R.id.cpshowmap);
        musicshool2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    int i=Integer.parseInt(mapx.get(names.get(nowlisttouch)).trim());
                    int i2=Integer.parseInt(mapy.get(names.get(nowlisttouch)).trim());
                    GeoPoint oKA = new GeoPoint(i, i2);
                    GeoPoint oGeo = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, oKA);
                    Double lat =Double.parseDouble(String.format("%.8f", oGeo.getY()));
                    Double lng =Double.parseDouble(String.format("%.8f", oGeo.getX()));


                    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lat,lng);
                    mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true);
                    //마커 생성
                    MapPOIItem marker = new MapPOIItem();
                    marker.setItemName(names.get(nowlisttouch));////말풍성이름

                    marker.setTag(0);
                    marker.setMapPoint(mapPoint);
                    marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);  //기본으로 제공하는 BluePin 마커 모양
                    marker.setCustomImageResourceId(R.drawable.ic_map_arrive); // 마커 이미지.
                    marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
                    marker.setCustomImageAnchor(0.5f, 0.5f);
                    //마커 추가
                    mapView.addPOIItem(marker);
                    mapView.selectPOIItem(marker, true);
                }catch (NullPointerException ex){

                }




            }
        });




    }

    // 아이템 터치 이벤트
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            nowlisttouch=arg2;
            naversearchinfo(names.get(nowlisttouch));


        }
    };














    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                String name = c.getString(TAG_NAME);

                HashMap<String,String> locs = new HashMap<String,String>();

                locs.put(TAG_NAME, name);
                names.add(name);
                locList.add(locs);

            }

            ListAdapter adapter = new SimpleAdapter(
                    computerschoolnaver.this, locList, R.layout.computerschoolnaver_list_item,
                    new String[]{TAG_NAME},
                    new int[]{R.id.c_name}
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getData(){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost("http://202.30.23.51/~sap16t7/computerroom.php");

                // Depends on your web service
                httppost.setHeader("Content-type", "application/json");

                InputStream inputStream = null;
                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }


    @Override
    public void onMapViewInitialized(MapView mapView) {



        //지도 이동
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(37.2507455, 127.0306161);
        mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true);
        //마커 생성
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("삼성컴퓨터교실");////말풍성이름
        marker.setTag(1);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);  //기본으로 제공하는 BluePin 마커 모양
        marker.setCustomImageResourceId(R.drawable.ic_map_arrive); // 마커 이미지.
        marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        marker.setCustomImageAnchor(0.5f, 0.5f);
        //마커 추가
        mapView.addPOIItem(marker);
        mapView.selectPOIItem(marker, true);

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    /////네이버 검색api
    public void naversearchinfo(String query){

        String convertQuery=null;
        if(query==null){
            return;
        }

        try {

            convertQuery = URLEncoder.encode(query, "utf-8");

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        final String defaultUrl = "https://openapi.naver.com/v1/search/local.xml?display=1&query=";

        AsyncHttpClient client = new AsyncHttpClient();

        client.addHeader("Content-Type", "application/xml");
        client.addHeader("X-Naver-Client-Id", "DJX1vK4MddqU3o_H8o_F");
        client.addHeader("X-Naver-Client-Secret", "RBL4fktXtG");
        client.get(defaultUrl + convertQuery, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] arg2) {
                String text = new String(arg2);
                // textView.append(text);
                try {
                    //XmlPullParser를 사용하기 위해서
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                    //네임스페이스 사용여부
                    factory.setNamespaceAware(true);
                    //xml문서를 이벤트를 이용해서 데이터를 추출해주는 객체
                    XmlPullParser xpp = factory.newPullParser();

                    //XmlPullParser xml데이터를 저장
                    //xpp.setInput(in, "euc-kr");
                    xpp.setInput(new StringReader(text));
                    //이벤트 저장할 변수선언
                    int eventType = xpp.getEventType();

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) { //시작 태그를 만났을때.
                            //태그명을 저장
                            tagName = xpp.getName();
                            if (tagName.equals("item")) isItemTag = true;

                        } else if (eventType == XmlPullParser.TEXT) { //내용
                            // tagName에 저장된 태그명 title태그일때 제목을 저장
                            if (isItemTag && tagName.equals("mapx")) {
                                mapx.put(names.get(nowlisttouch),xpp.getText());


                            }
                            //기사의 내용을 저장
                            if (isItemTag && tagName.equals("mapy")) {
                          mapy.put(names.get(nowlisttouch), xpp.getText());

                            }
                            if (isItemTag && tagName.equals("address")) {
                               // address.add(nowlisttouch,xpp.getText());
                             address.put(names.get(nowlisttouch),xpp.getText());

                            }
                            if (isItemTag && tagName.equals("telephone")) {
                                //phone.add(nowlisttouch,xpp.getText());
                                phone.put(names.get(nowlisttouch), xpp.getText());

                            }



                        } else if (eventType == XmlPullParser.END_TAG) { //닫는 태그를 만났을때
                            //태그명을 저장
                            tagName = xpp.getName();

                            if (tagName.equals("item")) {

                                isItemTag = false; //초기화

                            }


                        }

                        eventType = xpp.next(); //다음 이벤트 타입
                    }

                } catch (Exception e) {

                    Log.e("NewsApp", "예외발생 :" + e.getMessage());
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }
}
