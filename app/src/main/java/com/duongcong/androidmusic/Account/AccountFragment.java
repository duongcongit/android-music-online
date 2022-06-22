package com.duongcong.androidmusic.Account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duongcong.androidmusic.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {
    private Button btnPopup,btnLogin;
    private TextView username,userName,userRegisDate;
    private String user_email,user_password,user_displayName;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_account,container,false);

        btnLogin =(Button) root.findViewById(R.id.btn_login_test);
        btnPopup =(Button) root.findViewById(R.id.ButtonPopupMenu);
        username = (TextView) root.findViewById(R.id.user_name);
        userName = (TextView) root.findViewById(R.id.user_Name);
        userRegisDate = (TextView) root.findViewById(R.id.user_date_regis);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();



        //Lay du lieu tu phan dang nhap
        Intent intent = getActivity().getIntent();
        user_email = firebaseUser.getEmail();
        user_password = intent.getStringExtra("password");
        user_displayName = firebaseUser.getDisplayName();




        //Gan du lieu
        username.setText(user_email);
        userName.setText(user_displayName);


//        String lastSignInDate = user.auth().currentUser.metadata.lastSignInDate;
//        String regisDate = Auth.auth().currentUser.metadata.creationDate;


        //Popup button
        btnPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), btnPopup);
                popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.menu_dowwnload:
                                Toast.makeText(getActivity().getApplicationContext(), "download", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu_logout:
                                firebaseAuth.signOut();
                                Toast.makeText(getActivity().getApplicationContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.menu_upload:
                                Toast.makeText(getActivity().getApplicationContext(), "upload", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(),LoginActivity.class);
                startActivity(intent);

            }
        });


        return root;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
