package com.vinako.letask.utility;

import android.app.ProgressDialog;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Khue on 29/7/2015.
 */
public class Storage {
    public static List<ParseObject> questionList = new ArrayList<ParseObject>();
    public static Map<String, List<ParseObject>> answerMap = new HashMap<String, List<ParseObject>>();
    public static int questionSkip = 0 ;
    public static int answerSkip = 0 ;

    public static ProgressDialog dialog;
    public static ParseObject selectedQuestion;
    public static List<ParseObject> currentAnswerList = new ArrayList<ParseObject>();


}
