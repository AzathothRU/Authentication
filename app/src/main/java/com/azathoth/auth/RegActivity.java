package com.azathoth.auth;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ViewPropertyAnimatorCompatSet;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegActivity extends AppCompatActivity {

    int status;
    EditText emailText;
    EditText passText;
    EditText userNameText;
    Button regButton;
    TextView jsonText;
    TextView resultText;

    String email;
    String pass;
    String username;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        emailText = (EditText) findViewById(R.id.emailText);
        passText = (EditText) findViewById(R.id.passText);
        userNameText = (EditText) findViewById(R.id.userText);
        regButton = (Button) findViewById(R.id.rebButton);
        regButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                //sendToServer(v);
                String json = sendToServer(v);
                jsonText.setText("JSON: " + json);
            }
        });
        jsonText = (TextView) findViewById(R.id.jsonText);
        resultText = (TextView) findViewById(R.id.resultText);


    }


    public String sendToServer(View v){
        email = emailText.getText().toString();
        pass = passText.getText().toString();
        username = userNameText.getText().toString();
        JSONObject user = new JSONObject();
        JSONObject jsonreq = new JSONObject();

        try {
            user.put("email", email);
            user.put("password", pass);
            user.put("username", username);
            jsonreq.put("user", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonreq.length() > 0){
            new SendJsonDataToServer().execute(String.valueOf(jsonreq));
        }
        String json = jsonreq.toString();
        return  json;
    }

    private class SendJsonDataToServer extends AsyncTask<String,String,String>{
        private static final String TAG = "IOE";

        @Override
        protected String doInBackground(String... params){
            String JsonResponse = null;
            String JsonDATA = params[0];
            HttpURLConnection urlConection = null;
            BufferedReader reader = null;
            CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );

            try {
                URL url = new URL("http://52.196.0.249/frontend/web/api/v1/user/registration/");
                //URL url = new URL("http://52.196.0.249/frontend/web/");
                //Log.i(TAG, urlConection.toString());
                urlConection = (HttpURLConnection) url.openConnection();
                urlConection.setDoOutput(true);
                urlConection.setDoInput(true);
                //urlConection.setRequestMethod("POST");
                urlConection.setRequestProperty("Content-Type", "application/json");
                urlConection.setRequestProperty("Authorization", "Bearer token");
                urlConection.setConnectTimeout(20000);
                urlConection.setReadTimeout(20000);


                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();

                //status = urlConection.getResponseCode();

                reader = new BufferedReader(new InputStreamReader(urlConection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                try{
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null)
                        stringBuilder.append(inputLine + '\n');
                    reader.close();
                    JsonResponse = stringBuilder.toString();
                    return JsonResponse;
                } catch (Exception e){
                    Log.i(TAG, "noanswer");
                    e.printStackTrace();
                }

                Log.i(TAG, JsonResponse);



            } catch (IOException e){
                Log.i(TAG, "1" + e.toString());
                e.printStackTrace();
            }
            finally {
                if (urlConection != null){
                    Log.i(TAG, "noconnect");
                    urlConection.disconnect();
                }
            }
            if(reader !=null){
                try {
                    reader.close();
                } catch (final  IOException e){
                    Log.e(TAG, "Error closing stream", e);
                }
            }
            return JsonResponse;
        }
        @Override
        protected void onPostExecute(String strJson){
            super.onPostExecute(strJson);
            Log.d(TAG, "Response: " + strJson);

            JSONObject dataJsonObj = null;

            try {
                dataJsonObj = new JSONObject(strJson);
                //JSONArray tokenJson = dataJsonObj.getJSONArray("access_token");
                token = dataJsonObj.getString("access_token");
                resultText.setText("token" + token);

            } catch (JSONException e){
                e.printStackTrace();
            }
            //resultText.setText("status: " + status + "     " + "Result: " + strJson);
            //resultText.setText("Result: " + strJson);
        }
    }

}

