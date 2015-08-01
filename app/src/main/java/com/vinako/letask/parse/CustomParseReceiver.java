package com.vinako.letask.parse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.vinako.letask.R;
import com.vinako.letask.activities.AnswerActivity;
import com.vinako.letask.utility.Constants;
import com.vinako.letask.utility.Storage;
import com.vinako.letask.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by dackhue on 19/6/15.
 */
public class CustomParseReceiver extends ParsePushBroadcastReceiver {
    public static final String PARSE_DATA_KEY = "com.parse.Data";
    private static final String TAG = CustomParseReceiver.class.getSimpleName();
    private String installationId, notification_for, trackingId, questionId, message;
    private String userRole, userStatus;
    private JSONObject receiverSendData, courierSendData, dataReceived;
    private int user_type;
    private Context context;
    private Intent intent;
    private JSONObject locDict;
    private ParseReceiverCallBack callBack;
    private boolean isReceiver, isUserReceiver;

    public static final String USER_ROLE = "USER_ROLE", USER_STATUS= "USER_STATUS", NOTIFICATION_FOR = "notification_for";
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        try{
            this.context = context;
            this.intent = intent;
            receiveData(context, intent);
        }catch (Exception e ){
            e.printStackTrace();
        }

    }

    private void receiveData(final Context context, final Intent intent) {
        dataReceived = getDataFromIntent(intent);
        try {
            notification_for = dataReceived.getString("notification_for");
            if( notification_for != null && notification_for.equals("updateAnswerList")){
                questionId = dataReceived.getString("questionId");
                Log.d(TAG, "from : " + questionId + " notification for: " + notification_for);
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Constants.TableName.QUESTION);
                query.getInBackground(questionId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject question, ParseException e) {
                        if (e == null) {
                            Log.d(TAG, "Question found, update list view");
                            try{
                                Log.d(TAG, " Activity Name: " + getActivity(context, intent));
                                Activity currentAct = Utils.getForeGroundActivity();
                                if( currentAct != null){
                                    Log.d(TAG, "current activity: " + currentAct.getClass().getSimpleName());
                                    if( currentAct.getClass().getSimpleName().equals(AnswerActivity.class.getSimpleName())){
                                        ((AnswerActivity)currentAct).getListAnswerByQuestion(Constants.PARSE_QUERY.NO_SKIP, question);
                                    }else {
                                        Log.d(TAG, "Not current is AnswerActivty: Do nothing"  );
                                    }
                                }
                            }catch (Exception ex){
                                Log.d(TAG, "Error when update activity on Parse " + ex.getMessage());
                                ex.printStackTrace();
                            }

                        } else {
                            Log.d(TAG, "Can not found question : " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }else {
                Log.d(TAG, "Notification not for answer: ");
                super.onPushReceive(context, intent);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }




    public void getListAnswerByQuestion(int skip, final ParseObject question) {
//        if( skip == Constants.PARSE_QUERY.NO_SKIP){
//            Storage.answerMap.get(question.getObjectId()).clear();
//        }else {
//            Log.d(TAG,"Current answer skip: " + Storage.answerSkip);
//        }
        ParseQuery<ParseObject> answerQuery = new ParseQuery<ParseObject>(Constants.TableName.ANSWER);
        answerQuery.setSkip(skip);
        answerQuery.setLimit(Constants.PARSE_QUERY.LIMIT);

        answerQuery.whereEqualTo(Constants.ANSWER_TABLE.QUESTION, question);

        if(Utils.checkNetwork(context)){
            Utils.showProgressDialogLoading(context);
            answerQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    Utils.stopProgressDialogLoading();
                    if (e == null) {
                        if (list.size() == 0) {
                            Utils.showMessage(context, context.getString(R.string.no_item_available));
                        } else {
                            Storage.currentAnswerList.clear();
                            addListObjectToLocalList(list, Storage.currentAnswerList);
                            Storage.answerSkip = Storage.currentAnswerList.size();
                            Log.d(TAG, "Answer skip: " + Storage.answerSkip);

//                            Storage.answerMap.put(question.getObjectId(), Storage.currentAnswerList);
                            Log.d(TAG, "done get answer : " + question.getObjectId() + " " +
                                    " answer size: " + Storage.currentAnswerList.size());
                            updateAnswerListView();
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private static  void addListObjectToLocalList(List<ParseObject> list, List<ParseObject> localList) {
        for ( ParseObject object : list){
            localList.add(object);
        }
        Log.d(TAG, "parse list: " + list.size() + " localList size: " + localList.size());
    }

    public void updateAnswerListView() {
        try {
//            LoadMoreListView answerListView  = (LoadMoreListView) findViewById(R.id.answer_list_view);
            ListView answerListView  = (ListView) ((Activity)context).findViewById(R.id.answer_list_view);
            if( answerListView != null && answerListView.getAdapter()!= null){
                ((ArrayAdapter)answerListView.getAdapter()).notifyDataSetChanged();
//                Log.d(TAG, "On update answer listview: size of answer list: "
//                        + Storage.answerMap.get(Storage.selectedQuestion.getObjectId()).size());
                answerListView.invalidateViews();
                answerListView.refreshDrawableState();
                Log.d(TAG, "Update answer list view here");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
            Log.d(TAG, "receive data: " + data);
        } catch (JSONException e) {
            Log.d(TAG, "can not get JSON object from call");
        }
        return data;
    }

    public interface ParseReceiverCallBack {
        void updateAnswer(String objectId);
    }
}
