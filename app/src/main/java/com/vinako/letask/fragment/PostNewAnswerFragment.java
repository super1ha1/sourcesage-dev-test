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

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.vinako.letask.R;
import com.vinako.letask.activities.AnswerActivity;
import com.vinako.letask.utility.Constants;
import com.vinako.letask.utility.Storage;
import com.vinako.letask.utility.Utils;

import java.util.HashMap;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PostNewAnswerFragment extends Fragment {


    public static final String TAG = PostNewQuestionFragment.class.getSimpleName();

    @Bind(R.id.post_content)  EditText postContent;
    @Bind(R.id.button_post)  Button postButton;
    private View rootView;

    public static PostNewAnswerFragment newInstance() {
        PostNewAnswerFragment fragment = new PostNewAnswerFragment();
        return fragment;
    }

    public PostNewAnswerFragment() {
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
            rootView =  inflater.inflate(R.layout.fragment_post_new_answer, container, false);
            ButterKnife.bind(this, rootView);

            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String postString = postContent.getText().toString();
                    if (TextUtils.isEmpty(postString)) {
                        Utils.showMessage(getActivity(), "Please enter the required field!");
                    } else {
                        ParseObject answer = new ParseObject(Constants.TableName.ANSWER);
                        answer.put(Constants.ANSWER_TABLE.CONTENT, postString);

                        long currentTime = System.currentTimeMillis() / 1000L;
                        Log.d(TAG, "Current time post: " + currentTime);
                        answer.put(Constants.ANSWER_TABLE.TIME, currentTime);

                        answer.put(Constants.ANSWER_TABLE.QUESTION, Storage.selectedQuestion);

                        Utils.showProgressDialogLoading(getActivity());
                        answer.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Utils.stopProgressDialogLoading();
                                if (e == null) {
                                    Utils.showMessage(getActivity(), "Post new answer successfully!");
                                    ((AnswerActivity) getActivity()).getListAnswerByQuestion(Constants.PARSE_QUERY.NO_SKIP, Storage.selectedQuestion);

                                    postContent.setText("");
                                    sendPushtNoti(Storage.selectedQuestion.getObjectId());

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

    private void sendPushtNoti(String objectId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("questionId", objectId);

        ParseCloud.callFunctionInBackground("updateAnswerList", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                if( e == null){
                    Log.d(TAG, "send push to all successffuly: " + o.toString());
                }else {
                    Log.d(TAG, "Can not send push to update answer: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }


}
