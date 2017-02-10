package com.example.lj.redwine.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lj.redwine.R;
import com.example.lj.redwine.adapter.EvaluationBaseAdapter;
import com.example.lj.redwine.adapter.FavoritesBaseAdapter;
import com.example.lj.redwine.constant.ConstantClass;
import com.example.lj.redwine.javabean.Evaluation;
import com.example.lj.redwine.javabean.Favorites;
import com.example.lj.redwine.util.ToastUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RedwineCommentFragment extends Fragment {
    TextView none_evaluation;
    List<Evaluation> evaluationList;//评价列表
    ListView evaluation_list_view;//评价数据源
    ConstantClass constantClass;
    RequestQueue requestQueue;//请求队列
    EvaluationBaseAdapter evaluationBaseAdapter;//评价适配器
    int redwine_id;//红酒id

    public RedwineCommentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_redwine_comment, container, false);
        redwine_id = this.getArguments().getInt("redwine_id");
        requestData();
        InitView(view);//初始化布局
        return view;
    }

    private void InitView(View view) {
        evaluation_list_view = (ListView) view.findViewById(R.id.evaluation_list_view);
        none_evaluation = (TextView) view.findViewById(R.id.none_evaluation);
    }

    private void requestData() {
        requestQueue = Volley.newRequestQueue(getContext());
        String url = constantClass.getHttp_prefix()+"/evaluation/listEvaluationByRedwineId?id="+redwine_id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("[]")) {
                    none_evaluation.setVisibility(View.VISIBLE);
                } else {
                    evaluationList = JSON.parseArray(s, Evaluation.class);
                    evaluationBaseAdapter = new EvaluationBaseAdapter(getContext(), evaluationList);
                    evaluation_list_view.setAdapter(evaluationBaseAdapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.show(getContext(), "网络出了点问题");
            }
        });
        requestQueue.add(stringRequest);
    }

}
