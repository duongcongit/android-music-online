package com.duongcong.androidmusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class LoginTabFragment extends Fragment {
    Button btnLogin;
    EditText edtUserName,edtUserPassW;
    DBHelper DB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment,container,false);

        btnLogin =(Button) root.findViewById(R.id.btnLogin);
        edtUserName =(EditText) root.findViewById(R.id.login_username);
        edtUserPassW =(EditText) root.findViewById(R.id.login_password);
        DB = new DBHelper(getContext());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUserName.getText().toString();
                String password = edtUserPassW.getText().toString();
                if(username.equals("")||password.equals("")){
                    Toast.makeText(getActivity() , "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                else {
                    Boolean checkUserNamePassWord = DB.checkUserNamePassWord(username,password);
                    if(checkUserNamePassWord==true){
                        Toast.makeText(getActivity() , "Bạn đã đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity() , "Nhập sai thông tin", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        return root;
    }
}
