package com.vinako.letask;

import android.app.Activity;
import android.app.FragmentManager;
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
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.vinako.letask.utility.Constants;
import com.vinako.letask.utility.Storage;
import com.vinako.letask.utility.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QuestionRoomMainActivity extends Activity {

    private static final String TAG = QuestionRoomMainActivity.class.getSimpleName();

    private QuestionAdapter questionAdapter;
    @Bind(R.id.pullToRefresh) SwipeRefreshLayout pullToRefresh;
    @Bind(R.id.empty) TextView emptyView;
    @Bind(R.id.question_list_view) LoadMoreListView questionListView;
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

        if(Utils.checkNetwork(QuestionRoomMainActivity.this)){
            Utils.showProgressDialogLoading(QuestionRoomMainActivity.this);
            questionQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    Utils.stopProgressDialogLoading();
                    if( e == null){
                        if(list.size() == 0){
                            Utils.showMessage(QuestionRoomMainActivity.this, getResources().getString(R.string.no_item_available));
                        }else {
                            addQuestionToLocalList(list, Storage.questionList);
                            Storage.questionSkip = Storage.questionList.size();
                            Log.d(TAG, "Question skip: " + Storage.questionSkip);
                            updateQuestionListView();
                        }
                    }else {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private void addQuestionToLocalList(List<ParseObject> list, List<ParseObject> localList) {
        for ( ParseObject object : list){
            localList.add(object);
        }
        Log.d(TAG,"parse list: " + list.size() +" localList size: " +localList.size());
    }

    private void initView() {
        try {
            questionAdapter = new QuestionAdapter(QuestionRoomMainActivity.this, R.layout.question_item, Storage.questionList);
            questionListView.setAdapter(questionAdapter);

            questionListView.setEmptyView(emptyView);
            questionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
                    PostFragment fragment = new PostFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(android.R.id.content, fragment, PostFragment.TAG)
                            .addToBackStack(PostFragment.TAG)
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
