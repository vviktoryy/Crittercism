package com.crittercism;

import android.os.AsyncTask;
import android.widget.TextView;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class HttpConnection  extends AsyncTask {
    TextView textView;
    String componentsString, url, protocol, method;
    int connType = 0;
    int bytes = 1;
    int status;

    URL url_;
    long time, bytesRead, bytesSent;
    Exception ex;
    WorkActivity act;
    FragmentNetwork v;

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    public HttpConnection(String componentsString_, String protocol_, int connType_,
                          TextView textView_, WorkActivity act_, FragmentNetwork v_) {
        componentsString = componentsString_;
        protocol = protocol_;
        connType = connType_;
        textView = textView_;
        act = act_;
        v = v_;
    }

    @Override
    protected Object doInBackground(Object... arg0) {
        return CreateNetworkAction();
    }
    @Override
    protected void onPostExecute(Object result) {
        if (result!=null){
            String res = result.toString();
            v.SetResponsesText(res);
            v.WriteCrittercismNetLog(method, url_, time, bytesRead, bytesSent, status, ex);
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
        method = action_modifier[0];
        try {
            url_ = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (action_modifier[0].equals("get")){
            bytesRead = bytes;                    bytesSent = 0;
        }else{
            bytesRead = 0;                        bytesSent = bytes;
        }
        long timeBeg,timeEnd;
        if (connType==0){//HttpURLConnection
            try {
                InputStream myInputStream;
                HttpURLConnection conn = (HttpURLConnection) url_.openConnection();
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
                time = timeEnd-timeBeg;
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
            } catch (Exception e) {
                ex = e;
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
                time = timeEnd-timeBeg;
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
            }catch (Exception e) {
                ex = e;
                return null;
            }

        } else {//OkHttpClient
            OkHttpClient client = new OkHttpClient();
            Response response;
            Request request;

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
                time = timeEnd-timeBeg;
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
            } catch (IOException e) {
                ex = e;
                return null;
            }
        }
        return "(" + status + ") " + url;
    }
}
