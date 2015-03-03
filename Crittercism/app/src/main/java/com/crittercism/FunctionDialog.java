package com.crittercism;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FunctionDialog extends DialogFragment implements AdapterView.OnItemClickListener {
    String[] function_items = { "Function A", "Function B", "Function C", "Function D" };
    ListView function_list;
    ListView error_function_list;
    Context context;
    String LogString;
    String Operation;

    public void setArguments(Context context_, String LogString_, ListView error_function_list_, String Operation_) {
        context = context_;
        LogString = LogString_;
        error_function_list = error_function_list_;
        Operation = Operation_;
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
        //function_items[position]
       // error_function_list.removeView((View)error_function_list.getSelectedItem());
    }
}
