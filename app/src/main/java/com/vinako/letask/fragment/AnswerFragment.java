package com.vinako.letask.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vinako.letask.activities.QuestionActivity;
import com.vinako.letask.adapter.AnswerAdapter;
import com.vinako.letask.utility.Constants;
import com.vinako.letask.utility.LoadMoreListView;
import com.vinako.letask.R;
import com.vinako.letask.utility.Storage;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AnswerFragment extends Fragment {

    public static final String TAG = AnswerFragment.class.getSimpleName();
    private View rootView;
    private AnswerAdapter answerAdapter;
    @Bind   (R.id.pullToRefresh)  SwipeRefreshLayout pullToRefresh;
//    @Bind   (R.id.question_text_view)  TextView question;
    @Bind   (R.id.answer_list_view) ListView answerListView;
    @Bind   (R.id.button_post_new_answer)  Button answerButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView =  inflater.inflate(R.layout.fragment_answer, container, false);
        ButterKnife.bind(this, rootView);
//        question.setText(Storage.selectedQuestion.getString(Constants.QUESTION_TABLE.CONTENT));
        answerAdapter = new AnswerAdapter(getActivity(), R.layout.answer_item, Storage.answerMap.get(Storage.selectedQuestion.getObjectId()));
        answerListView.setAdapter(answerAdapter);

        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPostNewAnswerFragment();
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        return rootView;
    }

    private void openPostNewAnswerFragment() {
        PostNewAnswerFragment fragment = new PostNewAnswerFragment();
        FragmentManager fm = getActivity().getFragmentManager();
        fm.beginTransaction()
                .add(android.R.id.content, fragment, PostNewAnswerFragment.TAG)
                .addToBackStack(PostNewAnswerFragment.TAG)
                .commit();
        fm.executePendingTransactions();

        Log.d(TAG, "Open Post new Answer fragment here");


    }

    public AnswerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((QuestionActivity)getActivity()).getListAnswerByQuestion(Constants.PARSE_QUERY.NO_SKIP, Storage.selectedQuestion);
    }


    @Override
    public void onResume() {
        super.onResume();
        ((QuestionActivity)getActivity()).updateAnswerListView();
    }
}
