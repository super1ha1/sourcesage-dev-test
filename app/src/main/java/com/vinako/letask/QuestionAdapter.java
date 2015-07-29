package com.vinako.letask;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.vinako.letask.utility.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Khue on 29/7/2015.
 */
public class QuestionAdapter extends ArrayAdapter<ParseObject> {
    private static final String TAG = QuestionAdapter.class.getSimpleName();
    private Context context;
    private List<ParseObject> questionList;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Start create getView here");
        try {
            ViewHolder holder;
            if( convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.question_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            if( position >= questionList.size()){
                Log.d(TAG, "position larger than size: " + position + " size: " + questionList.size());
                return null;
            }

            ParseObject question = questionList.get(position);
            holder.questionContent.setText(question.getString(Constants.QUESTION_TABLE.CONTENT));
            long postTime = question.getLong(Constants.QUESTION_TABLE.TIME);
//            postTime = postTime * 1000;
            Log.d(TAG, "postTime: " + postTime);
            holder.questionTime.setText(DateUtils.getRelativeTimeSpanString(postTime).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public class ViewHolder{
        @Bind(R.id.question_content_text_view) TextView questionContent;
        @Bind(R.id.question_time) TextView questionTime;
        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    public QuestionAdapter(Context context, int resource, List<ParseObject> list) {
        super(context, resource, list);
        this.context = context;
        this.questionList = list;
        Log.d(TAG, "Question list size: " + questionList);
    }
}
