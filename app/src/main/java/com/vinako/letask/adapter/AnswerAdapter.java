package com.vinako.letask.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.vinako.letask.R;
import com.vinako.letask.utility.Constants;
import com.vinako.letask.utility.Storage;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Khue on 29/7/2015.
 */
public class AnswerAdapter  extends ArrayAdapter<ParseObject> {
    private static final String TAG = AnswerAdapter.class.getSimpleName();
    private Context context;
    private List<ParseObject> answerList;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Start create getView for answer adapter here");
        try {
            ViewHolder holder;
            if( convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.answer_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            if( position >= answerList.size()){
                Log.d(TAG, "position larger than size: " + position + " size: " + answerList.size());
                return null;
            }

            ParseObject answer = answerList.get(position);
            holder.answerTextView.setText(answer.getString(Constants.ANSWER_TABLE.CONTENT));
            long postTime = answer.getLong(Constants.ANSWER_TABLE.TIME);
            Log.d(TAG, "Answer postTime: " + postTime);
            holder.answerTime.setText(DateUtils.getRelativeTimeSpanString(postTime * 1000).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public class ViewHolder{
        @Bind(R.id.answer_content_text_view)  TextView answerTextView;
        @Bind(R.id.answer_time) TextView answerTime;
        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    public AnswerAdapter(Context context, int resource, List<ParseObject> list) {
        super(context, resource, list);
        this.context = context;
        this.answerList = list;
        Log.d(TAG, "Answer list size: " + answerList);
    }
}
