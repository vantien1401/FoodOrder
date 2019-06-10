package com.example.tien.formtest;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tien.formtest.common.Common;
import com.example.tien.formtest.model.PointDTO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {
    MaterialEditText edtPointId, edtName, edtPass, edtSecureCode;
    Button btnSignnUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = (MaterialEditText) findViewById(R.id.editPointName);
        edtPointId = (MaterialEditText) findViewById(R.id.editPoint);
        edtPass = (MaterialEditText) findViewById(R.id.editPass);
        edtSecureCode = (MaterialEditText) findViewById(R.id.editSecureCode);

        btnSignnUp = (Button) findViewById(R.id.btnSignUp);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference("Point");

        btnSignnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInterner(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Please waiting...");
                    mDialog.show();

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //check already point id
                            if (dataSnapshot.child(edtPointId.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Point Id already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();
                                PointDTO point = new PointDTO(edtName.getText().toString(),
                                        edtPass.getText().toString(),
                                        edtSecureCode.toString());
                                reference.child(edtPointId.getText().toString()).setValue(point);
                                Toast.makeText(SignUp.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast.makeText(SignUp.this, "Please check your connect internet!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
