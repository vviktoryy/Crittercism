package com.crittercism;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentLog extends Fragment {
    WorkActivity act;
    View v;
    TextView logText;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (WorkActivity) getActivity();
        v = inflater.inflate(R.layout.fragment_log, container, false);

        logText = (TextView) v.findViewById(R.id.LogText);

         v.findViewById(R.id.BasketButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                act.LogString ="";
                logText.setText(act.LogString);
                if (act.logFile!=null) act.logFile.Clear();
            }
        });

        v.findViewById(R.id.CopyButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = act.getSupportFragmentManager();
                CopyDialog copyDialog = new CopyDialog();
                copyDialog.setArguments(getActivity(), act.LogString);
                copyDialog.show(fm, "copy_dialog");
            }
        });

        ShowLog();

        return v;
    }

    private void ShowLog(){
        logText.setText(act.LogString);
    }
}
