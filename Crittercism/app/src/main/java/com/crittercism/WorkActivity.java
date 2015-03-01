package com.crittercism;

import android.content.Context;
import android.content.Entity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.app.AlertDialog;
import com.crittercism.app.Crittercism;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;

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

    private ImageButton basket_button;
    private ImageButton copy_button;
    private TextView textTitleView;
    private RelativeLayout relativeLayout;
    private ArrayAdapter arrayAdapter;
    private CheckBoxArrayAdapter checkBoxArrayAdapter;
    private ArrayAdapter<String> stringAdapter;

    private LinearLayout workLayout;
    private Context context;

    private String[] transArrayPunch = {"Get 100b", "Get 5Kb", "Get 7Mb",
            "Post 100b", "Post 4Kb", "Post 3Mb",
            "Latency 1s", "Latency 3s", "Latency 10s",
            "Do 202", "Do 404", "Do 500"};
    private String[] transArrayProtocol = {"HTTP", "HTTPS"};
    private String[] transArrayConnection = {"[HttpURLConnection]","[org.apache.http.client.HttpClient]"};

    private String url;
    private String[] action_modifier;
    private String protocol="http";
    private int connType = 0;
    private int bytes = 1;
    private int latency =0;
    private int code = 200;
    private int status;

    private String LogString="";
    private TextView logTextView;
    private TextView responsesTextView;

    private LogFile logFile;

    private String componentsString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Crittercism.initialize(getApplicationContext(), "54e5d8ac51de5e9f042edbd1");
        setContentView(R.layout.activity_work)  ;
        setupViews();
        setContent(1);
    }

    private void setupViews() {
        workLayout = (LinearLayout) findViewById(R.id.workLayer);
        textTitleView = (TextView) findViewById(R.id.TextTitleView);
        relativeLayout=(RelativeLayout) findViewById(R.id.relativeLayout);

        imageButtonError = (ImageButton) findViewById(R.id.imageButtonError);
        imageButtonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContent(1);
            }
        });
        textViewError = (TextView) findViewById(R.id.txtError);

        imageButtonNetWork = (ImageButton) findViewById(R.id.imageButtonNetWork);
        imageButtonNetWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContent(2);
            }
        });
        textViewNetWork = (TextView) findViewById(R.id.txtNetWork);

        imageButtonTransaction = (ImageButton) findViewById(R.id.imageButtonTransactions);
        imageButtonTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContent(3);
            }
        });
        textViewTransaction = (TextView) findViewById(R.id.txtTransaction);

        imageButtonOther = (ImageButton) findViewById(R.id.imageButtonOther);
        imageButtonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContent(4);
            }
        });
        textViewOther = (TextView) findViewById(R.id.txtOther);

        imageButtonConsole = (ImageButton) findViewById(R.id.imageButtonConsole);
        imageButtonConsole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContent(5);
            }
        });
        textViewConsole = (TextView) findViewById(R.id.txtConsole);

        basket_button = (ImageButton) findViewById(R.id.BasketButton);
        basket_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LogString ="";
                logTextView.setText(LogString);
                if (logFile!=null) logFile.Clear();
            }
        });

        logFile = new LogFile(context,"LogFile.txt");
        if (logFile!=null) {LogString = logFile.Read();/*logFile.readFile();*/}
        else {System.out.println("NOLogFile!");}

        copy_button = (ImageButton) findViewById(R.id.CopyButton);
        copy_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                CopyDialog copyDialog = new CopyDialog();
                copyDialog.setArguments(context, LogString);
                copyDialog.show(fm, "copy_dialog");
            }
        });

        responsesTextView = (TextView) findViewById(R.id.responsesTextView);
    }

    private void AddToLog(String addString){
        if (LogString.length()>0){  LogString+="\n"+addString;
        } else {            LogString=addString;        }
        if (logFile!=null) logFile.Write(LogString);
    }

    private void MsgBox(String title, String message){
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
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /*Network*/
    private void AddNetworkLists(){
        /*ConnectionType*/
        TextView textViewConnection = new TextView(context);
        ListView listViewConnection = new ListView(context);

        Add_CheckListView(textViewConnection, listViewConnection, transArrayConnection, "CHOOSE CONNECTION TYPE:");
        //Add_ListView(textViewConnection, listViewConnection, transArrayConnection, "CHOOSE CONNECTION TYPE:");

        listViewConnection.setItemsCanFocus(false);
        listViewConnection.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewConnection.setItemChecked(0,true);
        listViewConnection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((CheckedTextView)view).setChecked(true);
                connType = position;
                System.out.println(connType);
            }
        });
        /*Choose protocol*/
        TextView textViewProtocol = new TextView(context);
        ListView listViewProtocol = new ListView(context);

        Add_CheckListView(textViewProtocol, listViewProtocol, transArrayProtocol, "CHOOSE PROTOCOL:");
        //Add_ListView(textViewProtocol, listViewProtocol, transArrayProtocol, "CHOOSE PROTOCOL:");

        listViewProtocol.setItemsCanFocus(false);
        listViewProtocol.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewProtocol.setItemChecked(0,true);
        listViewProtocol.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                ((CheckedTextView)view).setChecked(true);
                switch (position) {
                    case 0://HTTP
                        protocol = "http";              break;
                    case 1://HTTPS
                        protocol = "https";             break;
                }
                System.out.println(protocol);
            }
        });

        /*Punch it*/
        TextView textViewPunch = new TextView(context);
        ListView listViewPunch = new ListView(context);

        Add_ListView(textViewPunch, listViewPunch, transArrayPunch, "PUNCH IT:");

        listViewPunch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                componentsString = transArrayPunch[position];
                //CreateNetworkAction();
                new Connection().execute();
                //Toast.makeText(getApplicationContext(),((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });
        /*WebViews*/
        TextView textViewWeb = new TextView(context);
        ListView listViewWeb = new ListView(context);

        String[] transArrayWeb = {"WebView"};
        Add_ListView(textViewWeb, listViewWeb, transArrayWeb, "WEB VIEWS:");

        listViewWeb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                MsgBox("Missing SDK part","No CrittercismConfig.monitorUIWebView: in SDK");
            }
        });
        /*Responses*/
        TextView textViewResponses = new TextView(context);
        Add_TextView(textViewResponses,"RESPONSES:");
    }

    /*Connect*/
    private class Connection extends AsyncTask {
        @Override
        protected Object doInBackground(Object... arg0) {
            CreateNetworkAction();
            return null;
        }
    }
    private void CreateNetworkAction(){
        action_modifier = componentsString.split(" ");
        action_modifier[0] = action_modifier[0].toLowerCase();

        if(action_modifier[0].equals("get") || action_modifier[0].equals("post")){
            latency = 0;
            String[] tmp = action_modifier[1].toLowerCase().replace("mb"," 1000000").replace("kb"," 1000").replace("b"," 1").split(" ");
            bytes = Integer.valueOf(tmp[0]) * Integer.valueOf(tmp[1]);

            if (action_modifier[0].equals("get")) {
                url = protocol+"://httpbin.org/bytes/"+bytes;
            }else {
                System.out.println(protocol);
                url = protocol+"://httpbin.org/post";
            }
        } else if (action_modifier[0].equals("latency")) {
            latency = Integer.valueOf(action_modifier[1].toLowerCase().replace("s",""));
            url = protocol+"://httpbin.org/delay/"+latency;
            action_modifier[0] = "get";
        }else if (action_modifier[0].equals("do")) {
            latency = 0;
            code = Integer.valueOf(action_modifier[1]);
            url = protocol+"://httpbin.org/status/"+code;
            action_modifier[0] = "get";
        }

        long bytesRead,bytesSent;
        if (action_modifier[0].equals("get")){
            bytesRead = bytes;                    bytesSent = 0;
        }else{
            bytesRead = 0;                        bytesSent = bytes;
        }
        long timeBeg=0,timeEnd=0;

        if (connType==0){//HttpURLConnection
            URL url_=null;
            try {
                InputStream myInputStream =null;
                url_= new URL(url);
                HttpURLConnection conn = (HttpURLConnection) url_.openConnection();
                /*conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("Content-length", "100");
                conn.setConnectTimeout(2000);*/
                if (action_modifier[0].equals("post")) conn.setDoOutput(true);
                conn.setChunkedStreamingMode(0);
                conn.setRequestMethod(action_modifier[0].toUpperCase());
                timeBeg =System.currentTimeMillis();

                conn.connect();

                if (action_modifier[0].equals("post")){
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    char[] buf = new char[bytes];
                    System.out.println(action_modifier[0]+": OutputStreamWriter"+bytes);
                    wr.write(buf);
                    wr.flush();
                    System.out.println(action_modifier[0]+": OutputStreamWriter");
                }
                //System.out.println(action_modifier[0]+": "+url);
                status = conn.getResponseCode();
                if(status >= HttpStatus.SC_BAD_REQUEST)
                    myInputStream = conn.getErrorStream();
                else
                    myInputStream = conn.getInputStream();
                timeEnd =System.currentTimeMillis();

                BufferedReader rd = new BufferedReader(new InputStreamReader(myInputStream), 4096);
                String line = "";
                StringBuilder sbResult =  new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    sbResult.append(line);
                }
                rd.close();
                Crittercism.logNetworkRequest(action_modifier[0].toUpperCase(), url_, timeEnd-timeBeg, bytesRead, bytesSent, status, null);
            } catch (Exception e) {
                Crittercism.logNetworkRequest(action_modifier[0].toUpperCase(), url_, timeEnd-timeBeg, bytesRead, bytesSent, status, e);
                System.out.println(action_modifier[0]+": " +e.toString());
            }
            SetResponsesText("(" + status + ") " + url);
        }else {//org.apache.http.client.HttpClient
            HttpResponse response = null;
            HttpClient client = new DefaultHttpClient();
            try {
                if (action_modifier[0].equals("get")){
                    HttpGet request = new HttpGet(url);
                    timeBeg =System.currentTimeMillis();
                    response = client.execute(request);
                } else {
                    HttpPost post = new HttpPost(url);
                    char[] buf = new char[bytes];
                    StringEntity entity = new StringEntity(buf.toString());
                    post.setEntity(entity);
                    timeBeg =System.currentTimeMillis();
                    response = client.execute(post);
                }
                status = response.getStatusLine().getStatusCode();
                timeEnd =System.currentTimeMillis();
                if(status < HttpStatus.SC_BAD_REQUEST)  {
                    BufferedReader rd = new BufferedReader
                            (new InputStreamReader(response.getEntity().getContent()), 4096);
                    String line = "";
                    StringBuilder sbResult =  new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        sbResult.append(line);
                    }
                    rd.close();
                    Crittercism.logNetworkRequest(action_modifier[0].toUpperCase(), new URL(url), timeEnd-timeBeg, bytesRead, bytesSent, status, null);
                }
            }catch (Exception e) {
                try {
                    Crittercism.logNetworkRequest(action_modifier[0].toUpperCase(), new URL(url), timeEnd-timeBeg, bytesRead, bytesSent, status, e);
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
                System.out.println(action_modifier[0]+": " +e.toString());
            }
            SetResponsesText("(" + status + ") " + url);
        }
        AddToLog("[Network]: " + componentsString);
    }

    private void SetResponsesText(String textString){
        String str = responsesTextView.getText().toString();
        if (str.length()>0){
            responsesTextView.setText(str+"\n"+textString);
        }else{
            responsesTextView.setText(textString);
        }
    }

    /*Transactions*/
    private void AddTransactionLists(){
        AddTransactionListView("Login");
        AddTransactionListView("Browse");
        AddTransactionListView("Reserve");
        AddTransactionListView("Confirm");
    }
    private void AddTransactionListView(String TransactionName){
        TextView textView = new TextView(context);
        ListView listView = new ListView(context);

        String[] transArray = { "Tx \""+TransactionName+"\": Begin",
                "Tx \""+TransactionName+"\": End",
                "Tx \""+TransactionName+"\": Fail",
                "Tx \""+TransactionName+"\": Add 1",
                "Tx \""+TransactionName+"\": Get Value" };

        Add_ListView(textView, listView, transArray, "TRANSACTION '"+TransactionName.toUpperCase()+"':");

        listView.setContentDescription(TransactionName);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                switch (position) {
                    case 0://Begin
                        Crittercism.beginTransaction(parent.getContentDescription()+"");
                        AddToLog("[Transactions]: Begin " + parent.getContentDescription());
                        break;
                    case 1://End
                        Crittercism.endTransaction(parent.getContentDescription()+"");
                        AddToLog("[Transactions]: End " + parent.getContentDescription());
                        break;
                    case 2://Fail
                        Crittercism.failTransaction(parent.getContentDescription()+"");
                        AddToLog("[Transactions]: Fail " + parent.getContentDescription());
                        break;
                    case 3://Add 1
                        int i = Crittercism.getTransactionValue(parent.getContentDescription()+"");
                        if(i<0) {Crittercism.setTransactionValue(parent.getContentDescription()+"", 1);}
                        else Crittercism.setTransactionValue(parent.getContentDescription()+"", i+1);
                        AddToLog("[Transactions]: Add " + parent.getContentDescription());
                        break;
                    case 4://GetValue
                        int j = Crittercism.getTransactionValue(parent.getContentDescription()+"");
                        if (j==-1) j=0;
                        MsgBox(parent.getContentDescription()+"","Transaction value="+j);
                        AddToLog("[Transactions]: Get " + parent.getContentDescription());
                        break;
                }
            }
        });
    }

    /*Other*/
    private void AddOtherLists() {
        /*UserName*/
        TextView textViewName = new TextView(context);
        ListView listViewName = new ListView(context);

        String[] transArrayName = {"Set Username: Bob", "Set Username: Jim",
                "Set Username: Sue", "Check Username"};
        Add_ListView(textViewName, listViewName, transArrayName, "USERNAME:");

        listViewName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                switch (position) {
                    case 0://Bob
                        Crittercism.setUsername("Bob");   AddToLog("[Other]: Set Username: Bob");
                        break;
                    case 1://Jim
                        Crittercism.setUsername("Jim");   AddToLog("[Other]: Set Username: Jim");
                        break;
                    case 2://Sue
                        Crittercism.setUsername("Sue");   AddToLog("[Other]: Set Username: Sue");
                        break;
                    case 3://Check username
                        AddToLog("[Other]: Check Username");
                        MsgBox("Missing SDK part","No getUsername: in SDK");
                        break;
                }
            }
        });
        /*Metadata*/
        TextView textViewData = new TextView(context);
        ListView listViewData = new ListView(context);

        String[] transArrayData = {"Set Game Level: 5", "Set Game Level: 30",
                "Set Game Level: 50", "Check Game Level"};
        Add_ListView(textViewData, listViewData, transArrayData, "METADATA:");

        listViewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                int n=0;
                switch (position) {
                    case 0://5
                        n=5;               break;
                    case 1://30
                        n=30;              break;
                    case 2://50
                        n=50;              break;
                    case 3://Check game level
                        AddToLog("[Other]: Check Game Level");
                        MsgBox("Missing SDK part","No valueForKey: in SDK");
                        break;
                }
                if(position<3){
                    try {
                        Crittercism.setMetadata(new JSONObject("{\"Game Level\":\""+n+"\"}"));
                        AddToLog("[Other]: Set Username: Jim");
                    } catch (Throwable t) {
                        System.out.println("Crittercism. Could not parse malformed JSON: '"+"{\"Game Level\":\""+n+"\"}"+"'");
                    }
                }
            }
        });
        /*Breadcrumbs*/
        TextView textViewLeave = new TextView(context);
        ListView listViewLeave = new ListView(context);
        String[] transArrayLeave = {"Leave: 'hello world'", "Leave: 'abc'", "Leave: '123'"};
        Add_ListView(textViewLeave, listViewLeave, transArrayLeave, "BREADCRUMBS:");
        listViewLeave.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                switch (position) {
                    case 0://hello world
                        Crittercism.leaveBreadcrumb("hello world");   AddToLog("[Other]: Leave: 'hello world'");
                        break;
                    case 1://abc
                        Crittercism.leaveBreadcrumb("abc");           AddToLog("[Other]: Leave: 'abc'");
                        break;
                    case 2://123
                        Crittercism.leaveBreadcrumb("123");           AddToLog("[Other]: Leave: '123'");
                        break;
                }
            }
        });
        /*Opt-out status*/
        TextView textViewStatus = new TextView(context);
        ListView listViewStatus = new ListView(context);

        String[] transArrayStatus = {"Opt Out", "Opt In", "Check Opt-Out Status"};
        Add_ListView(textViewStatus, listViewStatus, transArrayStatus, "OPT-OUT STATUS:");

        listViewStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                switch (position) {
                    case 0://Opt Out
                        Crittercism.setOptOutStatus(true);        AddToLog("[Other]: Opt Out");
                        break;
                    case 1://Opt In
                        Crittercism.setOptOutStatus(false);       AddToLog("[Other]: Opt In");
                        break;
                    case 2://Check Opt-Out Status
                        AddToLog("[Other]: Check Opt-Out Status");
                        MsgBox("OptOutStatus","is "+Crittercism.getOptOutStatus());
                        // if (Crittercism.getOptOutStatus()) {MsgBox("OptOutStatus","is YES");}
                        // else {MsgBox("OptOutStatus","is NO");}
                        break;
                }
            }
        });
    }

    private void Add_TextView(TextView textView, String textText){
        textView.setBackgroundColor(0xffefeff4);
        textView.setGravity(Gravity.START);
        //textView.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        //textView.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        //textView.setHeight(TypedValue.COMPLEX_UNIT_SP, 40);
        textView.setTextColor(Color.GRAY);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setFocusable(false);
        //textView.setId("@+id/TransactionLoginTitle");
        textView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        textView.setDrawingCacheBackgroundColor(Color.GRAY);
        textView.setText(textText);

        workLayout.addView(textView);
    }

    private void Add_ListView(TextView textView, ListView listView, String[] transArray, String textText){
        Add_TextView(textView,textText);
       /* listView.setLayoutParams(new ListView.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,(int) 1.0f));
*/
        listView.setBackgroundColor(Color.WHITE);
        listView.setDividerHeight(1);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, transArray);
        listView.setAdapter(arrayAdapter);

        workLayout.addView(listView);
        setListViewHeightBasedOnChildren(listView);
    }

    private void Add_CheckListView(TextView textView, ListView listView, String[] transArray, String textText){

        Add_TextView(textView,textText);

        listView.setBackgroundColor(Color.WHITE);
        listView.setDividerHeight(1);

        //arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, transArray);
        stringAdapter = new ArrayAdapter<String>(this, R.layout.list_item, transArray);

        listView.setAdapter(stringAdapter);

        workLayout.addView(listView);
        setListViewHeightBasedOnChildren(listView);
/*

        Add_TextView(textView,textText);

        listView.setBackgroundColor(Color.WHITE);
        listView.setDividerHeight(1);

        ArrayList<String> transList = new ArrayList();
        for (int i =0; i<transArray.length;i++ ){
            transList.add(transArray[i]);
        }
        checkBoxArrayAdapter = new CheckBoxArrayAdapter(this, android.R.layout.simple_list_item_checked, transList);
        listView.setAdapter(checkBoxArrayAdapter);

        workLayout.addView(listView);
        setListViewHeightBasedOnChildren(listView);*/
    }

    private void AddErrorsView() {
        /*force crash*/
        TextView textViewCrash = new TextView(context);
        ListView listViewCrash = new ListView(context);

        String[] transArrayCrash = {"Uncaught Exception", "Segfault", "Stack Overflow"};
        Add_ListView(textViewCrash, listViewCrash, transArrayCrash, "FORCE CRASH:");

        listViewCrash.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                switch (position) {
                    case 0://Uncaught Exception
                        System.out.println("Crittercism. Raising custom uncaught exception");
                        try {
                            throw new Exception("This is a forced uncaught exception");
                        } catch (Exception exception) {
                            Crittercism.logHandledException(exception);
                            AddToLog("[Error]: Uncaught Exception");
                        }
                        break;
                    case 1://Segfault
                        System.out.println("Crittercism. Calling kill with SIGSEGV");
                        try {
                            throw new IllegalStateException();
                        } catch (Exception exception) {
                            Crittercism.logHandledException(exception);
                            AddToLog("[Error]: Segfault");
                        }
                        break;
                    case 2://Stack Overflow
                        System.out.println("Crittercism. Logging exception: stack overflow");
                        try {
                            throw new StackOverflowError();
                        } catch (Exception exception) {
                            Crittercism.logHandledException(exception);
                            AddToLog("[Error]: Stack Overflow");
                        }
                        //recurse
                        break;
                }
                // Toast.makeText(getApplicationContext(),((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });
        /*handle exception*/
        TextView textViewHandle = new TextView(context);
        ListView listViewHandle = new ListView(context);

        String[] transArrayHandle = {"Index Out Of Bounds", "Log Error"};
        Add_ListView(textViewHandle, listViewHandle, transArrayHandle, "HANDLE EXCEPTION:");

        listViewHandle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                switch (position) {
                    case 0://Index Out Of Bounds
                        System.out.println("Crittercism. Logging exception: out of bounds");
                        try {
                            throw new ArrayIndexOutOfBoundsException();
                        } catch (Exception exception) {
                            Crittercism.logHandledException(exception);
                            AddToLog("[Error]: Index Out OfBounds");
                        }
                        break;
                    case 1://Log Error
                        AddToLog("[Error]: Log Error");
                        MsgBox("Missing SDK part","No logError: in SDK");
                        break;
                }
                // Toast.makeText(getApplicationContext(),((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setContent(int id) {
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

        workLayout.removeAllViews();

        switch (id) {
            case 1:
                textViewError.setTextColor(0xff007aff);
                imageButtonError.setImageResource(R.drawable.bug_blue);
                textTitleView.setText(R.string.title_error);
                relativeLayout.setBackgroundColor(0xffefeff4);
                basket_button.setVisibility(View.INVISIBLE);
                copy_button.setVisibility(View.INVISIBLE);
                responsesTextView.setVisibility(View.INVISIBLE);
                AddErrorsView();
                break;
            case 2:
                textViewNetWork.setTextColor(0xff007aff);
                imageButtonNetWork.setImageResource(R.drawable.network_blue);
                textTitleView.setText(R.string.title_network);
                relativeLayout.setBackgroundColor(0xffefeff4);
                basket_button.setVisibility(View.INVISIBLE);
                copy_button.setVisibility(View.INVISIBLE);
                responsesTextView.setVisibility(View.VISIBLE);
                AddNetworkLists();

                break;
            case 3:
                textViewTransaction.setTextColor(0xff007aff);
                imageButtonTransaction.setImageResource(R.drawable.shopping_cart_blue);
                textTitleView.setText(R.string.title_transactions);
                relativeLayout.setBackgroundColor(0xffefeff4);
                basket_button.setVisibility(View.INVISIBLE);
                copy_button.setVisibility(View.INVISIBLE);
                responsesTextView.setVisibility(View.INVISIBLE);
                AddTransactionLists();

                break;
            case 4:
                textViewOther.setTextColor(0xff007aff);
                imageButtonOther.setImageResource(R.drawable.controller_blue);
                textTitleView.setText(R.string.title_other);
                relativeLayout.setBackgroundColor(0xffefeff4);
                basket_button.setVisibility(View.INVISIBLE);
                copy_button.setVisibility(View.INVISIBLE);
                responsesTextView.setVisibility(View.INVISIBLE);
                AddOtherLists();

                break;
            case 5:
                textViewConsole.setTextColor(0xff007aff);
                imageButtonConsole.setImageResource(R.drawable.glasses_blue);
                textTitleView.setText(R.string.title_console);
                relativeLayout.setBackgroundColor(Color.WHITE);
                basket_button.setVisibility(View.VISIBLE);
                copy_button.setVisibility(View.VISIBLE);
                responsesTextView.setVisibility(View.INVISIBLE);
                showConsole();
                break;
        }
    }

    private void showConsole(){
        logTextView = new TextView(context);
        Add_TextView(logTextView);
        logTextView.setText(LogString);
        //logTextView.setText(logString);
        //     View child = getLayoutInflater().inflate(R.layout.console, null);
        //     workLayout.addView(child);
    }

    /*Log*/
    private void Add_TextView(TextView textView){
        textView.setBackgroundColor(Color.WHITE);
        textView.setGravity(Gravity.START);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setFocusable(false);
        textView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        workLayout.addView(textView);
    }
}
