//package com.vinako.letask.parse;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//
//import com.parse.FunctionCallback;
//import com.parse.ParseAnalytics;
//import com.parse.ParseAnonymousUtils;
//import com.parse.ParseCloud;
//import com.parse.ParseException;
//import com.parse.ParsePushBroadcastReceiver;
//import com.parse.ParseUser;
//import com.parse.SaveCallback;
//import com.vinako.letask.R;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//
//
///**
// * Created by dackhue on 19/6/15.
// */
//public class CustomParseReceiver extends ParsePushBroadcastReceiver {
//    public static final String PARSE_DATA_KEY = "com.parse.Data";
//    private static final String TAG = CustomParseReceiver.class.getSimpleName();
//    private String installationId, notification_for, trackingId, senderUserId, message;
//    private String userRole, userStatus;
//    private JSONObject receiverSendData, courierSendData, dataReceived;
//    private int user_type;
//    private Context context;
//    private Intent intent;
//    private JSONObject locDict;
//    private ParseReceiverCallBack callBack;
//    private boolean isReceiver, isUserReceiver;
//
//    public static final String USER_ROLE = "USER_ROLE", USER_STATUS= "USER_STATUS", NOTIFICATION_FOR = "notification_for";
//    @Override
//    protected void onPushReceive(Context context, Intent intent) {
//        try{
//            this.context = context;
//            this.intent = intent;
//            receiveData(context, intent);
//        }catch (Exception e ){
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    protected void onPushOpen(Context context, Intent intent) {
//        try{
//            Log.d(TAG, "Push Open");
//            String jobId, userId, userRole, userStatus;
//            Bundle bundle = intent.getExtras();
//
//            userRole = bundle.getString(USER_ROLE);
//            userStatus = bundle.getString(USER_STATUS);
//            jobId = bundle.getString("job");
//            userId = bundle.getString("user");
//            Storage.chatTrackingId = bundle.getString("job");
//            Storage.currentUserToChatId = bundle.getString("user");
//            String notificationFor = bundle.getString("notification_for");
//            Log.d(TAG, "Notification for: " + notificationFor + "chatTrackingId: " + Storage.chatTrackingId + " UserToChatId: " + Storage.currentUserToChatId);
//
//            if(ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())){
//                Log.d(TAG, "current is anonymous, log in again");
//                context.startActivity(new Intent(context, LoginActivity.class));
//                Utils.showMessage(context, "Please log in to receive message");
//            }else {
//                if( jobId != null && userId != null ){
//                    Intent chatIntent = new Intent(context, ChatActivity.class);
//                    chatIntent.putExtras(intent.getExtras());
//                    chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                    Utils.getListChat(Storage.chatTrackingId);
//                    ParseAnalytics.trackAppOpenedInBackground(intent, new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e == null) {
//                                Log.d(TAG, "User open to view message");
//                            } else {
//                                Log.d(TAG, "can not send track Open to parse: " + e.getMessage());
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                    context.startActivity(chatIntent);
//                }else if( userRole != null && userStatus != null){
//                    Log.d(TAG, "Push for Admin react to user registration");
//                    Intent intentStartActivity  = new Intent(context, StartActivity.class);
//                    intentStartActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intentStartActivity);
//                } else {
//                    Log.d(TAG, "Push for new job");
//                    Intent intentStartActivity  = new Intent(context, StartActivity.class);
//                    intentStartActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intentStartActivity);
//                }
//            }
//
//        }catch ( Exception e ){
//            e.printStackTrace();
//        }
//
//
//    }
//
//
//    private void receiveData(Context context, Intent intent) {
//        dataReceived = getDataFromIntent(intent);
//        try {
//            notification_for = dataReceived.getString("notification_for");
//            if( notification_for != null && notification_for.equals("Chat")){
//                senderUserId = dataReceived.getString("fromUser");
//                message = dataReceived.getString("alert");
//                trackingId = dataReceived.getString("tracking_num");
//                Log.d(TAG, "from : "+ senderUserId + " message: " + message + " trackingId: " + trackingId );
//
//                ChatMessage newMessage = new ChatMessage(
//                        UserType.OTHER,
//                        Status.DELIVERED,
//                        message,
//                        (new Date().getTime())
//                );
//
//                Conversation currentConversation = Storage.chatMap.get(trackingId);
//                if( currentConversation != null){
//                    Log.d(TAG, "Current conversation is present, continue");
//                    List<ChatMessage> currentChatList = currentConversation.getChatList();
//                    currentChatList.add(newMessage);
//                    Log.d(TAG, "put message to new conversation: " + trackingId + " newMessge: " + newMessage.getMessageText() + " size: " + currentChatList.size());
//                }else {
//                    Log.d(TAG, "Start new conversation");
//                    ArrayList<ChatMessage> newChatList = new ArrayList<ChatMessage>();
//                    newChatList.add(newMessage);
//                    currentConversation = new Conversation(
//                            senderUserId,
//                            ParseUser.getCurrentUser().getObjectId(),
//                            trackingId,
//                            newChatList
//                    );
//                    Storage.chatMap.put(trackingId, currentConversation);
//                    Log.d(TAG, "put message to new conversation: " + trackingId + " newMessge: " + newMessage.getMessageText() + " size: " + newChatList.size());
//                }
//                updateDisplayMessageIfInChatRoom();
//
//                if(dataReceived.getString("alert") != null && !dataReceived.getString("alert").equals("") ){
//                    Log.d(TAG, "Display notification !");
//                    Bundle bundle  = new Bundle();
//                    bundle.putString("user", senderUserId);
//                    bundle.putString("job", trackingId);
//                    intent.putExtras(bundle);
//                    super.onPushReceive(context, intent);
//                }
//
//            }else if (notification_for != null && notification_for.equals("Track") ){
//                user_type = dataReceived.getInt("user_type");
//                Log.d(TAG, "user_type: " + user_type);
//                if( user_type != 0 ){
//                    switch (user_type){
//                        case Constants.UserType.RECEIVER:
//                            receiverSendData = dataReceived;
//                            installationId = receiverSendData.getString("installationId");
//                            Log.d(TAG, "installationId: " + installationId);
//                            getCurrentGeoPoint();
//                            pushToReceiver();
//                            break;
//                        case Constants.UserType.COURIER:
//                            courierSendData = dataReceived;
//                            locDict = courierSendData.getJSONObject("locDict");
//                            Log.d(TAG, "locDict receive: " + locDict);
//                            try{
//                                Log.d(TAG, " Activity Name: " + getActivity(context, intent));
//                                Activity currentAct = Utils.getForeGroundActivity();
//                                if( currentAct != null){
//                                    Log.d(TAG, "current activity: " + currentAct.getClass().getSimpleName());
//                                    callBack =  (ParseReceiverCallBack) currentAct;
//                                }
//                            }catch (Exception e){
//                                Log.d(TAG, "Activity must implement ParseReceive Callback " + e.getMessage());
//                                e.printStackTrace();
//                            }
//                            updateCourierLocationOnMap();
//                            break;
//                    }
//                }
//                if(  dataReceived.getString("sound") != null && !dataReceived.getString("sound").equals("") ){
//                    Log.d(TAG, "Display notification !");
//                    super.onPushReceive(context, intent);
//                }
//
//            }else if(notification_for != null && notification_for.equals("AdminApprove")){
//                    Log.d(TAG, "Push for Admin Approve: " + notification_for);
//                    Bundle bundle  = new Bundle();
//                    userRole = dataReceived.getString("featureApprovalFor");
//                    userStatus = dataReceived.getString("ApproveStatus");
//                    bundle.putString(USER_ROLE, userRole);
//                    bundle.putString(USER_STATUS, userStatus);
//                    bundle.putString(NOTIFICATION_FOR, notification_for);
//                    String message = getMessageForUserNotification(userRole, userStatus);
//
//                    dataReceived.put("alert", message);
//                    bundle.putString(PARSE_DATA_KEY, dataReceived.toString());
//                    intent.putExtras(bundle);
//                    super.onPushReceive(context, intent);
//            } else{
//                Log.d(TAG, "Push for  " + notification_for);
//                Log.d(TAG, "Display notification !");
//                Bundle bundle  = new Bundle();
//                bundle.putString("user", senderUserId);
//                bundle.putString("job", trackingId);
//                bundle.putString("notification_for", notification_for);
//                intent.putExtras(bundle);
//                super.onPushReceive(context, intent);
//
//
//            }
//            //user type of the one who receive the push notification
//            //Wrong, user type is the user who send the push
//
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//    }
//
//    private String getMessageForUserNotification(String userRole, String userStatus) {
//        String message =null;
//        switch (userStatus){
//            case Constants.UserAppliedStatusString.APPROVED:
//                message = "You are approved now, you can use the " + userRole +  " feature";
//                break;
//            case Constants.UserAppliedStatusString.REJECTED:
//                message = "Your application to use the " + userRole
//                        +  " feature has been denied, please contact us for more details";
//                break;
//            case Constants.UserAppliedStatusString.DELETED:
//                message = "You can no longer use the " + userRole
//                        +  " feature, please contact us for more details";
//                break;
//        }
//        return message;
//    }
//
//    public static  void updateDisplayMessageIfInChatRoom() {
//        Activity currentActivity = Utils.getForeGroundActivity();
//        if( currentActivity != null &&  currentActivity.getClass().getSimpleName().equals(ChatActivity.class.getSimpleName())){
//            Log.d(TAG, "Chat activity, Re-update chat list and view: ");
//            ((ChatActivity) currentActivity).updateChatList();
//            ((ChatActivity) currentActivity).updateChatListView();
//        }
//    }
//
//    private void updateChatListView() {
//        final Activity currentActivity = Utils.getForeGroundActivity();
//        if( currentActivity != null &&  currentActivity.getClass().getSimpleName().equals("ChatActivity")){
//            currentActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    ListView chatListView = (ListView) currentActivity.findViewById(R.id.chat_list_view);
//                    if (chatListView != null && chatListView.getAdapter() != null) {
//                        ((ArrayAdapter) chatListView.getAdapter()).notifyDataSetChanged();
//                        Log.d(TAG, "update chatListView");
//                    }
//                }
//            });
//        }
//    }
//
//    private void updateCourierLocationOnMap() {
//        Log.d(TAG, "update courier location on map");
//        if( callBack != null)
//            callBack.updateCourierLocationOnMap(locDict);
//    }
//
//    private void pushToReceiver() {
//        Log.d(TAG, "push to receiver");
//        HashMap<String, Object> params = new HashMap<String, Object>();
//        params.put("installationId", installationId);
//        params.put("user_type", Constants.UserType.COURIER);
//        params.put("locDict", locDict);
//        ParseCloud.callFunctionInBackground("sendPushToReceiver", params, new FunctionCallback<String>() {
//            public void done(String success, ParseException e) {
//                if (e == null) {
//                    Log.d(TAG, "success: " + success);
//                } else {
//                    Log.d(TAG, "error: " + e.getMessage());
//                }
//            }
//        });
//    }
//
//    private void getCurrentGeoPoint() {
//        GPSTracker mGPS = new GPSTracker(context);
//        if(mGPS.canGetLocation ){
//            mGPS.getLocation();
//            locDict = Utils.createJSONLocationObject(mGPS.getLatitude(), mGPS.getLongitude());
//            Log.d(TAG, "locDict: " + locDict);
//            mGPS.stopUsingGPS();
//            mGPS = null;
//            Log.d(TAG, "Stop using GPS after use");
//        }else{
//            System.out.println("Unable to get current GeoPoint");
//        }
//    }
//    private JSONObject getDataFromIntent(Intent intent) {
//        JSONObject data = null;
//        try {
//            data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
//            Log.d(TAG, "receive data: " + data);
//        } catch (JSONException e) {
//            Log.d(TAG, "can not get JSON object from call");
//        }
//        return data;
//    }
//
//    public interface ParseReceiverCallBack {
//        void updateCourierLocationOnMap(JSONObject locDict);
//    }
//}
