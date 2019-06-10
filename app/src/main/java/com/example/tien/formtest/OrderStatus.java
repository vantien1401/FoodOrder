package com.example.tien.formtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;

import com.example.tien.formtest.common.Common;
import com.example.tien.formtest.model.OrderDTO;
import com.example.tien.formtest.model.RequestDTO;
import com.example.tien.formtest.viewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<RequestDTO, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //init data
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Request");

        //init view
        recyclerView = (RecyclerView) findViewById(R.id.recycler_ListOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //if we start OrderStatus activity form Home Action
        // We will not put any extra, so we just loadOrder by phone from Common
        if (getIntent() == null)
            loadOrder(Common.currenPointDTO.getPhone());
        else
            loadOrder(getIntent().getStringExtra("userPhone"));

        loadOrder(Common.currenPointDTO.getPhone());


    }

    private void loadOrder(String phone) {
        adapter = new FirebaseRecyclerAdapter<RequestDTO, OrderViewHolder>(
                RequestDTO.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                reference.orderByChild("phone").equalTo(phone)) {

            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, RequestDTO model, int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                
            }
        };
        recyclerView.setAdapter(adapter);
    }

}
