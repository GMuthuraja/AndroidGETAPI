package app.example.app.androidgetapi;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    int CurrentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
    }


    private class fetchData extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String results = null;

            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;

                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0){
                    return null;
                }

                results = buffer.toString();
            }catch (Exception exception){
                Log.e("Error-1", "doInBackground",exception);
            }finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try {
                        reader.close();
                    }catch (IOException e){
                        Log.e("Error-2", "doInBackground",e);
                    }
                }
            }
            return results;
        }

        @Override
        protected void onPostExecute(String s){
            try{
                JSONObject jObject = new JSONObject(s);
                JSONArray jArray = jObject.getJSONArray("data");

                if(jArray.length() != 0){
                    textView.setText(
                            "Cuurent Page: "+jObject.getString("page")+"\nName is: "
                                    +jArray.getJSONObject(0).getString("first_name"));
                }else{
                    Toast.makeText(getApplicationContext(), "No more data found!", Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e){
                Log.e("Error-1", "onPostExecute",e);
            }
        }
    }


    public void getNextValue(View v){
        new fetchData().execute(new String[]{"https://reqres.in/api/users?page="+CurrentPage});
        CurrentPage++;
    }

    public void getPrevValue(View v){
        CurrentPage--;
        new fetchData().execute(new String[]{"https://reqres.in/api/users?page="+CurrentPage});
    }
}
