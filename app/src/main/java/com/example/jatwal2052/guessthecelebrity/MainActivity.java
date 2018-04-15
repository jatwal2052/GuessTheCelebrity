package com.example.jatwal2052.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    Button button0,button1,button2,button3;
    ImageView imageView;
    ArrayList<String> urls = new ArrayList<String>();
    ArrayList<String> names = new ArrayList<String>();
    int celebrity ;
    GridLayout gridLayout;
    String answers[] = new String[4];
    int r;
    public void chose(View view){
        int gettag = Integer.parseInt(view.getTag().toString());
        if(r == gettag){
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Wrong! Person is " + names.get(celebrity)  , Toast.LENGTH_SHORT).show();
        }
        complete();
    }

    public void complete(){
        Random rd =new Random();
        celebrity = rd.nextInt(urls.size());
        r = rd.nextInt(4);
        DownloadImage image = new DownloadImage();
        Bitmap bitmap = null;
        try {
            bitmap = image.execute(urls.get(celebrity)).get();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        imageView.setImageBitmap(bitmap);
        for(int i=0 ;i<4;i++){
            if(r == i){
                answers[i]=names.get(celebrity);
            }
            else{
                int incorrectanswer = rd.nextInt(urls.size());
                while(incorrectanswer == celebrity){
                    incorrectanswer=rd.nextInt(urls.size());
                }
                answers[i]=names.get(incorrectanswer);
            }
        }
        button0.setText(answers[0]);
        button1.setText(answers[1]);
        button2.setText(answers[2]);
        button3.setText(answers[3]);


    }

    public class Download extends AsyncTask<String ,Void ,String>{

        @Override
        protected String doInBackground(String... params) {
            URL url ;
            HttpURLConnection httpURLConnection = null;
            try {
                url =new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                String result = "";
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while(data != -1){
                    char s = (char) data;
                    result += s;
                    data = inputStreamReader.read();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "failed";
        }
    }

    public class DownloadImage extends AsyncTask<String ,Void ,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url =new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        button0 = (Button)findViewById(R.id.button0);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        imageView = (ImageView)findViewById(R.id.imageView);
        String result = "";
        Download task = new Download();
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
       // Log.i("result",result);

        String substring[] = result.split("<div class=\"sidebarContainer\">");
        Pattern p = Pattern.compile("img src=\"(.*?)\"");
        Matcher m = p.matcher(substring[0]);
        while(m.find()){
            urls.add(m.group(1));
            Log.i("urls",m.group(1));
        }
        p = Pattern.compile("alt=\"(.*?)\"");
        m = p.matcher(substring[0]);
        while(m.find()){
            names.add(m.group(1));
            System.out.println(m.group(1));;
        }
        complete();

    }
}
