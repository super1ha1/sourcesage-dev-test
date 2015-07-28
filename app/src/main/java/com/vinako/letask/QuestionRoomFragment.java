package com.vinako.letask;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class QuestionRoomFragment extends Fragment {

    public static QuestionRoomFragment newInstance(String param1, String param2) {
        QuestionRoomFragment fragment = new QuestionRoomFragment();

        return fragment;
    }

    public QuestionRoomFragment() {
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
        return inflater.inflate(R.layout.fragment_question_room, container, false);
    }

}
