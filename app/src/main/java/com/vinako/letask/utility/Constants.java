package com.vinako.letask.utility;

/**
 * Created by Khue on 29/7/2015.
 */
public class Constants {
    public class PARSE_QUERY{
        public static final int LIMIT = 10 ;
        public static final int NO_SKIP = 0;

    }
    public class TableName{
        public static final String QUESTION = "question";
        public static final String ANSWER = "answer";
    }

    public class QUESTION_TABLE{
        public static final String CONTENT = "content"; //string
        public static final String TIME = "created_time"; //number
    }

    public class ANSWER_TABLE{
        public static final String CONTENT = "content"; //string
        public static final String TIME = "time"; //number
        public static final String QUESTION = "question"; //pointer
    }
}
