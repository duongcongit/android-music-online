package com.duongcong.androidmusic.Account;

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

import com.duongcong.androidmusic.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpTabFragment extends Fragment {
    private Button btnSignUp;
    private EditText email,password,rePassWord,name;
    private String uEmail,pass,rePass,uName;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.sign_up_tab_fragment,container,false);

        btnSignUp =(Button) root.findViewById(R.id.btnSignUp);
        email =(EditText) root.findViewById(R.id.email);
        name =(EditText) root.findViewById(R.id.name);
        password =(EditText) root.findViewById(R.id.password);
        rePassWord =(EditText) root.findViewById(R.id.conf_password);

        firebaseAuth = FirebaseAuth.getInstance();

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
        uEmail = email.getText().toString();
        uName = name.getText().toString();
        pass = password.getText().toString();
        rePass = rePassWord.getText().toString();
        if(uEmail.isEmpty()){
            email.setError("Vui lòng điền thông tin");
            email.requestFocus();
        }
        else if(!emailCheck(uEmail)){
            email.setError("Vui lòng điền đúng định dạng email");
            email.requestFocus();
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
            password.setError("Điền tối đa 6 kí tự");
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
        sendDataToDB();
    }

    //send to firebase
    private void sendDataToDB() {
        //FirebaseAuth
        firebaseAuth.createUserWithEmailAndPassword(uEmail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseUser = firebaseAuth.getCurrentUser();
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(uName).build();
                    firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity().getApplicationContext(), "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
//                    Fragment fragment = new LoginTabFragment();
//                    FragmentManager fm = getFragmentManager();
//                    FragmentTransaction transaction = fm.beginTransaction();
//                    transaction.replace(R.id.sign_up_tab_fragment, fragment);
//                    transaction.commit();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Đăng kí thất bại", Toast.LENGTH_SHORT).show();
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

    //Validate mật khẩu
    private boolean passwordValidation(String pass) {
        Pattern p = Pattern.compile(".{6}");
        Matcher m = p.matcher(pass);
        return m.matches();
    }

    private boolean emailCheck(String uEmail) {
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
        Matcher m = emailPattern.matcher(uEmail);
        return m.matches();
    }
}
