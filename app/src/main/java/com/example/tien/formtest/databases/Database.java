package com.example.tien.formtest.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.tien.formtest.model.OrderDTO;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.sql.SQLData;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tien on 11/01/2018.
 */

public class Database extends SQLiteAssetHelper{
    private static final String DB_Name="FormTestDB.db";
    private static final int DB_Ver=1;

    public Database(Context context) {
        super(context, DB_Name, null, DB_Ver);
    }

    public List<OrderDTO> getCart(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect={"ProductId", "ProductName", "Quantity", "Price", "Discount"};
        String sqlTable="OrderDetail";

        qb.setTables(sqlTable);

        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        List<OrderDTO> result = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                result.add(new OrderDTO(c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")), c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")), c.getString(c.getColumnIndex("Discount"))));
            } while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(OrderDTO order){
        SQLiteDatabase db = getReadableDatabase();
        String query =
                String.format("INSERT INTO OrderDetail(ProductId, ProductName, Quantity, Price, Discount) VALUES('%s','%s','%s','%s','%s');"
                ,order.getProductId()
                ,order.getProductName()
                ,order.getQuantity()
                ,order.getPrice()
                ,order.getDiscount());

        db.execSQL(query);
    }

    public void clearCart(){
        SQLiteDatabase db = getReadableDatabase();
        //String query = String.format("delete * from OrderDetail");

        db.execSQL("DELETE FROM OrderDetail");
    }

    //Favorites
    public void addToFavorites(String formID){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(FoodId) VALUES('%s');", formID);
        db.execSQL(query);

    }
    public void removeFromFavorites(String formID){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE FromID='%s';", formID);
        db.execSQL(query);

    }

    public boolean isFavorites(String formID){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("Select * FROM Favorites WHERE FromID='%s';", formID);
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount()<=0){
            cursor.close();
        }
        cursor.close();
        return true;
    }

}
