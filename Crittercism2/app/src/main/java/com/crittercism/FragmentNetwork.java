package com.crittercism;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.crittercism.app.Crittercism;
import java.net.URL;


public class FragmentNetwork extends Fragment implements ScrollViewListener{
    WorkActivity act;
    View v;
    TextView responsesTextView;
    private String[] transArrayPunch = {"Get 100b", "Get 5Kb", "Get 7Mb",
            "Post 100b", "Post 4Kb", "Post 3Mb",
            "Latency 1s", "Latency 3s", "Latency 10s",
            "Do 202", "Do 404", "Do 500"};
    FragmentNetwork context;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (WorkActivity) getActivity();
        v = inflater.inflate(R.layout.fragment_network, container, false);
        context = this;
        responsesTextView = (TextView)v.findViewById(R.id.responsesTextView);
        responsesTextView.setText(act.ResponsesString);
        AddConnType();
        AddProtocols();
        AddPunch();
        AddWebView();

        return v;
    }

    @Override
    public void onScrollChanged(final ScrollViewExt scrollView, final int x, final int y, int oldx, int oldy) {
        final View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

        // if diff is zero, then the bottom has been reached
        if (diff == 0) {
            if (responsesTextView.getVisibility() == View.VISIBLE){
                final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) responsesTextView.getLayoutParams();
                if (params.height < 10) {
                    Animation a = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            params.height = (int) (200 * interpolatedTime);
                            responsesTextView.setLayoutParams(params);
                            scrollView.scrollTo(x, 10000);
                        }
                    };
                    a.setDuration(300); // in ms
                    responsesTextView.startAnimation(a);

                }
            }
        }else{
            if (responsesTextView.getVisibility() == View.VISIBLE){
                final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) responsesTextView.getLayoutParams();
                if (params.height > 190) {
                    Animation a = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            params.height = (int) (200 - 200 * interpolatedTime);
                            responsesTextView.setLayoutParams(params);
                        }
                    };
                    a.setDuration(300); // in ms
                    responsesTextView.startAnimation(a);
                }
            }
        }
    }

    private void AddConnType(){
        ListView listView = (ListView) v.findViewById(R.id.ConnTypeListView);
        String[] transArray = {"[HttpURLConnection]","[org.apache.http.client.HttpClient]","[OkHttpClient]"};
        ArrayAdapter<String> stringAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_checked, transArray);
        listView.setAdapter(stringAdapter);

        act.setListViewHeightBasedOnChildren(listView);

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setItemChecked(act.connType,true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((CheckedTextView)view).setChecked(true);
                act.connType = position;
            }
        });
    }

    private void AddProtocols(){
        ListView listView = (ListView) v.findViewById(R.id.ProtocolListView);
        String[] transArray = {"HTTP", "HTTPS"};
        ArrayAdapter<String> stringAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_checked, transArray);
        listView.setAdapter(stringAdapter);

        act.setListViewHeightBasedOnChildren(listView);

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if (act.protocol.equals("http")) listView.setItemChecked(0,true);
        else listView.setItemChecked(1, true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((CheckedTextView)view).setChecked(true);
                switch (position) {
                    case 0://HTTP
                        act.protocol = "http";              break;
                    case 1://HTTPS
                        act.protocol = "https";             break;
                }
            }
        });
    }

    private void AddPunch(){
        GridView gridView = (GridView) v.findViewById(R.id.PunchListView);

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.linear_list_item, transArrayPunch);
        gridView.setAdapter(arrayAdapter);

        act.setGridViewHeightBasedOnChildren(gridView, 3);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                act.AddToLog("[Network]: " + transArrayPunch[position]);
                new HttpConnection(transArrayPunch[position], act.protocol, act.connType,
                        responsesTextView, act, context).execute();
            }
        });
    }

    private void AddWebView(){
        ListView listView = (ListView) v.findViewById(R.id.WebViewListView);
        String[] transArray = {"WebView"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, transArray);
        listView.setAdapter(arrayAdapter);

        act.setListViewHeightBasedOnChildren(listView);

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if (act.protocol.equals("http")) listView.setItemChecked(0,true);
        else listView.setItemChecked(1, true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StartWebView();
            }
        });
    }

    private void StartWebView(){
        Intent intent = new Intent();
        intent.setClass(getActivity(), WebViewActivity.class);
        startActivity(intent);
    }

    public void WriteCrittercismNetLog(String method, URL url_, long time, long bytesRead,
                                        long bytesSent, int status, Exception ex){
        Crittercism.logNetworkRequest(method, url_, time, bytesRead, bytesSent, status, ex);
    }

    public void SetResponsesText(String textString){
        if ( act.ResponsesString.length()>0){
            act.ResponsesString= act.ResponsesString+"\n"+textString;
            responsesTextView.setText( act.ResponsesString);
        }else{
            act.ResponsesString = textString;
            responsesTextView.setText( act.ResponsesString);
        }
    }
}
