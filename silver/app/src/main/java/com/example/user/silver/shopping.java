package com.example.user.silver;

/**
 * Created by user on 2016-12-11.
 */
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hhdd.messi.Daum;
import com.hhdd.messi.event.DaumEventListener;
import com.hhdd.messi.daum.object.shopping.SearchObject;

public class shopping extends Activity implements DaumEventListener.OnShoppingSearchListener {

    ListView list;
    ArrayList<String> ids2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping);

        final Daum open_api = new Daum();
        open_api.setShoppingKey("7cfac449e4fdbdfb07ecac051b63e56b");
        open_api.setShoppingSearchListener(this);

        list = (ListView)findViewById(R.id.lv1);


        Button btn = (Button) findViewById(R.id.bt1);
        btn.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                EditText edt = (EditText) findViewById(R.id.et1);
                open_api.ShoppingSearch(edt.getText().toString());
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               /*
                Toast toast = Toast.makeText(getApplicationContext(),ids2.get(position), Toast.LENGTH_LONG);
                toast.show();*/
                String url ="http://shopping.daum.net/product/"+ids2.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);

            }
        });

    }

    public void onFaultResult(int arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    public void onResult(ArrayList<SearchObject> arg0) {
        // TODO Auto-generated method stub
        ListView listView = (ListView) findViewById(R.id.lv1);
        ListAdapter b1_adapter = new ListAdapter(this, R.layout.shitem_row, arg0);
        listView.setAdapter(b1_adapter);
        ids2=b1_adapter.ids;

    }
/*
    // 아이템 터치 이벤트
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            nowlisttouch=arg2;
            naversearchinfo(names.get(nowlisttouch));
            SearchObject Info2 = items2.get(arg2);
            Toast toast = Toast.makeText(getApplicationContext(),Info2.getDocid(), Toast.LENGTH_LONG);

            toast.show();



        }
    };
*/

    public class ListAdapter extends ArrayAdapter<SearchObject>{
        public ArrayList<SearchObject> items;
        private Context mcontext;
        private int count = 0;
        ArrayList<String> ids;
        public ListAdapter(Context context, int textViewResourceId, ArrayList<SearchObject> items) {
            super(context, textViewResourceId, items);
            mcontext = context;
            this.items = items;
            ids=new ArrayList<String>();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.shitem_row, null);
            }

            SearchObject Info = items.get(position);

            if (Info != null) {
                ImageView iv1 = (ImageView) v.findViewById(R.id.iv1);
                TextView tv1 = (TextView) v.findViewById(R.id.tv1);
                TextView tv2 = (TextView) v.findViewById(R.id.tv2);
                Info.BindImage(iv1);
                tv1.setText(Info.getTitle());
                ids.add(Info.getDocid());
                tv2.setText("최저가 : "+Info.getPrice_min()+"원\n최고가 : "+Info.getPrice_max()+"원");
            }
            return v;
        }


    }
}