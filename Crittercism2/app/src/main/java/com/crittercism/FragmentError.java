package com.crittercism;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crittercism.app.Crittercism;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentError extends Fragment {
    WorkActivity act;
    View v;
    ListView listViewStack;
    ArrayAdapter arrayFuncAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (WorkActivity) getActivity();
        v = inflater.inflate(R.layout.fragment_error, container, false);

        AddForceCrash();
        AddHandleException();
        AddCustomStackTrace();
        SetButtonsListeners();

        return v;
    }

    private void AddForceCrash() {
        ListView listView = (ListView) v.findViewById(R.id.ForceCrashListView);

        String[] transArray = {"Uncaught Exception", "Segfault", "Stack Overflow"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, transArray);
        listView.setAdapter(arrayAdapter);
        act.setListViewHeightBasedOnChildren(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://Uncaught Exception
                        System.out.println("Crittercism. Raising custom uncaught exception");
                        act.AddToLog("[Error]: Uncaught Exception");
                        throw new RuntimeException("This is a forced uncaught exception");
                    case 1://Segfault
                        System.out.println("Crittercism. Calling kill with SIGSEGV");
                        act.AddToLog("[Error]: Segfault");
                        throw new IllegalStateException();
                    case 2://Stack Overflow
                        System.out.println("Crittercism. Logging exception: stack overflow");
                        act.AddToLog("[Error]: Stack Overflow");
                        throw new StackOverflowError();
                }
            }
        });
    }

    private void AddHandleException(){
        ListView listView = (ListView) v.findViewById(R.id.HandleExceptionListView);

        String[] transArray = {"Index Out Of Bounds", "Input/Output"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, transArray);
        listView.setAdapter(arrayAdapter);
        act.setListViewHeightBasedOnChildren(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                switch (position) {
                    case 0://Index Out Of Bounds
                        System.out.println("Crittercism. Logging exception: out of bounds");
                        try {
                            throw new ArrayIndexOutOfBoundsException();
                        } catch (Exception exception) {
                            Crittercism.logHandledException(exception);
                            act.AddToLog("[Error]: Index Out OfBounds");
                            Toast.makeText(getActivity(), "Index Out OfBounds", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1://Input/Output
                        System.out.println("Crittercism. Logging exception: input/output");
                        try {
                            throw new IOException();
                        } catch (Exception exception) {
                            Crittercism.logHandledException(exception);
                            act.AddToLog("[Error]: Input/Output Exception");
                            Toast.makeText(getActivity(),"Input/Output Exception", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
    }

    private void AddCustomStackTrace(){
        listViewStack = (ListView) v.findViewById(R.id.CustomStackListView);

        arrayFuncAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, act.transArrayStack);
        listViewStack.setAdapter(arrayFuncAdapter);
        act.setListViewHeightBasedOnChildren(listViewStack);
        listViewStack.post(new Runnable()        {
            public void run()            {
                for(int i=0; i<=listViewStack.getAdapter().getCount()-2;i++){
                    ((TextView)listViewStack.getChildAt(i)).setTextColor(Color.BLACK);
                }
                ((TextView)listViewStack.getChildAt(listViewStack.getAdapter().getCount()-1)).setTextColor(Color.GRAY);
            }
        });

        listViewStack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                switch (((TextView)view).getText().toString()){
                    case "Add Function...":

                    case "Add Another Function...":
                        AddAnotherFunction();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void AddAnotherFunction(){
        FragmentManager fm = act.getSupportFragmentManager();
        FunctionDialog functionDialog = new FunctionDialog();
        functionDialog.setArguments(this, listViewStack);
        functionDialog.show(fm, "function_dialog");
    }

    private void SetButtonsListeners(){
        Button clearButton = (Button) v.findViewById(R.id.clearButton);
        buttonEffect(clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                act.transArrayStack.clear();
                act.transArrayStack.add("Add Function...");
                ((TextView) listViewStack.getChildAt(listViewStack.getAdapter().getCount() - 1)).setTextColor(Color.GRAY);
                arrayFuncAdapter.notifyDataSetChanged();
                act.setListViewHeightBasedOnChildren(listViewStack);
                act.AddToLog("[Error]: Clear");
            }
        });
        Button exceptionButton = (Button) v.findViewById(R.id.exceptionButton);
        buttonEffect(exceptionButton);
        exceptionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CrashClick(false);
            }
        });
        Button crashButton = (Button) v.findViewById(R.id.crashButton);
        buttonEffect(crashButton);
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CrashClick(true);
            }
        });
    }

    private void CrashClick(Boolean IsCrash){
        for (int i = 0; i < act.transArrayStack.size(); i++) {
            switch (act.transArrayStack.get(i)) {
                case "Function A":
                    funcA();
                    break;
                case "Function B":
                    funcB();
                    break;
                case "Function C":
                    funcC();
                    break;
                case "Function D":
                    funcD();
                    break;
                default:
                    if (IsCrash){
                        System.out.println("Crittercism. Raising forced uncaught exception");
                        act.AddToLog("[Error]: Crash");
                        throw new RuntimeException("This is a forced uncaught exception");
                    }else {
                        System.out.println("Crittercism. Raising forced caught exception");
                        try {
                            throw new Exception("This is a forced caught exception");
                        } catch (Exception exception) {
                            Crittercism.logHandledException(exception);
                            act.AddToLog("[Error]: Exception");
                        }
                    }
            }
        }
    }

    private void funcA(){
        act.AddToLog("[Error]: Function A called...");
    }

    private void funcB(){ act.AddToLog("[Error]: Function B called...");  }

    private void funcC(){
        act.AddToLog("[Error]: Function C called...");
    }

    private void funcD(){
        act.AddToLog("[Error]: Function D called...");
    }

    /*for click effect on transparent buttons putting*/
    public static void buttonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ((TextView)v).setTextColor(Color.GRAY);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        ((TextView)v).setTextColor(Color.BLUE);
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }
}

