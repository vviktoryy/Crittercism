package com.crittercism.utils;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class LogFile {
    public String LogName;
    public File file;
    public Context context;

    public LogFile(Context context_, String LogName_){
        context = context_;
        LogName = LogName_;
        file = new File(context.getFilesDir(), LogName);
        if(!file.exists()){
            try {
                Boolean fileCreated = file.createNewFile();
                if (!fileCreated) file = null;
            }catch (Exception e) {
                System.out.println("LogFile: " + e.getMessage());
            }
        }
    }

    public String Read(){
        String readString=null;
        try {
            FileInputStream fIn = context.openFileInput(LogName);
            InputStreamReader isr = new InputStreamReader(fIn);

            char[] inputBuffer =new char[(int)file.length()];
            isr.read(inputBuffer);
            readString = new String(inputBuffer);
        }catch (Exception e) {
            System.out.println("LogFile: " + e.getMessage());
        }
        return readString;
    }

    public void Write(String logString) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(LogName, Context.MODE_PRIVATE);
            outputStream.write(logString.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e)        {
            System.out.println("LogFile: " + e.getMessage());
        }
    }

    public void Clear(){
        Boolean fileDeleted = file.delete();
        if (fileDeleted) {
            try {
                Boolean fileCreated = file.createNewFile();
                if (!fileCreated) file = null;
            } catch (Exception e) {
                System.out.println("LogFile: " + e.getMessage());
            }
        } else file = null;
    }
}
