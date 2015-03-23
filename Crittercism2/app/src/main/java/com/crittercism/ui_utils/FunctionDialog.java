package com.crittercism.ui_utils;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crittercism.fragments.FragmentError;
import com.crittercism.R;
import com.crittercism.WorkActivity;

public class FunctionDialog extends DialogFragment implements AdapterView.OnItemClickListener {
    String[] function_items = { "Function A", "Function B", "Function C", "Function D" };
    ListView function_list;
    ListView error_function_list;
    FragmentError fe;

    public void setArguments( FragmentError fe_, ListView error_function_list_) {
        fe = fe_;
        error_function_list = error_function_list_;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.function_dialog, container);
        function_list = (ListView) view.findViewById(R.id.function_list);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, function_items);

        function_list.setAdapter(adapter);
        function_list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        fe.act.transArrayStack.remove(fe.act.transArrayStack.size()-1);
        fe.act.transArrayStack.add(function_items[position]);
        fe.act.transArrayStack.add("Add Another Function...");

        error_function_list.post(new Runnable()        {
            public void run()            {
                ((TextView)error_function_list.getChildAt(error_function_list.getAdapter().getCount()-2)).setTextColor(Color.BLACK);
                ((TextView)error_function_list.getChildAt(error_function_list.getAdapter().getCount()-1)).setTextColor(Color.GRAY);
            }
        });

        fe.arrayFuncAdapter.notifyDataSetChanged();

        WorkActivity.setListViewHeightBasedOnChildren(error_function_list);
        if (fe.act.transArrayStack.size()>2) fe.act.AddToLog("[Error]: Add Another Function...");
        else fe.act.AddToLog("[Error]: Add Function...");
    }
}
