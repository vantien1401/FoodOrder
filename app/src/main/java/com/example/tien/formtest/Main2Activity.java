package com.example.tien.formtest;

import android.content.Intent;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tien.formtest.common.Common;
import com.example.tien.formtest.iterface.ItemClickListener;
import com.example.tien.formtest.model.Category;
import com.example.tien.formtest.model.OrderDTO;
import com.example.tien.formtest.service.ListenOrder;
import com.example.tien.formtest.viewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference reference;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    TextView fullNamePoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Point List");
        setSupportActionBar(toolbar);

        //init data
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Category");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, Cart.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get name for point
        View headerView = navigationView.getHeaderView(0);
        fullNamePoint = (TextView) headerView.findViewById(R.id.fullName);
        fullNamePoint.setText(Common.currenPointDTO.getName());

        //load data
        recycler_menu = (RecyclerView) findViewById(R.id.recyclerview_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        Paper.init(this);

        if (Common.isConnectedToInterner(getBaseContext()))
            loadMenu();
        else {
            Toast.makeText(Main2Activity.this, "Please check your connect internet!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        //register service
        Intent service = new Intent(Main2Activity.this, ListenOrder.class);
        startService(service);
    }
    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, reference) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get new category and send activity
                        Intent formList = new Intent(Main2Activity.this, ListForm.class);
                        //CategoryID is key, so we just get key of this item
                        formList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(formList);

                    }
                });

            }
        };
        recycler_menu.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_refresh) {
            loadMenu();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_List) {
            Intent listIntent = new Intent(Main2Activity.this, ListForm.class);
            startActivity(listIntent);

        } else if (id == R.id.nav_cart) {
            Intent cartIntent  = new Intent(Main2Activity.this, Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_oder) {
            Intent orderIntent = new Intent(Main2Activity.this, OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_signOut) {
            //delete remember login
            Paper.book().destroy();

            //logout
            Intent signIntent = new Intent(Main2Activity.this, SignIn.class);
            signIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(signIntent);

    }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
