package com.vinako.letask.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.vinako.letask.activities.QuestionActivity;
import com.vinako.letask.R;
import com.vinako.letask.utility.Constants;
import com.vinako.letask.utility.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PostNewQuestionFragment extends Fragment {

    public static final String TAG = PostNewQuestionFragment.class.getSimpleName();

    @Bind(R.id.post_content)  EditText postContent;
    @Bind(R.id.button_post)  Button postButton;
    private View rootView;
    public static PostNewQuestionFragment newInstance(String param1, String param2) {
        PostNewQuestionFragment fragment = new PostNewQuestionFragment();
        return fragment;
    }

    public PostNewQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            rootView =  inflater.inflate(R.layout.fragment_post_new_question, container, false);
            ButterKnife.bind(this, rootView);

            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String postString = postContent.getText().toString();
                    if (TextUtils.isEmpty(postString)) {
                        Utils.showMessage(getActivity(), "Please enter the required field!");
                    } else {
                        ParseObject question = new ParseObject(Constants.TableName.QUESTION);
                        question.put(Constants.QUESTION_TABLE.CONTENT, postString);
                        long currentTime = System.currentTimeMillis() / 1000L;
                        Log.d(TAG, "Current time post: " + currentTime);
                        question.put(Constants.QUESTION_TABLE.TIME, currentTime);

                        Utils.showProgressDialogLoading(getActivity());
                        question.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Utils.stopProgressDialogLoading();
                                if (e == null) {
                                    Utils.showMessage(getActivity(), "Post new question successfully!");
                                    ((QuestionActivity)getActivity()).getListQuestion(Constants.PARSE_QUERY.NO_SKIP);
                                    postContent.setText("");

                                } else {
                                    Log.d(TAG, "Error when post new question: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

}
