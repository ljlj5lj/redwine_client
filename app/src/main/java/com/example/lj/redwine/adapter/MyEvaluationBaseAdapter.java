package com.example.lj.redwine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.lj.redwine.R;
import com.example.lj.redwine.javabean.Evaluation;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/12/7 0007.
 */
public class MyEvaluationBaseAdapter extends BaseAdapter{
    private List<Evaluation> evaluationList;
    private Context context;

    public MyEvaluationBaseAdapter(Context context, List<Evaluation> evaluationList) {
        this.context = context;
        this.evaluationList = evaluationList;
    }

    @Override
    public int getCount() {
        return (evaluationList == null) ? 0 : evaluationList.size();
    }

    @Override
    public Object getItem(int position) {
        return evaluationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.my_evaluation_list_item, null);
            viewHolder = new ViewHolder();
            initView(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.evaluation_redwine.setText(evaluationList.get(position).getRedwine().getRedwine_name());
        viewHolder.evaluation_content.setText(evaluationList.get(position).getContent());
        viewHolder.evaluation_grade.setRating(evaluationList.get(position).getGrade());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        viewHolder.evaluation_date.setText(simpleDateFormat.format(evaluationList.get(position).getEvaluation_date()));
        return convertView;
    }

    private void initView(ViewHolder viewHolder, View convertView) {
        viewHolder.evaluation_redwine = (TextView) convertView.findViewById(R.id.evaluation_redwine);
        viewHolder.evaluation_content = (TextView) convertView.findViewById(R.id.evaluation_content);
        viewHolder.evaluation_date = (TextView) convertView.findViewById(R.id.evaluation_date);
        viewHolder.evaluation_grade = (RatingBar) convertView.findViewById(R.id.evaluation_grade);
    }

    static class ViewHolder {
        TextView evaluation_redwine;
        TextView evaluation_date;
        RatingBar evaluation_grade;
        TextView evaluation_content;
    }
}
