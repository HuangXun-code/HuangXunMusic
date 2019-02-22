package edu.whut.huangxun.HuangXunMusic;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText username = (EditText) findViewById(R.id.edt_username);
        final EditText password = (EditText) findViewById(R.id.edt_password);
        final CheckBox saveNameChk = (CheckBox) findViewById(R.id.chk_savename);
        Button quit = (Button) findViewById(R.id.btn_quit);
        Button login = (Button) findViewById(R.id.btn_login);

        SharedPreferences pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        username.setText(pref.getString("uname", ""));


        //password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        findViewById(R.id.btn_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //监听器要做的事情
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);

            }
        });

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //监听器要做的事情
                if (username.getText().toString()==null||password.getText().toString().equals("") || !password.getText().toString().equals("123456"))
                {
                    Toast.makeText(LoginActivity.this, "密码错误，请重试", Toast.LENGTH_SHORT).show();
                    password.setText("");
                }
                else
                {

                    SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
                    if(saveNameChk.isChecked()){
                        editor.putString("uname", username.getText().toString());
                    }else{
                        editor.putString("uname", "");
                    }
                    editor.apply();

                    Intent i=new Intent(LoginActivity.this,ListActivity.class);
                    i.putExtra("uname",username.getText().toString());
                    startActivity(i);
                    finish();
                }
            }
        });




    }
}

