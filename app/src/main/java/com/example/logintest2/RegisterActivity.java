package com.example.logintest2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private FirebaseDatabase mDatabase=FirebaseDatabase.getInstance();
    private String confirmed="";
    private Button btn_register_request,btn_check_request;
    private EditText editEmail,editPW,editNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editEmail=(EditText)findViewById(R.id.registerEmail);
        editPW=(EditText)findViewById(R.id.registerPW);
        editNickname=(EditText)findViewById(R.id.registerNickname);
        btn_check_request=(Button)findViewById(R.id.btn_check_request);
        btn_register_request=(Button)findViewById(R.id.btn_register_request);

        btn_register_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=editEmail.getText().toString().trim();
                String pw=editPW.getText().toString().trim();
                String nickname=editNickname.getText().toString().trim();
                if(email.equals("")){
                    showToast("이메일을 입력하세요");
                }
                else if(pw.equals("")){
                    showToast("패스워드를 입력하세요");
                }
                else if(!nickname.equals(confirmed)){
                    showToast("닉네임 중복 검사가 필요합니다");
                }
                else{
                    createUser(email,pw,nickname);
                }
            }
        });

        btn_check_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname=editNickname.getText().toString().trim();
                if(nickname.equals("")){
                    showToast("닉네임을 입력하세요");
                }
                else {
                    chkUser(nickname);
                }
            }
        });
    }

    public void chkUser(final String nickname){
        mDatabase.getReference().child("Users").child(nickname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    showToast(nickname+"은 중복된 닉네임입니다");
                }
                else{
                    confirmed=nickname;
                    showToast("사용 가능한 닉네임입니다");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast("인터넷 연결을 확인하세요");
            }
        });
    }

    public void createUser(final String email, String pw, final String nickname){
        firebaseAuth.createUserWithEmailAndPassword(email,pw)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            UserData userData=new UserData(nickname,email);
                            mDatabase.getReference().child("Users").child(nickname).setValue(userData);
                            // success listener 구현해야함
                            FirebaseUser user=firebaseAuth.getCurrentUser();
                            sendEmail(user);
                            updateUI(user);
                        }
                        else{
                            showToast("인터넷 연결 상태/이미 있는 이메일/파이어 베이스 서버 문제");
                            updateUI(null);
                        }
                    }
                });
    }


    public void sendEmail(FirebaseUser user){
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    showToast("이메일을 보냈습니다");
                }
                else{
                    showToast("이메일을 보낼 수 없습니다");
                }
            }
        });
    }

    public void showToast(String contents){
        Toast.makeText(RegisterActivity.this,contents,Toast.LENGTH_SHORT).show();
    }

    public void updateUI(FirebaseUser user){
        firebaseAuth.signOut();
        if(user!=null) {
            boolean emailVerified = user.isEmailVerified();
            if (!emailVerified) {
                showToast("이메일 인증 후 사용할 수 있습니다");
            }
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
