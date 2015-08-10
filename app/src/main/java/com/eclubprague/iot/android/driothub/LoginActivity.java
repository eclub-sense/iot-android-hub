package com.eclubprague.iot.android.driothub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Dat on 10.8.2015.
 */
public class LoginActivity extends Activity {

    public final static String USERNAME = "User";
    public final static String PASSWORD = "123";

    EditText un,pw;
    Button ok;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        un=(EditText)findViewById(R.id.et_un);
        pw=(EditText)findViewById(R.id.et_pw);
        ok=(Button)findViewById(R.id.btn_login);

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = un.getText().toString();
                String password = pw.getText().toString();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(USERNAME, username);
                intent.putExtra(PASSWORD, password);
                startActivity(intent);
             }
        });
    }

}
