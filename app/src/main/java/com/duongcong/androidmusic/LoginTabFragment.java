package com.duongcong.androidmusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginTabFragment extends Fragment {
    private Button btnLogin;
    private EditText edtUserName,edtUserPassW;
    private String username,password;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment,container,false);

        btnLogin =(Button) root.findViewById(R.id.btnLogin);
        edtUserName =(EditText) root.findViewById(R.id.login_username);
        edtUserPassW =(EditText) root.findViewById(R.id.login_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validation();
            }
        });

        return root;
    }

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

    private void checkFromDB() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String db_password = snapshot.child("password").getValue().toString();
                    if(password.equals(db_password)){
                        Toast.makeText(getActivity().getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), "Nhập sai mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
