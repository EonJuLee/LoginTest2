package com.example.logintest2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class FirstpageActivity extends AppCompatActivity {

    private  SharedPreferences auto;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstpage);

        mAuth=FirebaseAuth.getInstance();
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.btn_logout:
                signOut();
                break;
        }
        updateUI();
    }

    private void signOut(){
        auto =getSharedPreferences("autologin", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=auto.edit();
        editor.clear();
        editor.commit();
        mAuth.signOut();
    }

    private void updateUI(){
        Intent intent=new Intent(FirstpageActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
