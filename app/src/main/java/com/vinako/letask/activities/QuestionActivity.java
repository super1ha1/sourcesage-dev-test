package com.vinako.letask.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.vinako.letask.R;
import com.vinako.letask.adapter.AnswerAdapter;
import com.vinako.letask.adapter.QuestionAdapter;
import com.vinako.letask.fragment.AnswerFragment;
import com.vinako.letask.fragment.PostNewQuestionFragment;
import com.vinako.letask.utility.Constants;
import com.vinako.letask.utility.LoadMoreListView;
import com.vinako.letask.utility.Storage;
import com.vinako.letask.utility.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QuestionActivity extends Activity {

    private static final String TAG = QuestionActivity.class.getSimpleName();

    private QuestionAdapter questionAdapter;
    @Bind(R.id.pullToRefresh) SwipeRefreshLayout pullToRefresh;
    @Bind(R.id.empty) TextView emptyView;
    @Bind(R.id.question_list_view)
    LoadMoreListView questionListView;
    @Bind(R.id.button_post_new_question) Button  postNewQuestionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_room_main);
        ButterKnife.bind(this);
        initView();
        getListQuestion(Constants.PARSE_QUERY.NO_SKIP);
    }

    public void getListQuestion(int skip) {
        if( skip == Constants.PARSE_QUERY.NO_SKIP){
            Storage.questionList.clear();
        }else {
            Log.d(TAG,"Current question skip: " + Storage.questionSkip);
        }
        ParseQuery<ParseObject> questionQuery = new ParseQuery<ParseObject>(Constants.TableName.QUESTION);
        questionQuery.setSkip(skip);
        questionQuery.setLimit(Constants.PARSE_QUERY.LIMIT);

        if(Utils.checkNetwork(QuestionActivity.this)){
            Utils.showProgressDialogLoading(QuestionActivity.this);
            questionQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    Utils.stopProgressDialogLoading();
                    if( e == null){
                        if(list.size() == 0){
                            Utils.showMessage(QuestionActivity.this, getResources().getString(R.string.no_item_available));
                        }else {
                            addListObjectToLocalList(list, Storage.questionList);
                            Storage.questionSkip = Storage.questionList.size();
                            Log.d(TAG, "Question skip: " + Storage.questionSkip);

                            addAnswerToLocalList(Storage.questionList, Storage.answerMap);
                            updateQuestionListView();
                        }
                    }else {
                        e.printStackTrace();
                    }
                }
            });
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

        if(Utils.checkNetwork(QuestionActivity.this)){
            Utils.showProgressDialogLoading(QuestionActivity.this);
            answerQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    Utils.stopProgressDialogLoading();
                    if (e == null) {
                        if (list.size() == 0) {
                            Utils.showMessage(QuestionActivity.this, getResources().getString(R.string.no_item_available));
                        } else {
                            Log.d(TAG, "Current answer list size with questionId: " + question.getObjectId() + " size: "
                                    + Storage.answerMap.get(question.getObjectId()).size());
                            Storage.answerMap.get(question.getObjectId()).clear();
                            List<ParseObject> answerList = new ArrayList<ParseObject>();
                            addListObjectToLocalList(list, answerList);

                            Storage.answerSkip = answerList.size();
                            Log.d(TAG, "Answer skip: " + Storage.answerSkip);

                            Storage.answerMap.put(question.getObjectId(), answerList);
                            Log.d(TAG, "done get answer : " + question.getObjectId() + " " +
                                    " answer size: " + Storage.answerMap.get(question.getObjectId()).size() );
                            updateAnswerListView();
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public void updateAnswerListView() {
        try {
//            LoadMoreListView answerListView  = (LoadMoreListView) findViewById(R.id.answer_list_view);
            ListView answerListView  = (ListView) findViewById(R.id.answer_list_view);
            if( answerListView != null && answerListView.getAdapter()!= null){
                ((ArrayAdapter)answerListView.getAdapter()).notifyDataSetChanged();
                Log.d(TAG, "On update answer listview: size of answer list: "
                        + Storage.answerMap.get(Storage.selectedQuestion.getObjectId()).size());
                answerListView.invalidateViews();
                answerListView.refreshDrawableState();
                Log.d(TAG, "Update answer list view here");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAnswerToLocalList(List<ParseObject> questionList, Map<String, List<ParseObject>> localMap) {
        List<ParseObject> answerList;
        Log.d(TAG, " start init answer to local list here");
        for ( ParseObject object : questionList){
            answerList = new ArrayList<ParseObject>();
            localMap.put(object.getObjectId(), answerList);
            Log.d(TAG, "answer List  size for question: "  + object.getObjectId() + " size: " + answerList.size());
        }
        Log.d(TAG, "parse list: " + questionList.size() + " localList size: " + localMap.size());
    }

    private void addListObjectToLocalList(List<ParseObject> list, List<ParseObject> localList) {
        for ( ParseObject object : list){
            localList.add(object);
        }
        Log.d(TAG,"parse list: " + list.size() +" localList size: " +localList.size());
    }

    private void initView() {
        try {
            questionAdapter = new QuestionAdapter(QuestionActivity.this, R.layout.question_item, Storage.questionList);
            questionListView.setAdapter(questionAdapter);

            questionListView.setEmptyView(emptyView);
            questionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Storage.selectedQuestion = Storage.questionList.get(position);
                    Storage.currentAnswerList.clear();
//                    getListAnswerByQuestion(Constants.PARSE_QUERY.NO_SKIP, Storage.selectedQuestion);

//                    AnswerFragment fragment = new AnswerFragment();
//                    FragmentManager fragmentManager = getFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .add(android.R.id.content, fragment, AnswerFragment.TAG)
//                            .addToBackStack(AnswerFragment.TAG)
//                            .commit();
//                    fragmentManager.executePendingTransactions();
                    startActivity(new Intent(QuestionActivity.this, AnswerActivity.class));
                    Log.d(TAG, "art Answer Fragment here");



                }
            });

            questionListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {

                }
            });

            pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                }
            });

            postNewQuestionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PostNewQuestionFragment fragment = new PostNewQuestionFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(android.R.id.content, fragment, PostNewQuestionFragment.TAG)
                            .addToBackStack(PostNewQuestionFragment.TAG)
                            .commit();
                    fragmentManager.executePendingTransactions();
                    Log.d(TAG, "Open Post new question fragment here");

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateQuestionListView(){
        try {
            if( questionListView != null && questionListView.getAdapter()!= null){
                ((ArrayAdapter)((HeaderViewListAdapter)questionListView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
//                ((ArrayAdapter)questionListView.getAdapter()).notifyDataSetChanged();
                questionListView.invalidateViews();
                questionListView.refreshDrawableState();
                Log.d(TAG, "Update question list view here");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_question_room_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
