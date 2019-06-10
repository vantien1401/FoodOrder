package com.example.tien.formtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.tien.formtest.common.Common;
import com.example.tien.formtest.databases.Database;
import com.example.tien.formtest.iterface.ItemClickListener;
import com.example.tien.formtest.model.FormDTO;
import com.example.tien.formtest.viewHolder.FormViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListForm extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference formList;

    FirebaseRecyclerAdapter<FormDTO, FormViewHolder> adapter;

    //search functionality
    FirebaseRecyclerAdapter<FormDTO, FormViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar searchBar;

    String categoryId = "";

    Database localData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_form);

        //init Firebase
        database = FirebaseDatabase.getInstance();
        formList = database.getReference("Form");

        recyclerView = (RecyclerView) findViewById(R.id.listFormRecycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        //local data
        localData = new Database(this);

        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");

        if (!categoryId.isEmpty() && categoryId != null) {
            if (Common.isConnectedToInterner(getBaseContext()))
                loadListForm();
            else {
                Toast.makeText(ListForm.this, "Please check your connect internet!!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //init search
        searchBar = (MaterialSearchBar) findViewById(R.id.search_bar);
        searchBar.setHint("Enter your food");

        //func load suggest to firebase
        loadSuggest();
        searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //When user type their test, we will change suggest list
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When search bar is close
                //Restore original adapter
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //when search finish
                //show result adapter
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<FormDTO, FormViewHolder>(
                FormDTO.class,
                R.layout.form_item,
                FormViewHolder.class,
                formList.orderByChild("Name").equalTo(text.toString())) {

            @Override
            protected void populateViewHolder(FormViewHolder viewHolder, FormDTO model, int position) {
                viewHolder.formName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.formImage);
                final FormDTO local = model;

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new active
                        Intent formDetail = new Intent(ListForm.this, FormDetail.class);
                        formDetail.putExtra("FormId", searchAdapter.getRef(position).getKey()); //Send id form to call active new
                        startActivity(formDetail);
                    }
                });

            }
        };
        recyclerView.setAdapter(searchAdapter); //set adapter to recycler is search result
    }

    private void loadSuggest() {
        formList.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FormDTO item = postSnapshot.getValue(FormDTO.class);
                    //add name of form to suggest list
                    suggestList.add(item.getName());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListForm() {
        adapter = new FirebaseRecyclerAdapter<FormDTO, FormViewHolder>(FormDTO.class, R.layout.form_item,
                FormViewHolder.class, formList.orderByChild("menuId").equalTo(categoryId) // like: select * from Form where MenuId=
        ) {
            @Override
            protected void populateViewHolder(final FormViewHolder viewHolder, final FormDTO model, final int position) {

                viewHolder.formName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.formImage);
                final FormDTO local = model;

                //Add favorites
                if (localData.isFavorites(adapter.getRef(position).getKey()))
                    viewHolder.formFavImage.setImageResource(R.drawable.ic_favorite_black_24dp);

                //click change favorites
                viewHolder.formFavImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localData.isFavorites(adapter.getRef(position).getKey())) {
                            localData.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.formFavImage.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(ListForm.this, "" + model.getName() + "was add to Favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            localData.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.formFavImage.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(ListForm.this, "" + model.getName() + "was remove from Favorites", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start new active
                        Intent formDetail = new Intent(ListForm.this, FormDetail.class);
                        formDetail.putExtra("FormId", adapter.getRef(position).getKey()); //Send id form to call active new
                        startActivity(formDetail);


                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
    }
}
