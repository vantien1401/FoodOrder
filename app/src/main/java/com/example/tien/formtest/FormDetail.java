package com.example.tien.formtest;

import android.media.Rating;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.tien.formtest.common.Common;
import com.example.tien.formtest.databases.Database;
import com.example.tien.formtest.model.FormDTO;
import com.example.tien.formtest.model.OrderDTO;
import com.example.tien.formtest.model.RatingDTO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.lang.reflect.Array;
import java.util.Arrays;

public class FormDetail extends AppCompatActivity implements RatingDialogListener{
    TextView formName, formDes, formPrice;
    ImageView formImage;
    CollapsingToolbarLayout layout;
    FloatingActionButton btnCart, btnRating;
    ElegantNumberButton numberButton;
    RatingBar rtBar;

    String formId;

    FirebaseDatabase data;
    DatabaseReference forms;
    DatabaseReference ratingTb1;

    FormDTO currentForm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_detail);

        //init firebase
        data = FirebaseDatabase.getInstance();
        forms = data.getReference("Form");
        ratingTb1 = data.getReference("Rating");

        //init view active
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);
        btnRating = (FloatingActionButton) findViewById(R.id.btnRating);
        rtBar = (RatingBar) findViewById(R.id.ratingBar);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new OrderDTO(
                        formId,
                        currentForm.getName(),
                        numberButton.getNumber(),
                        currentForm.getPrice(),
                        currentForm.getDiscount()

                ));
                Toast.makeText(FormDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        layout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_form_detail);

        formName = (TextView) findViewById(R.id.form_name_detail);
        formDes = (TextView) findViewById(R.id.form_description);
        formPrice = (TextView) findViewById(R.id.form_price_detail);
        formImage = (ImageView) findViewById(R.id.img_form_detail);

        layout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        layout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get Form id from intent
        if(getIntent() != null)
            formId = getIntent().getStringExtra("FormId");
        if(!formId.isEmpty()){
            if (Common.isConnectedToInterner(getBaseContext())){
                getDetailForm(formId);
                getDetailRating(formId);
            }

            else {
                Toast.makeText(FormDetail.this, "Please check your connect internet!!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    private void getDetailRating(String formId) {
        Query foodRating = ratingTb1.orderByChild("formId").equalTo(formId);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                RatingDTO item = postSnapshot.getValue(RatingDTO.class);

                sum+=Integer.parseInt(item.getRateValue());
                count++;

                }
                if (count != 0){
                    float average = sum/count;
                    rtBar.setRating(average);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad" , "Not Good", "Quite Ok", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescription("Please select some star and give your feedback ")
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here")
                .setHintTextColor(R.color.colorAccent)
                .setCommentBackgroundColor(R.color.colorPrimary)
                .setCommentTextColor(android.R.color.white)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(this)
                .show();



    }

    private void getDetailForm(String formId) {
        forms.child(formId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentForm = dataSnapshot.getValue(FormDTO.class);

                //set image
                Picasso.with(getBaseContext()).load(currentForm.getImage()).into(formImage);

                layout.setTitle(currentForm.getName());
                formPrice.setText(currentForm.getPrice());
                formDes.setText(currentForm.getDescription());
                formName.setText(currentForm.getName());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int value, String comment) {
        //get Rating and upload to fireBase
        final RatingDTO rating = new RatingDTO(Common.currenPointDTO.getPhone(),
                formId,
                String.valueOf(value),
                comment);

        ratingTb1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currenPointDTO.getPhone()).exists()){
                    //remove old value or let it be - useless function
                    ratingTb1.child(Common.currenPointDTO.getPhone()).removeValue();
                    //update new value
                    ratingTb1.child(Common.currenPointDTO.getPhone()).setValue(rating);
                }
                else {
                    //update new value
                    ratingTb1.child(Common.currenPointDTO.getPhone()).setValue(rating);

                }
                Toast.makeText(FormDetail.this, "Thank you for submit rating", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
