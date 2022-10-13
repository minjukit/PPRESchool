package org.tensorflow.lite.examples.classification;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MyDictionaryRequest extends AsyncTask<String, Integer, String> {



    String myurl;
    public static String sentence = "";
    public static Context context;

    public MyDictionaryRequest(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        myurl = params[0];

        try {
            URL url = new URL(myurl);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept","application/json");


            // read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();

        }
        catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //Toast.makeText(context,s, Toast.LENGTH_LONG).show();

        try
        {

            JSONArray js = new JSONArray(s);
            JSONObject mm = js.getJSONObject(0);

            JSONArray meanings = mm.getJSONArray("meanings");

            JSONObject definition = meanings.getJSONObject(0);
            JSONArray definitions = definition.getJSONArray("definitions");

            JSONObject example = definitions.getJSONObject(0);
            String ss = example.getString("example");

            sentence = ss;

        }catch (JSONException e) {
            e.printStackTrace();
            sentence = "";
        }

    }
}