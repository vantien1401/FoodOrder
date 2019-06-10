package com.example.tien.formtest.common;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.tien.formtest.model.PointDTO;

/**
 * Created by Tien on 12/12/2017.
 */

public class Common {
    public  static PointDTO currenPointDTO;


    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return  "Placed";

        else if (status.equals("1"))
            return  "Shipping";

        else
            return  "Shipped";
    }

    public static boolean isConnectedToInterner(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info !=null){
                for (int i =0; i<info.length; i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static final String DELETE = "Delete";

    public static final String USER_KEY = "User";

    public static final String PWD_KEY = "Password";
}
