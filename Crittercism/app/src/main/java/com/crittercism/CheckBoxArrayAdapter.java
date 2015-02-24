package com.crittercism;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import java.util.List;

class CheckBoxArrayAdapter extends ArrayAdapter<String> implements CompoundButton.OnCheckedChangeListener {
    private CheckedTextView checkedTextBox;

    public CheckBoxArrayAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CheckedTextView view = (CheckedTextView) super.getView(position, convertView, parent);
        if (position == 0) {view.setChecked(true);   checkedTextBox = view;}

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedTextBox.setChecked(false);
                ((CheckedTextView)v).setChecked(true);
                checkedTextBox = ((CheckedTextView)v);
            }
        });
        return view;
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }
}