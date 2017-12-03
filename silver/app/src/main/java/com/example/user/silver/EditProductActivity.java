package com.example.user.silver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditProductActivity extends Activity {

    EditText txtName;
    EditText txtPrice;
    EditText txtDesc;
    EditText txtCreatedAt;
    Button btnSave;
    Button btnDelete;
    EditText edit;
    String pid;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single product url
    private static final String url_product_detials = "http://202.30.23.51/~sap16t7/get_product_details.php";

    // url to update product
    private static final String url_update_product = "http://202.30.23.51/~sap16t7/update_product.php";

    // url to delete product
    private static final String url_delete_product = "http://202.30.23.51/~sap16t7/delete_product.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "board";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "title";
    private static final String TAG_DESCRIPTION = "content";
    private static final String TAG_PRI = "pri";




    String name;
    String title;
    String content;
    String prikey;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product);



        // getting product details from intent
        Intent i = getIntent();

        // getting product id (pid) from intent
        pid = i.getStringExtra(TAG_PID);

        // Getting complete product details in background thread
        new GetProductDetails().execute();

        txtName=(EditText)findViewById(R.id.inputName2);
        txtPrice=(EditText)findViewById(R.id.inputPrice2);
        txtDesc=(EditText)findViewById(R.id.inputDesc2);

        // save button
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.Delete);

        /*
        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // starting background task to update product
             //   new SaveProductDetails().execute();
            }
        });
         */
      /*
        // Delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // deleting product in background thread
                Log.d("ㄴㄴㄴㄴㄴㄴㄴㄴㄴ", "삭제버튼들어옴");
                if(prikey== edit.toString())
                {
                    Log.d(edit.toString(), "삭제버튼들어옴");
                    new DeleteProduct().execute();
                }

            }
        });
     */
    }

    public void delete(View v){

        String tmp="ss";
        EditText tmp2=(EditText)findViewById(R.id.PRIKEY);
        tmp=tmp2.getText().toString();
        Log.d("ㄴㄴㄴㄴㄴㄴㄴㄴㄴ", "삭제버튼들어옴");
        Log.d(prikey, "삭제버튼들어옴");
        Log.d(prikey, "삭제버튼들어옴444");
        Log.d(tmp, "삭제버튼들어옴444");
        if(prikey.equals(tmp))
        {
            Log.d(tmp, "삭제버튼들어옴555555555");
            new DeleteProduct().execute();

        }


    }

    public void update(View v){
        // deleting product in background thread
        String tmp="ss";
        EditText tmp2=(EditText)findViewById(R.id.PRIKEY);
        tmp=tmp2.getText().toString();
        Log.d("ㄴㄴㄴㄴㄴㄴㄴㄴㄴ", "수정버튼들어옴");
        Log.d(prikey, "수정버튼들어옴");
        Log.d(prikey, "수정버튼들어옴444");
        Log.d(tmp, "수정버튼들어옴444");
        new SaveProductDetails().execute();

        /*
        if(prikey.equals(tmp))
        {
            Log.d(tmp, "수정버튼들어옴555555555");
            new SaveProductDetails().execute();

        }
        */

    }

    /**
     * Background Async Task to Get complete product details     * */
    class GetProductDetails extends AsyncTask<String, String, JSONObject> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Loading product details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         * */
        protected JSONObject doInBackground(String... params) {

            JSONObject product = null;
            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                params2.add(new BasicNameValuePair("pid", pid));

                // getting product details by making HTTP request
                // Note that product details url will use GET request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_product_detials, "GET", params2);

                // check your log for json response
                Log.d("Single Product Details", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully received product details
                    JSONArray productObj = json
                            .getJSONArray(TAG_PRODUCT); // JSON Array

                    // get first product object from JSON Array
                    product = productObj.getJSONObject(0);
                }else{
                    // product with pid not found
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return product;
        }



        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(JSONObject product) {
            pDialog.dismiss();
            if (product != null) {
                setContentView(R.layout.edit_product);

                // product with this pid found
                // Edit Text
                txtName = (EditText) findViewById(R.id.inputName2);
                txtPrice = (EditText) findViewById(R.id.inputPrice2);
                txtDesc = (EditText) findViewById(R.id.inputDesc2);

                // display product data in EditText
                try{
                    txtName.setText(product.getString(TAG_NAME));
                    name=product.getString(TAG_NAME);
                    txtPrice.setText(product.getString(TAG_PRICE));
                    title=product.getString(TAG_PRICE);
                    txtDesc.setText(product.getString(TAG_DESCRIPTION));
                    content=product.getString(TAG_DESCRIPTION);
                    prikey=product.getString(TAG_PRI);
                    Log.d("비밀번호",prikey);
                }catch (org.json.JSONException ex){
                    ex.printStackTrace();
                }


            }
            /*
            // dismiss the dialog once got all details
            pDialog.dismiss();*/
        }
    }







    /**
     * Background Async Task to  Save product Details
     * */
    class SaveProductDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Saving product ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Saving product
         * */




        protected String doInBackground(String... args) {

            // getting updated data from EditTexts

            Log.d("update", pid+name+title+content);
            Log.d("update", "수정 php2");
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_PID, pid));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_PRICE, title));
            params.add(new BasicNameValuePair(TAG_DESCRIPTION, content));
            Log.d("update", "수정 php3"+name+title+content);
            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_product,
                    "POST", params);
            Log.d("update", "수정 php4");
            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                Log.d("update", "수정 php5"+success);

                if (success == 1) {
                    Log.d("update", "수정 php6");
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about product update
                    setResult(100, i);
                    finish();

                } else {
                    // failed to update product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product uupdated
            pDialog.dismiss();
        }
    }







    /*****************************************************************
     * Background Async Task to Delete Product
     * */
    class DeleteProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Deleting Product...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting product
         * */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                Log.d("Delete Product", "지우기 php");
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("pid", pid));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_product, "POST", params);

                // check your log for json response
                Log.d("Delete Product", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about product deletion
                    setResult(100, i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();

        }

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////


}