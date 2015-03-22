package com.crittercism;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crittercism.app.Crittercism;

import org.json.JSONObject;

public class FragmentOther extends Fragment {
    WorkActivity act;
    View v;
    Boolean optOutStatus;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (WorkActivity) getActivity();
        v = inflater.inflate(R.layout.fragment_other, container, false);

        AddUserNames();
        AddMetaData();
        AddBreadcrumbs();
        AddOptOutStatus();

        return v;
    }

    private void AddUserNames(){
        ListView listView = (ListView) v.findViewById(R.id.UsernameListView);

        String[] transArray = {"Set Username: Bob", "Set Username: Jim",
                "Set Username: Sue"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, transArray);
        listView.setAdapter(arrayAdapter);
        act.setListViewHeightBasedOnChildren(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                String Name;
                switch (position) {
                    case 0:     Name ="Bob";   break;
                    case 1:     Name="Jim";   break;
                    default:     Name="Sue";
                }
                Crittercism.setUsername(Name);
                act.AddToLog("[Other]: Set Username: "+Name);
            }
        });
    }

    private void AddMetaData(){
        ListView listView = (ListView) v.findViewById(R.id.MetadataListView);

        String[] transArray = {"Set Game Level: 5", "Set Game Level: 30","Set Game Level: 50"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, transArray);
        listView.setAdapter(arrayAdapter);
        act.setListViewHeightBasedOnChildren(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                int n=0;
                switch (position) {
                    case 0:        n=5;         break;
                    case 1:        n=30;        break;
                    case 2:        n=50;
                }
                try {
                    Crittercism.setMetadata(new JSONObject("{\"Game Level\":\""+n+"\"}"));
                    act.AddToLog("[Other]: Set Game Level: "+n);
                } catch (Throwable t) {
                    System.out.println("Crittercism. Could not parse malformed JSON: '"+"{\"Game Level\":\""+n+"\"}"+"'");
                }
            }
        });
    }

    private void AddBreadcrumbs(){
        ListView listView = (ListView) v.findViewById(R.id.BreadcrumbsListView);

        String[] transArray = {"Leave: 'hello world'", "Leave: 'abc'", "Leave: '123'"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, transArray);
        listView.setAdapter(arrayAdapter);
        act.setListViewHeightBasedOnChildren(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                String Breadcrumb;
                switch (position) {
                    case 0:        Breadcrumb= "hello world";        break;
                    case 1:        Breadcrumb= "abc";        break;
                    default:        Breadcrumb= "123";
                }
                Crittercism.leaveBreadcrumb(Breadcrumb);
                act.AddToLog("[Other]: Leave: '"+Breadcrumb+"'");
            }
        });
    }

    private void AddOptOutStatus() {
        ListView listView = (ListView) v.findViewById(R.id.StatusListView);
        optOutStatus=false;
        ((TextView)v.findViewById(R.id.statusText)).setText("OPT-OUT STATUS:"+optOutStatus);
        String[] transArray = {"Opt Out", "Opt In"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, transArray);
        listView.setAdapter(arrayAdapter);
        act.setListViewHeightBasedOnChildren(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                if (position==0) optOutStatus =true;
                else optOutStatus=false;
                Crittercism.setOptOutStatus(optOutStatus);
                if (optOutStatus) act.AddToLog("[Other]: Opt Out");
                else act.AddToLog("[Other]: Opt In");
                ((TextView)v.findViewById(R.id.statusText)).setText("OPT-OUT STATUS: "+optOutStatus);
            }
        });
    }
}
