package com.example.logintest2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth=null;
    private FirebaseDatabase mDatabase=FirebaseDatabase.getInstance();
    private GoogleSignInClient mGoogleSingInClient;
    private static final int RC_SIGN_IN=9001;
    private SignInButton btn_login;
    private Button register,emaillogin;
    private boolean isnewAccount=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login=(SignInButton)findViewById(R.id.btn_login);
        register=(Button)findViewById(R.id.btn_register);
        emaillogin=(Button)findViewById(R.id.btn_emaillogin);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });


        emaillogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,EmailLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            Intent intent=new Intent(MainActivity.this,FirstpageActivity.class);
            startActivity(intent);
            finish();
        }

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSingInClient= GoogleSignIn.getClient(this,gso);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn(){
        Intent signInIntent=mGoogleSingInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                // 구글 로그인이 성공했다면, 파이어베이스에 연동한다
                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String email=user.getEmail();
                            checkAccount(email);
                        }

                        else{
                            showToast("로그인 실패");
                            updateUI(null);
                        }

                    }
                });
    }

    public void updateUIwithName(FirebaseUser user){
        UserData userdata=new UserData(user.getDisplayName(),user.getEmail());
        Intent intent =new Intent(MainActivity.this,InitialActivity.class);
        intent.putExtra("userInformation",userdata);
        startActivity(intent);
        finish();
    }

    public void checkAccount(final String email){
        // Orderbychild, toequal 쓰려고 했는데 실행 안됨
        mDatabase.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            UserData userData=snapshot.getValue(UserData.class);
                            if(userData.getUseremail().equals(email)){
                                isnewAccount=false;
                            }
                        }
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if(isnewAccount){
                            showToast("신규가입을 환영합니다");
                            updateUIwithName(user);
                        }
                        else{
                            showToast("로그인 성공");
                            updateUI(user);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void showToast(String contents){
        Toast.makeText(MainActivity.this,contents,Toast.LENGTH_SHORT).show();
    }

    private void updateUI(FirebaseUser user){
        if(user!=null){
            Intent intent=new Intent(this,FirstpageActivity.class);
            startActivity(intent);
            finish();
        }
    }
}