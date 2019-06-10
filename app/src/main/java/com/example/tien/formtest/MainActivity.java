package com.example.tien.formtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    Button btnSignUp, btnSignIn;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        txtSlogan = (TextView) findViewById(R.id.txtSlogan);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Nabila.otf");
        txtSlogan.setTypeface(typeface);

        Paper.init(this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp);

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);

            }
        });

        //check remember
        String user = Paper.book().read(Common.USER_KEY);
        String pass = Paper.book().read(Common.PWD_KEY);

        if (user!=null && pass!=null){
            if (!user.isEmpty() && !pass.isEmpty())
                login(user, pass);
        }

    }

    private void login(final String point, final String pass) {


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference("Point");

        if (Common.isConnectedToInterner(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please waiting...");
            mDialog.show();

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //check if point not exits in data
                    if (dataSnapshot.child(point).exists()) {

                        mDialog.dismiss();
                        // get Point information
                        PointDTO pointDTO = dataSnapshot.child(point).getValue(PointDTO.class);
                        pointDTO.setPhone(point);
                        if (pointDTO.getPass().equals(pass)) {
//                                Toast.makeText(SignIn.this, "Thành công", Toast.LENGTH_SHORT).show();

                            Intent homeIntent = new Intent(MainActivity.this, Main2Activity.class);
                            Common.currenPointDTO = pointDTO;
                            startActivity(homeIntent);
                            finish();
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Password is wrong", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Point not exists in data", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "Please check your connect internet!!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
