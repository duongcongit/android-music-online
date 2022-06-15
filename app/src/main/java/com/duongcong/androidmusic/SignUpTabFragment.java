package com.duongcong.androidmusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class SignUpTabFragment extends Fragment {
    Button btnSignUp;
    EditText phoneNum,password,rePassWord;
    DBHelper DB;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.sign_up_tab_fragment,container,false);

        btnSignUp =(Button) root.findViewById(R.id.btnSignUp);
        phoneNum =(EditText) root.findViewById(R.id.phone_number);
        password =(EditText) root.findViewById(R.id.password);
        rePassWord =(EditText) root.findViewById(R.id.conf_password);
        DB = new DBHelper(getContext());

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phoneNum.getText().toString();
                String pass = password.getText().toString();
                String rePass = rePassWord.getText().toString();

                if(phone.equals("")||pass.equals("")||rePass.equals("")){
                    Toast.makeText(getActivity() , "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(pass.equals(rePass)){
                        Boolean checkUser = DB.checkUserName(phone);
                        if(checkUser==false){
                            Boolean insert = DB.insertData(phone,pass);
                            if(insert==true){
                                Toast.makeText(getActivity() , "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getActivity() , "Đăng kí thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getActivity() , "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getActivity() , "Nhập sai xác nhận mật khẩu,vui lòng nhập lại", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        return root;
    }
}
