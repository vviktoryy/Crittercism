package com.crittercism;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crittercism.app.Crittercism;
import com.crittercism.fragments.FragmentLog;
import com.crittercism.ui_utils.TabPagerAdapter;
import com.crittercism.utils.LogFile;

import java.util.ArrayList;
import java.util.List;

public class WorkActivity extends FragmentActivity {
    private ImageButton imageButtonError;
    private ImageButton imageButtonConsole;
    private ImageButton imageButtonOther;
    private ImageButton imageButtonNetWork;
    private ImageButton imageButtonTransaction;

    private TextView textViewError;
    private TextView textViewConsole;
    private TextView textViewOther;
    private TextView textViewNetWork;
    private TextView textViewTransaction;

    public LogFile logFile;
    public String LogString="";
    public FragmentLog log;
    public List<String> transArrayStack;
    public String ResponsesString="";
    public int connType;
    public String protocol = "http";

    ViewPager Tab;
    TabPagerAdapter TabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crittercism.initialize(getApplicationContext(), "54e5d8ac51de5e9f042edbd1");
        setContentView(R.layout.activity_work);
        setupViews();
    }

    private void setupViews() {
        TabAdapter = new TabPagerAdapter(getSupportFragmentManager());
        Tab = (ViewPager)findViewById(R.id.pager);
        Tab.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        setMenuSelect(position);
                    }
                });
        Tab.setAdapter(TabAdapter);

        transArrayStack = new ArrayList<>();
        transArrayStack.add("Add Function...");

        logFile = new LogFile(this,"LogFile.txt");
        if (logFile.file.exists()) {LogString = logFile.Read();}
        else {System.out.println("NOLogFile!");}

        imageButtonError = (ImageButton) findViewById(R.id.imageButtonError);
        imageButtonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab.setCurrentItem(0);
            }
        });
        textViewError = (TextView) findViewById(R.id.txtError);

        imageButtonNetWork = (ImageButton) findViewById(R.id.imageButtonNetWork);
        imageButtonNetWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab.setCurrentItem(1);
            }
        });
        textViewNetWork = (TextView) findViewById(R.id.txtNetWork);

        imageButtonTransaction = (ImageButton) findViewById(R.id.imageButtonTransactions);
        imageButtonTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab.setCurrentItem(2);
            }
        });
        textViewTransaction = (TextView) findViewById(R.id.txtTransaction);

        imageButtonOther = (ImageButton) findViewById(R.id.imageButtonOther);
        imageButtonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab.setCurrentItem(3);
            }
        });
        textViewOther = (TextView) findViewById(R.id.txtOther);

        imageButtonConsole = (ImageButton) findViewById(R.id.imageButtonConsole);
        imageButtonConsole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab.setCurrentItem(4);
            }
        });
        textViewConsole = (TextView) findViewById(R.id.txtConsole);
        setMenuSelect(0);

    }

    private void setMenuSelect(int id) {

        textViewError.setTextColor(Color.GRAY);
        textViewConsole.setTextColor(Color.GRAY);
        textViewOther.setTextColor(Color.GRAY);
        textViewNetWork.setTextColor(Color.GRAY);
        textViewTransaction.setTextColor(Color.GRAY);

        imageButtonError.setImageResource(R.drawable.bug);
        imageButtonConsole.setImageResource(R.drawable.glasses);
        imageButtonOther.setImageResource(R.drawable.controller);
        imageButtonNetWork.setImageResource(R.drawable.network);
        imageButtonTransaction.setImageResource(R.drawable.shopping_cart);

        switch (id) {
            case 0:
                textViewError.setTextColor(0xff007aff);
                imageButtonError.setImageResource(R.drawable.bug_blue);
                break;
            case 1:
                textViewNetWork.setTextColor(0xff007aff);
                imageButtonNetWork.setImageResource(R.drawable.network_blue);
                break;
            case 2:
                textViewTransaction.setTextColor(0xff007aff);
                imageButtonTransaction.setImageResource(R.drawable.shopping_cart_blue);
                break;
            case 3:
                textViewOther.setTextColor(0xff007aff);
                imageButtonOther.setImageResource(R.drawable.controller_blue);
                break;
            case 4:
                textViewConsole.setTextColor(0xff007aff);
                imageButtonConsole.setImageResource(R.drawable.glasses_blue);
                break;
        }
    }

    public void AddToLog(String addString){
        if (LogString.length()>0){  LogString+="\n"+addString;
        } else {            LogString=addString;        }
        if (logFile!=null) logFile.Write(LogString);
        if (log.logText!=null) log.logText.setText(LogString);
    }

    public void MsgBox(String title, String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title).setMessage(message).show();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setGridViewHeightBasedOnChildren(GridView gridView, int colCount) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(gridView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, gridView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight/colCount;
        gridView.setLayoutParams(params);
        gridView.requestLayout();
    }
}

