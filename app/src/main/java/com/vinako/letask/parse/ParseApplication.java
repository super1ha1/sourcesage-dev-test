package com.vinako.letask.parse;

/**
 * Created by dackhue on 18/5/15.
 */

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;


public class ParseApplication extends Application {

    private static final String TAG = ParseApplication.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();


        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "GVI504JtCJzB5Gze2j3D2lh9jL7MsVk6KY6n8dO9", "cWQhcyMDaAinCFApyhFdg2j4oQltfBLAinBJpQuf");


        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);


//        Push notification
//        ParsePush.subscribeInBackground("", new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    Log.d(TAG, "successfully subscribed to the broadcast channel.");
////                    if( ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())){
////                        Log.d(TAG, "current user is anonymous, set for installation");
//                    ParseInstallation curentIns = ParseInstallation.getCurrentInstallation();
////                        curentIns.put("user", ParseUser.getCurrentUser());
//                    curentIns.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e == null) {
//                                Log.d(TAG, "Save successfully current installation");
//                                Log.d(TAG, "installationId by get: " + ParseInstallation.getCurrentInstallation().getInstallationId());
//                                Log.d(TAG, "installationId by getString: " + ParseInstallation.getCurrentInstallation().getString("installationId"));
//                            } else {
//                                Log.d(TAG, "Can not save the installation: " + e.getMessage());
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                } else {
//                    Log.e(TAG, "failed to subscribe for push" +  e.getMessage());
//                }
//            }
//        });
    }

}