package com.crittercism;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.app.AlertDialog;
import android.widget.Toast;
import com.crittercism.app.Crittercism;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.crittercism.app.CrittercismConfig;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class WorkActivity extends FragmentActivity implements ScrollViewListener{
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

    private LinearLayout errorButtonsLayout;

    private LinearLayout workLayout;
    private Context context;

    private String[] transArrayPunch = {"Get 100b", "Get 5Kb", "Get 7Mb",
            "Post 100b", "Post 4Kb", "Post 3Mb",
            "Latency 1s", "Latency 3s", "Latency 10s",
            "Do 202", "Do 404", "Do 500"};
    private String[] transArrayProtocol = {"HTTP", "HTTPS"};
    private String[] transArrayConnection = {"[HttpURLConnection]","[org.apache.http.client.HttpClient]","[OkHttpClient]"};

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    public List<String> transArrayStack;
    public ArrayAdapter arrayFuncAdapter;

    private String url;
    private String protocol="http";
    private int connType = 0;
    private int bytes = 1;
    private int status;

    private String LogString="";
    private TextView logTextView;
    private TextView responsesTextView;
    private TextView textViewStatus;
    private Boolean optOutStatus=false;

    private ListView listViewStack;
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

    @Override
    protected void onResume(){
        overridePendingTransition(android.R.anim.fade_in, R.anim.abc_fade_out);
        super.onResume();
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

    private void setupViews() {
        ScrollViewExt scrollView = (ScrollViewExt) findViewById(R.id.scrollView);
        scrollView.setScrollViewListener(this);

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
        if (logFile.file.exists()) {LogString = logFile.Read();/*logFile.readFile();*/}
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
        responsesTextView.setMovementMethod(new ScrollingMovementMethod());

        errorButtonsLayout = (LinearLayout) findViewById(R.id.errorButtonsLayout);

        Button clearButton = (Button) findViewById(R.id.clearButton);
        buttonEffect(clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                transArrayStack.clear();
                transArrayStack.add("Add Function...");
                ((TextView) listViewStack.getChildAt(listViewStack.getAdapter().getCount() - 1)).setTextColor(Color.GRAY);
                arrayFuncAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(listViewStack);
                AddToLog("[Error]: Clear");
            }
        });
        Button exceptionButton = (Button) findViewById(R.id.exceptionButton);
        buttonEffect(exceptionButton);
        exceptionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (int i = 0; i < transArrayStack.size(); i++) {
                    switch (transArrayStack.get(i)) {
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
                            System.out.println("Crittercism. Raising forced caught exception");
                            try {
                                throw new Exception("This is a forced caught exception");
                            } catch (Exception exception) {
                                Crittercism.logHandledException(exception);
                                AddToLog("[Error]: Exception");
                            }
                            break;
                    }
                }
            }
        });
        Button crashButton = (Button) findViewById(R.id.crashButton);
        buttonEffect(crashButton);
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for (int i = 0; i < transArrayStack.size(); i++) {
                    switch (transArrayStack.get(i)) {
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
                            System.out.println("Crittercism. Raising forced uncaught exception");
                            AddToLog("[Error]: Crash");
                            throw new RuntimeException("This is a forced uncaught exception");
                            /*try {
                                throw new Exception("This is a forced uncaught exception");
                            } catch (Exception e) {
                            }*/
                    }
                }
            }
        });

        transArrayStack = new ArrayList<>();
        transArrayStack.add("Add Function...");

    }

    private void funcA(){
        AddToLog("[Error]: Function A called...");
    }

    private void funcB(){
        AddToLog("[Error]: Function B called...");
    }

    private void funcC(){
        AddToLog("[Error]: Function C called...");
    }

    private void funcD(){
        AddToLog("[Error]: Function D called...");
    }

    public void AddToLog(String addString){
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
        listViewConnection.setItemChecked(connType,true);
        listViewConnection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((CheckedTextView)view).setChecked(true);
                connType = position;
            }
        });
        /*Choose protocol*/
        TextView textViewProtocol = new TextView(context);
        ListView listViewProtocol = new ListView(context);

        Add_CheckListView(textViewProtocol, listViewProtocol, transArrayProtocol, "CHOOSE PROTOCOL:");
        //Add_ListView(textViewProtocol, listViewProtocol, transArrayProtocol, "CHOOSE PROTOCOL:");

        listViewProtocol.setItemsCanFocus(false);
        listViewProtocol.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if (protocol.equals("http")) listViewProtocol.setItemChecked(0,true);
        else listViewProtocol.setItemChecked(1, true);
        listViewProtocol.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                ((CheckedTextView)view).setChecked(true);
                switch (position) {
                    case 0://HTTP
                        protocol = "http";              break;
                    case 1://HTTPS
                        protocol = "https";             break;
                }
            }
        });

        /*Punch it*/
        TextView textViewPunch = new TextView(context);
        GridView gridViewPunch = new GridView(context);
        Add_TextView(textViewPunch, "PUNCH IT:");

        gridViewPunch.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        gridViewPunch.setNumColumns(3);
        gridViewPunch.setGravity(Gravity.CENTER);
        gridViewPunch.setBackgroundColor(Color.WHITE);

        arrayAdapter = new ArrayAdapter(this, R.layout.linear_list_item, transArrayPunch);
        gridViewPunch.setAdapter(arrayAdapter);

        workLayout.addView(gridViewPunch);
        setGridViewHeightBasedOnChildren(gridViewPunch,3);

        gridViewPunch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                //CrittercismConfig config = new CrittercismConfig();
                //config.monitorUIWebView = true;
                //Crittercism.enableWithAppID("4ce2d43766d78766a1000013", andConfig: config)
                StartWebView();
                //MsgBox("Missing SDK part","No CrittercismConfig.monitorUIWebView: in SDK");
            }
        });
        /*Responses*/
        TextView textViewResponses = new TextView(context);
        Add_TextView(textViewResponses,"RESPONSES:");
    }

    /*start webView*/
    private void StartWebView(){
        Intent intent = new Intent();
        intent.setClass(this, WebViewActivity.class);
        startActivity(intent);
    }

    /*Connect*/
    private class Connection extends AsyncTask {
        @Override
        protected Object doInBackground(Object... arg0) {
            return CreateNetworkAction();
        }
        @Override
        protected void onPostExecute(Object result) {
            if (result!=null){
                String res = result.toString();
                SetResponsesText(res);
            }
        }
    }
    private String CreateNetworkAction(){
        String[] action_modifier = componentsString.split(" ");
        action_modifier[0] = action_modifier[0].toLowerCase();

        int latency;
        switch (action_modifier[0]) {
            case "get":
            case "post":
                String[] tmp = action_modifier[1].toLowerCase().replace("mb", " 1000000").replace("kb", " 1000").replace("b", " 1").split(" ");
                bytes = Integer.valueOf(tmp[0]) * Integer.valueOf(tmp[1]);

                if (action_modifier[0].equals("get"))
                    url = protocol + "://httpbin.org/bytes/" + bytes;
                else {
                    System.out.println(protocol);
                    url = protocol + "://httpbin.org/post";
                }
                break;
            case "latency":
                latency = Integer.valueOf(action_modifier[1].toLowerCase().replace("s", ""));
                url = protocol + "://httpbin.org/delay/" + latency;
                action_modifier[0] = "get";
                break;
            case "do":
                int code = Integer.valueOf(action_modifier[1]);
                url = protocol + "://httpbin.org/status/" + code;
                action_modifier[0] = "get";
                break;
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
                InputStream myInputStream;
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
                    wr.write(buf);
                    wr.flush();
                }

                status = conn.getResponseCode();
                if(status >= HttpStatus.SC_BAD_REQUEST)
                    myInputStream = conn.getErrorStream();
                else
                    myInputStream = conn.getInputStream();
                timeEnd =System.currentTimeMillis();
                if (action_modifier[0].equals("get")) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(myInputStream), 4096);
                    String line;
                    StringBuilder sbResult;
                    sbResult = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        sbResult.append(line);
                    }
                    rd.close();
                }
                Crittercism.logNetworkRequest(action_modifier[0].toUpperCase(), url_, timeEnd-timeBeg, bytesRead, bytesSent, status, null);
            } catch (Exception e) {
                Crittercism.logNetworkRequest(action_modifier[0].toUpperCase(), url_, timeEnd-timeBeg, bytesRead, bytesSent, status, e);
                System.out.println(action_modifier[0]+": " +e.toString());
                return null;
            }

        }else if (connType==1) {//org.apache.http.client.HttpClient
            HttpResponse response;
            HttpClient client = new DefaultHttpClient();
            try {
                if (action_modifier[0].equals("get")){
                    HttpGet request = new HttpGet(url);
                    timeBeg =System.currentTimeMillis();
                    response = client.execute(request);
                } else {
                    HttpPost post = new HttpPost(url);
                    char[] buf = new char[bytes];
                    StringEntity entity = new StringEntity(Arrays.toString(buf));
                    post.setEntity(entity);
                    timeBeg =System.currentTimeMillis();
                    response = client.execute(post);
                }
                status = response.getStatusLine().getStatusCode();
                timeEnd =System.currentTimeMillis();
                if(action_modifier[0].equals("get") && status < HttpStatus.SC_BAD_REQUEST)  {
                    BufferedReader rd = new BufferedReader
                            (new InputStreamReader(response.getEntity().getContent()), 4096);
                    String line;
                    StringBuilder sbResult;
                    sbResult = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        sbResult.append(line);
                    }
                    rd.close();
                }

                Crittercism.logNetworkRequest(action_modifier[0].toUpperCase(), new URL(url), timeEnd-timeBeg, bytesRead, bytesSent, status, null);
            }catch (Exception e) {
                try {
                    Crittercism.logNetworkRequest(action_modifier[0].toUpperCase(), new URL(url), timeEnd-timeBeg, bytesRead, bytesSent, status, e);
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                    return null;
                }
            }

        } else {//OkHttpClient
            OkHttpClient client = new OkHttpClient();
            Response response;
            Request request;
            URL url_= null;
            try { url_ = new URL(url); } catch (MalformedURLException e) {            }

            if (action_modifier[0].equals("get")){
                request = new Request.Builder()
                        .url(url)
                        .build();
            }else{
                char[] buf = new char[bytes];
                RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, new String(buf));
                request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
            }

            try {
                timeBeg =System.currentTimeMillis();
                response = client.newCall(request).execute();
                status = response.code();
                timeEnd =System.currentTimeMillis();

                if(action_modifier[0].equals("get") && status < HttpStatus.SC_BAD_REQUEST)  {
                   BufferedReader rd = new BufferedReader
                            (new InputStreamReader(response.body().byteStream()), 4096);
                    String line;
                    StringBuilder sbResult;
                    sbResult = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        sbResult.append(line);
                    }
                    rd.close();
                }
                Crittercism.logNetworkRequest(action_modifier[0].toUpperCase(), url_, timeEnd-timeBeg, bytesRead, bytesSent, status, null);
            } catch (IOException e) {
                Crittercism.logNetworkRequest(action_modifier[0].toUpperCase(), url_, timeEnd-timeBeg, bytesRead, bytesSent, status, e);
                System.out.println(action_modifier[0]+": " +e.toString());
                return null;
            }
        }
        AddToLog("[Network]: " + componentsString);
        return "(" + status + ") " + url;
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
        final ListView listViewName = new ListView(context);

        String[] transArrayName = {"Set Username: Bob", "Set Username: Jim",
                "Set Username: Sue"};
        /*String[] transArrayName = {"Set Username: Bob", "Set Username: Jim",
                "Set Username: Sue", "Check Username"};*/
        Add_ListView(textViewName, listViewName, transArrayName, "USERNAME:");
        //listViewName.setItemChecked(0, true);
        /*listViewName.post(new Runnable() {
            public void run()  {
                TextView view = (TextView)listViewName.getChildAt(listViewName.getAdapter().getCount()-1);
                view.setTextColor(Color.BLUE);
                view.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        });*/
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
        final ListView listViewData = new ListView(context);

        String[] transArrayData = {"Set Game Level: 5", "Set Game Level: 30",
                "Set Game Level: 50"};
        /*String[] transArrayData = {"Set Game Level: 5", "Set Game Level: 30",
                "Set Game Level: 50", "Check Game Level"};*/
        Add_ListView(textViewData, listViewData, transArrayData, "METADATA:");

        /*listViewData.post(new Runnable() {
            public void run()  {
                TextView view = (TextView)listViewData.getChildAt(listViewData.getAdapter().getCount()-1);
                view.setTextColor(Color.BLUE);
                view.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        });*/

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
        textViewStatus = new TextView(context);
        final ListView listViewStatus = new ListView(context);

        String[] transArrayStatus = {"Opt Out", "Opt In"};
        /*String[] transArrayStatus = {"Opt Out", "Opt In", "Check Opt-Out Status"};*/
        Add_ListView(textViewStatus, listViewStatus, transArrayStatus, "OPT-OUT STATUS: "+optOutStatus);
       /* listViewStatus.post(new Runnable() {
            public void run()  {
                TextView view = (TextView)listViewStatus.getChildAt(listViewStatus.getAdapter().getCount()-1);
                view.setTextColor(Color.BLUE);
                view.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        });*/
        listViewStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                switch (position) {
                    case 0://Opt Out
                        Crittercism.setOptOutStatus(true);        AddToLog("[Other]: Opt Out");
                        optOutStatus = true;                        break;
                    case 1://Opt In
                        Crittercism.setOptOutStatus(false);       AddToLog("[Other]: Opt In");
                        optOutStatus = false;                        break;
                    /*case 2://Check Opt-Out Status
                        AddToLog("[Other]: Check Opt-Out Status");
                        MsgBox("OptOutStatus","is "+Crittercism.getOptOutStatus());
                        // if (Crittercism.getOptOutStatus()) {MsgBox("OptOutStatus","is YES");}
                        // else {MsgBox("OptOutStatus","is NO");}
                        break;*/
                }
                textViewStatus.setText("OPT-OUT STATUS: "+optOutStatus);
            }
        });
    }

    private void Add_TextView(TextView textView, String textText){
        textView.setBackgroundColor(0xffefeff4);
        textView.setGravity(Gravity.START);
        textView.setTextColor(Color.GRAY);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setFocusable(false);
        textView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        textView.setDrawingCacheBackgroundColor(Color.GRAY);
        textView.setText(textText);

        workLayout.addView(textView);
    }

    private void Add_ListView(TextView textView, ListView listView, String[] transArray, String textText){
        Add_TextView(textView, textText);

        listView.setBackgroundColor(Color.WHITE);
        listView.setDividerHeight(1);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, transArray);
        listView.setAdapter(arrayAdapter);

        workLayout.addView(listView);
        setListViewHeightBasedOnChildren(listView);
    }

    private void Add_ListView1(TextView textView, ListView listView, String[] transArray, String textText){
        Add_TextView(textView, textText);

        listView.setBackgroundColor(Color.WHITE);
        listView.setDividerHeight(1);

        arrayAdapter = new ArrayAdapter(this, R.layout.linear_list_item1, transArray);
        listView.setAdapter(arrayAdapter);

        workLayout.addView(listView);
        setListViewHeightBasedOnChildren(listView);
    }

    private void Add_ListView2(TextView textView, ListView listView, List<String> transArray, String textText){
        Add_TextView(textView, textText);

        listView.setBackgroundColor(Color.WHITE);
        listView.setDividerHeight(1);

        arrayFuncAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, transArray);
        listView.setAdapter(arrayFuncAdapter);

        workLayout.addView(listView);
        setListViewHeightBasedOnChildren(listView);
    }

    private void Add_CheckListView(TextView textView, ListView listView, String[] transArray, String textText){
        Add_TextView(textView,textText);

        listView.setBackgroundColor(Color.WHITE);
        listView.setDividerHeight(1);

        ArrayAdapter<String> stringAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, transArray);

        listView.setAdapter(stringAdapter);

        workLayout.addView(listView);
        setListViewHeightBasedOnChildren(listView);
    }

    private void AddErrorsView() {
        /*force crash*/
        TextView textViewCrash = new TextView(context);
        ListView listViewCrash = new ListView(context);

        String[] transArrayCrash = {"Uncaught Exception", "Segfault", "Stack Overflow"};
        Add_ListView1(textViewCrash, listViewCrash, transArrayCrash, "FORCE JAVA CRASH:");//"FORCE CRASH:"

        listViewCrash.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://Uncaught Exception
                        System.out.println("Crittercism. Raising custom uncaught exception");
                        AddToLog("[Error]: Uncaught Exception");
                        throw new RuntimeException("This is a forced uncaught exception");
                        /*try {
                            throw new Exception("This is a forced uncaught exception");
                        } catch (Exception exception) {
                            Crittercism.logHandledException(exception);
                            AddToLog("[Error]: Uncaught Exception");
                        }
                        break;*/
                    case 1://Segfault
                        System.out.println("Crittercism. Calling kill with SIGSEGV");
                        AddToLog("[Error]: Segfault");
                        throw new IllegalStateException();
                        /*try {
                            throw new IllegalStateException();
                        } catch (Exception exception) {
                            Crittercism.logHandledException(exception);
                            AddToLog("[Error]: Segfault");
                        }
                        break;*/
                    case 2://Stack Overflow
                        System.out.println("Crittercism. Logging exception: stack overflow");
                        AddToLog("[Error]: Stack Overflow");
                        throw new StackOverflowError();
                        /*try {
                            throw new StackOverflowError();
                        } catch (Exception exception) {
                            Crittercism.logHandledException(exception);
                            AddToLog("[Error]: Stack Overflow");
                        }
                        //recurse
                        break;*/
                }
                // Toast.makeText(getApplicationContext(),((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });
        /*handle exception*/
        TextView textViewHandle = new TextView(context);
        ListView listViewHandle = new ListView(context);

        //String[] transArrayHandle = {"Index Out Of Bounds", "Log Error"};
        String[] transArrayHandle = {"Index Out Of Bounds", "Input/Output"};
        Add_ListView1(textViewHandle, listViewHandle, transArrayHandle, "HANDLE EXCEPTION:");

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
                            Toast.makeText(getApplicationContext(),"Index Out OfBounds", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1://Input/Output //Log Error
                       /* AddToLog("[Error]: Log Error");
                        MsgBox("Missing SDK part","No logError: in SDK");
                        break;*/
                        System.out.println("Crittercism. Logging exception: input/output");
                        try {
                            throw new IOException();
                        } catch (Exception exception) {
                            Crittercism.logHandledException(exception);
                            AddToLog("[Error]: Input/Output Exception");
                            Toast.makeText(getApplicationContext(),"Input/Output Exception", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                // Toast.makeText(getApplicationContext(),((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });
        /*Custom stack trace*/
        TextView textViewStack = new TextView(context);
        listViewStack = new ListView(context);

        Add_ListView2(textViewStack, listViewStack, transArrayStack, "CUSTOM STACK TRACE:");
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
                        FragmentManager fm = getSupportFragmentManager();
                        FunctionDialog functionDialog = new FunctionDialog();
                        functionDialog.setArguments(context, listViewStack);
                        functionDialog.show(fm, "function_dialog");
                        break;
                    default:
                        break;
                }
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
                basket_button.setVisibility(View.GONE);
                copy_button.setVisibility(View.GONE);
                responsesTextView.setVisibility(View.GONE);
                errorButtonsLayout.setVisibility(View.VISIBLE);
                AddErrorsView();
                break;
            case 2:
                textViewNetWork.setTextColor(0xff007aff);
                imageButtonNetWork.setImageResource(R.drawable.network_blue);
                textTitleView.setText(R.string.title_network);
                relativeLayout.setBackgroundColor(0xffefeff4);
                basket_button.setVisibility(View.GONE);
                copy_button.setVisibility(View.GONE);
                responsesTextView.setVisibility(View.VISIBLE);
                errorButtonsLayout.setVisibility(View.GONE);
                AddNetworkLists();

                break;
            case 3:
                textViewTransaction.setTextColor(0xff007aff);
                imageButtonTransaction.setImageResource(R.drawable.shopping_cart_blue);
                textTitleView.setText(R.string.title_transactions);
                relativeLayout.setBackgroundColor(0xffefeff4);
                basket_button.setVisibility(View.GONE);
                copy_button.setVisibility(View.GONE);
                responsesTextView.setVisibility(View.GONE);
                errorButtonsLayout.setVisibility(View.GONE);
                AddTransactionLists();

                break;
            case 4:
                textViewOther.setTextColor(0xff007aff);
                imageButtonOther.setImageResource(R.drawable.controller_blue);
                textTitleView.setText(R.string.title_other);
                relativeLayout.setBackgroundColor(0xffefeff4);
                basket_button.setVisibility(View.GONE);
                copy_button.setVisibility(View.GONE);
                responsesTextView.setVisibility(View.GONE);
                errorButtonsLayout.setVisibility(View.GONE);
                AddOtherLists();

                break;
            case 5:
                textViewConsole.setTextColor(0xff007aff);
                imageButtonConsole.setImageResource(R.drawable.glasses_blue);
                textTitleView.setText(R.string.title_console);
                relativeLayout.setBackgroundColor(Color.WHITE);
                basket_button.setVisibility(View.VISIBLE);
                copy_button.setVisibility(View.VISIBLE);
                responsesTextView.setVisibility(View.GONE);
                errorButtonsLayout.setVisibility(View.GONE);
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
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.START);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setFocusable(false);
        textView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        workLayout.addView(textView);
    }

    /*for click effect on transparent buttons putting*/
    public static void buttonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ((TextView)v).setTextColor(Color.GRAY);
                        //v.setBackgroundColor(Color.CYAN);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        //v.setBackgroundColor(Color.WHITE);
                        ((TextView)v).setTextColor(Color.BLUE);
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
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
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        //params.height = totalHeight + (gridView.getDividerHeight() * (listAdapter.getCount() - 1));
        params.height = totalHeight/colCount;
        gridView.setLayoutParams(params);
        gridView.requestLayout();
    }


}
