package com.crittercism;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CopyDialog extends DialogFragment implements AdapterView.OnItemClickListener{
    String[] copy_items = { "Mail", "Copy" };
    ListView copy_list;
    Context context;
    String LogString;

    public CopyDialog() {

    }

    public void setArguments(Context context_, String LogString_) {
        context = context_;
        LogString = LogString_;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.copy_dialog, container);
        copy_list = (ListView) view.findViewById(R.id.copy_list);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, copy_items);

        copy_list.setAdapter(adapter);
        copy_list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        dismiss();
        switch (position) {
            case 0://mail
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_TEXT, LogString);
                emailIntent.putExtra(Intent.EXTRA_CC, "");
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Email"));

               /* Intent intent = new Intent();
                intent.setClass(context, MailActivity.class);
                intent.putExtra("LogString", LogString);
                startActivity(intent);*/
                break;
            case 1://copy to clipboard
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", LogString);
                clipboard.setPrimaryClip(clip);
                //System.out.println("paste:  "+clipboard.getText().toString());
                Toast.makeText(getActivity(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
