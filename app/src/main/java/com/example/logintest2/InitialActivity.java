package com.example.logintest2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InitialActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private Button btn_enter;
    private EditText edit_nickname;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        edit_nickname=(EditText)findViewById(R.id.edit_nickname);
        btn_enter=(Button)findViewById(R.id.btn_enter);
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });

        userData=(UserData)getIntent().getSerializableExtra("userInformation");
    }

    public void addUser(){
        final String nickname=edit_nickname.getText().toString();
        if(nickname.equals("")){
            showToast("닉네임을 입력하세요");
        }
        else {
            database.getReference().child("Users").child(nickname).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        showToast(nickname + "은 중복된 닉네임입니다");
                    } else {
                        userData.setUsername(nickname);
                        database.getReference().child("Users").child(nickname).setValue(userData);
                        // success 리스너 구현 필요함
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUI(user);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showToast("인터넷 연결 문제");
                }
            });
        }
    }

    public void showToast(String contents){
        Toast.makeText(InitialActivity.this,contents,Toast.LENGTH_SHORT).show();
    }

    public void updateUI(FirebaseUser user){
        if(user!=null){
            Intent intent = new Intent(InitialActivity.this,FirstpageActivity.class);
            startActivity(intent);
            finish();
        }
    }

}