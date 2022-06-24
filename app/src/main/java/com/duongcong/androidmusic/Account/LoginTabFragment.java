package com.duongcong.androidmusic.Account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.duongcong.androidmusic.MainActivity;
import com.duongcong.androidmusic.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginTabFragment extends Fragment {
    private Button btnLogin;
    private EditText edtUserName,edtUserPassW;
    private String username,password;
    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment,container,false);

        btnLogin =(Button) root.findViewById(R.id.btnLogin);
        edtUserName =(EditText) root.findViewById(R.id.login_username);
        edtUserPassW =(EditText) root.findViewById(R.id.login_password);
        firebaseAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validation();
            }
        });

        return root;
    }

    //Validate đăng nhập
    private void Validation() {
        username = edtUserName.getText().toString();
        password = edtUserPassW.getText().toString();
        if(username.isEmpty()){
            edtUserName.setError("Vui lòng điền thông tin");
            edtUserName.requestFocus();
        }
        else if(password.isEmpty()){
            edtUserPassW.setError("Vui lòng điền thông tin");
            edtUserPassW.requestFocus();
        }
        else{
            checkFromDB();
        }

    }

    //Kiểm tra dữ liệu từ DB
    private void checkFromDB() {
        //Firebase Auth
        firebaseAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity().getApplicationContext(), "Xin chào"+username, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    intent.putExtra("email",username);
                    intent.putExtra("password",password);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Đăng nhập thất bại,vui lòng kiểm tra thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {

            }
        });

    }
}
