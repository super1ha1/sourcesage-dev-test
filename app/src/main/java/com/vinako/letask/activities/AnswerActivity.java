package com.vinako.letask.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.vinako.letask.R;
import com.vinako.letask.adapter.AnswerAdapter;
import com.vinako.letask.fragment.PostNewAnswerFragment;
import com.vinako.letask.utility.Constants;
import com.vinako.letask.utility.Storage;
import com.vinako.letask.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AnswerActivity extends Activity {

    public static final String TAG = AnswerActivity.class.getSimpleName();
    private View rootView;
    private AnswerAdapter answerAdapter;
//    @Bind(R.id.pullToRefresh)  SwipeRefreshLayout pullToRefresh;
    //    @Bind   (R.id.question_text_view)  TextView question;
    @Bind   (R.id.answer_list_view)  ListView answerListView;
    @Bind   (R.id.button_post_new_answer)  Button answerButton;

    @Override
    protected void onResume() {
        super.onResume();
//        updateAnswerListView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        ButterKnife.bind(this);

        getListAnswerByQuestion(Constants.PARSE_QUERY.NO_SKIP, Storage.selectedQuestion);

//        question.setText(Storage.selectedQuestion.getString(Constants.QUESTION_TABLE.CONTENT));
        answerAdapter = new AnswerAdapter(this, R.layout.answer_item, Storage.currentAnswerList);
        answerListView.setAdapter(answerAdapter);

        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPostNewAnswerFragment();
            }
        });



    }

    private void openPostNewAnswerFragment() {
        PostNewAnswerFragment fragment = new PostNewAnswerFragment();
        FragmentManager fm = this.getFragmentManager();
        fm.beginTransaction()
                .add(android.R.id.content, fragment, PostNewAnswerFragment.TAG)
                .addToBackStack(PostNewAnswerFragment.TAG)
                .commit();
        fm.executePendingTransactions();

        Log.d(TAG, "Open Post new Answer fragment here");


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

        if(Utils.checkNetwork(AnswerActivity.this)){
            Utils.showProgressDialogLoading(AnswerActivity.this);
            answerQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    Utils.stopProgressDialogLoading();
                    if (e == null) {
                        if (list.size() == 0) {
                            Utils.showMessage(AnswerActivity.this, getResources().getString(R.string.no_item_available));
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
            ListView answerListView  = (ListView) findViewById(R.id.answer_list_view);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
