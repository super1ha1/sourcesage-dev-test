package com.vinako.letask.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Khue on 29/7/2015.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static void showMessage(Context context, String message){
        try {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d(TAG, "Error when make Toast: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void stopRefresh(SwipeRefreshLayout swipeRefreshLayout){
        try {
            if ( swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
                Log.d(TAG,"Stop refresh here!");
            }
        } catch (Exception e) {
            Log.d(TAG, "Error when stop refresh : " + e.getMessage());
            e.printStackTrace();
        }
    }
    public  static void showProgressDialogLoading(Context activity){
        try {
            ProgressDialog dialog = new ProgressDialog(activity);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();
            Storage.dialog = dialog;
            Log.d(TAG, "Show progress Dialog");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public static void stopProgressDialogLoading(){
        try {
            if( Storage.dialog != null){
                Storage.dialog.dismiss();
                Log.d(TAG, "Dismiss progress dialog here");
                Storage.dialog = null;
            }
        } catch (Exception e) {
            Log.d(TAG, "Can not dismiss progress dialog: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public static boolean checkNetwork(Context context){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if( networkInfo != null && networkInfo.isConnected()){
                Log.d(TAG, "is connected");
                return true;
            }else {
                Utils.showMessage(context, "No Network Available");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
