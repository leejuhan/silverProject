package com.example.user.silver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.HashMap;

public class login extends AppCompatActivity implements View.OnClickListener{

    public static final String USER_NAME = "USER_NAME";

    public static final String PASSWORD = "PASSWORD";

    private static final String LOGIN_URL = "http://202.30.23.51/~sap16t7/login.php";

    private EditText editTextUserName;
    private EditText editTextPassword;

    private Button buttonLogin;
    private Button buttonRegister;
    private Button buttonExit;
    public int checklogin=0;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        layout=(RelativeLayout)findViewById(R.id.login);
        layout.setBackgroundResource(R.drawable.login);

        editTextUserName = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);

        buttonLogin = (Button) findViewById(R.id.button3);
        buttonRegister = (Button)findViewById(R.id.button2);
        buttonExit = (Button)findViewById(R.id.button7);

        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        buttonExit.setOnClickListener(this);
    }


    private void login(){
        String username = editTextUserName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        userLogin(username,password);
    }

    private void userLogin(final String username, final String password){
        class UserLoginClass extends AsyncTask<String,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(login.this,"Please Wait",null,true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                s=s.trim();

                if(s.equalsIgnoreCase("success")){
                    Intent intent = new Intent(login.this, mainmenu.class);
                    intent.putExtra(USER_NAME,username);
                    //finish();
                    startActivity(intent);
                    checklogin=1;
                }else{
                    Toast.makeText(login.this,s,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put("username",params[0]);
                data.put("password",params[1]);

                RegisterUserClass ruc = new RegisterUserClass();

                String result = ruc.sendPostRequest(LOGIN_URL,data);

                return result;
            }
        }
        UserLoginClass ulc = new UserLoginClass();
        ulc.execute(username, password);
    }



    @Override
    public void onClick(View v) {
        if(v == buttonLogin){
            login();
            if(checklogin==1){
                Intent intent = new Intent(login.this, mainmenu.class);
                //intent.putExtra(USER_NAME,username);
                startActivity(intent);
            }
             checklogin=0;
        }

        else if(v==buttonRegister){
            Intent intent = new Intent(login.this, joinus.class);
            startActivity(intent);
        }
        else if(v==buttonExit){
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
