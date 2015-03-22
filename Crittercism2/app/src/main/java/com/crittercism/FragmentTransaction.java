package com.crittercism;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.crittercism.app.Crittercism;

public class FragmentTransaction extends Fragment {
    WorkActivity act;
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (WorkActivity) getActivity();
        v = inflater.inflate(R.layout.fragment_transaction, container, false);

        AddTransactionListView(R.id.LoginTransListView,"Login");
        AddTransactionListView(R.id.BrowseTransListView,"Browse");
        AddTransactionListView(R.id.ReserveTransListView,"Reserve");
        AddTransactionListView(R.id.ConfirmTransListView,"Confirm");

        return v;
    }

    private void AddTransactionListView(Integer ID,String TransactionName){
        ListView listView = (ListView) v.findViewById(ID);

        String[] transArray = { "Tx \""+TransactionName+"\": Begin",
                "Tx \""+TransactionName+"\": End",
                "Tx \""+TransactionName+"\": Fail",
                "Tx \""+TransactionName+"\": Add 1",
                "Tx \""+TransactionName+"\": Get Value" };

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, transArray);
        listView.setAdapter(arrayAdapter);
        act.setListViewHeightBasedOnChildren(listView);

        listView.setContentDescription(TransactionName);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                switch (position) {
                    case 0://Begin
                        Crittercism.beginTransaction(parent.getContentDescription()+"");
                        act.AddToLog("[Transactions]: Begin " + parent.getContentDescription());
                        break;
                    case 1://End
                        Crittercism.endTransaction(parent.getContentDescription()+"");
                        act.AddToLog("[Transactions]: End " + parent.getContentDescription());
                        break;
                    case 2://Fail
                        Crittercism.failTransaction(parent.getContentDescription()+"");
                        act.AddToLog("[Transactions]: Fail " + parent.getContentDescription());
                        break;
                    case 3://Add 1
                        int i = Crittercism.getTransactionValue(parent.getContentDescription()+"");
                        if(i<0) {Crittercism.setTransactionValue(parent.getContentDescription()+"", 1);}
                        else Crittercism.setTransactionValue(parent.getContentDescription()+"", i+1);
                        act.AddToLog("[Transactions]: Add " + parent.getContentDescription());
                        break;
                    case 4://GetValue
                        int j = Crittercism.getTransactionValue(parent.getContentDescription()+"");
                        if (j==-1) j=0;
                        act.MsgBox(parent.getContentDescription()+"","Transaction value="+j);
                        act.AddToLog("[Transactions]: Get " + parent.getContentDescription());
                }
            }
        });
    }
}
