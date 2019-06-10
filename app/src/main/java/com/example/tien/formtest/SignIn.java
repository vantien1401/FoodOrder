package com.example.tien.formtest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tien.formtest.common.Common;
import com.example.tien.formtest.model.PointDTO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import java.util.zip.Inflater;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    MaterialEditText edtPointId, edtPass;
    Button btnSignIn;
    CheckBox ckbRemember;
    TextView txtForgotPwd;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPointId = (MaterialEditText) findViewById(R.id.editPoint);
        edtPass = (MaterialEditText) findViewById(R.id.editPass);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        ckbRemember = (CheckBox) findViewById(R.id.ckbRemember);
        txtForgotPwd = (TextView) findViewById(R.id.txtForgotPwd);

        Paper.init(this);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Point");

        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPwdDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInterner(getBaseContext())) {
                    //save user and pass
                    if (ckbRemember.isChecked()){
                        Paper.book().write(Common.USER_KEY, edtPointId.getText().toString());
                        Paper.book().write(Common.PWD_KEY, edtPass.getText().toString());
                    }


                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please waiting...");
                    mDialog.show();

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //check if point not exits in data
                            if (dataSnapshot.child(edtPointId.getText().toString()).exists()) {

                                mDialog.dismiss();
                                // get Point information
                                PointDTO pointDTO = dataSnapshot.child(edtPointId.getText().toString()).getValue(PointDTO.class);
                                pointDTO.setPhone(edtPointId.getText().toString());
                                if (pointDTO.getPass().equals(edtPass.getText().toString())) {
//                                Toast.makeText(SignIn.this, "Thành công", Toast.LENGTH_SHORT).show();

                                    Intent homeIntent = new Intent(SignIn.this, Main2Activity.class);
                                    Common.currenPointDTO = pointDTO;
                                    startActivity(homeIntent);
                                    finish();
                                } else {
                                    mDialog.dismiss();
                                    Toast.makeText(SignIn.this, "Password is wrong", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "Point not exists in data", Toast.LENGTH_SHORT).show();
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast.makeText(SignIn.this, "Please check your connect internet!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

    private void showForgotPwdDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your secure code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_password_layout, null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone = (MaterialEditText) forgot_view.findViewById(R.id.editPoint);
        final MaterialEditText edtSecureCode = (MaterialEditText) forgot_view.findViewById(R.id.editSecureCode);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //check if point available
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        PointDTO point = dataSnapshot.child(edtPhone.getText().toString()).getValue(PointDTO.class);
                        if(point.getSecureCode().equals(edtSecureCode.getText().toString()))
                            Toast.makeText(SignIn.this, "Your Password: "+point.getPass(), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(SignIn.this, "Wrong secure code ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();


    }
}
