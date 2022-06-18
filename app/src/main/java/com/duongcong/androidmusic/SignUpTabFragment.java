package com.duongcong.androidmusic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.utilities.Validation;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpTabFragment extends Fragment {
    Button btnSignUp;
    EditText phoneNum,password,rePassWord,name;
    String phone,uName,pass,rePass;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.sign_up_tab_fragment,container,false);

        btnSignUp =(Button) root.findViewById(R.id.btnSignUp);
        phoneNum =(EditText) root.findViewById(R.id.phone_number);
        name =(EditText) root.findViewById(R.id.name);
        password =(EditText) root.findViewById(R.id.password);
        rePassWord =(EditText) root.findViewById(R.id.conf_password);


        //progressdialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Xin vui lòng đợi...");
        progressDialog.setCanceledOnTouchOutside(false);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validation();
            }
        });

        return root;
    }

    private void Validation(){
        phone = phoneNum.getText().toString();
        uName = name.getText().toString();
        pass = password.getText().toString();
        rePass = rePassWord.getText().toString();
        if(phone.isEmpty()){
            phoneNum.setError("Vui lòng điền thông tin");
            phoneNum.requestFocus();
        }
        else if(!numberCheck(phone)){
            phoneNum.setError("Vui lòng điền số điện thoại");
            phoneNum.requestFocus();
        }
        else if(uName.isEmpty()){
            name.setError("Vui lòng điền thông tin");
            name.requestFocus();
        }
        else if(pass.isEmpty()){
            password.setError("Vui lòng điền thông tin");
            password.requestFocus();
        }
        else if(!passwordValidation(pass)){
            password.setError("Điền tối đa 6 chữ số");
            password.requestFocus();
        }
        else if(rePass.isEmpty()){
            rePassWord.setError("Vui lòng điền thông tin");
            rePassWord.requestFocus();
        }
        else if(!rePassWordValidation(rePass)){
            rePassWord.setError("Vui lòng nhập đúng với mật khẩu");
            rePassWord.requestFocus();
        }
        else{
            createAccount();
        }


    }

    private boolean rePassWordValidation(String rePass) {
        if(pass.equals(rePass)){
            return true;
        }
        else{
            return false;
        }
    }

    private void createAccount() {
        progressDialog.setMessage("Đang tạo tài khoản");
        progressDialog.show();
        sendDataToDB();
    }

    //send to firebase
    private void sendDataToDB() {
        String regTime = ""+System.currentTimeMillis();
        HashMap<String,Object> data = new HashMap<>();
        data.put("Register time",regTime);
        data.put("phoneNumber",phone);
        data.put("username",uName);
        data.put("password",pass);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(phone).setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //db update
                        progressDialog.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //Validate mật khẩu
    private boolean passwordValidation(String pass) {
        Pattern p = Pattern.compile(".{6}");
        Matcher m = p.matcher(pass);
        return m.matches();
    }

    //Validate sdt
    private boolean numberCheck(String phone) {
        Pattern p = Pattern.compile("[0-9]{10}");
        Matcher m = p.matcher(phone);
        return m.matches();
    }
}
