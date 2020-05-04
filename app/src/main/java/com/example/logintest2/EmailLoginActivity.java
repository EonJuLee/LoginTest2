package com.example.logintest2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailLoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button loginbtn;
    private EditText loginemail,loginpw;
    private SharedPreferences auto;
    private String loginID,loginPW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        loginemail = (EditText) findViewById(R.id.loginemail);
        loginpw = (EditText) findViewById(R.id.loginpw);
        loginbtn = (Button) findViewById(R.id.loginbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        auto = getSharedPreferences("autologin", Activity.MODE_PRIVATE);

        loginID = auto.getString("inputId", null);
        loginPW = auto.getString("inputPW", null);

        if (loginID != null && loginPW != null) {
            login(loginID, loginPW);
        }

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginemail.getText().toString().trim();
                String pw = loginpw.getText().toString().trim();
                if (email.equals("")) {
                    showToast("이메일을 입력하세요");
                } else if (pw.equals("")) {
                    showToast("패스워드를 입력하세요");
                } else {
                    login(email, pw);
                }
            }
        });
    }

    public void login(final String email, final String pw) {
        firebaseAuth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(EmailLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences.Editor autoLogin = auto.edit();
                            autoLogin.putString("inputId", email);
                            autoLogin.putString("inputPW", pw);
                            autoLogin.commit();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.e("Error6", loginID + " " + loginPW);
                            updateUI(user);
                        } else {
                            showToast("로그인 오류입니다");
                            updateUI(null);
                        }
                    }
                });
    }

    public void showToast(String contents){
        Toast.makeText(EmailLoginActivity.this,contents,Toast.LENGTH_SHORT).show();
    }

    public void updateUI(FirebaseUser user){
        if(user!=null){
            boolean emailVerified=user.isEmailVerified();
            if(emailVerified){
                Intent intent=new Intent(EmailLoginActivity.this,FirstpageActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                firebaseAuth.signOut();
                showToast("이메일 인증 후 사용할 수 있습니다");
            }
        }
    }
}