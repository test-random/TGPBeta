package org.telegram.messenger;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.telegram.messenger.Utilities;
public class HttpGetTask extends AsyncTask<String, Void, String> {
    private Utilities.Callback<String> callback;
    private Exception exception;

    public HttpGetTask(Utilities.Callback<String> callback) {
        this.callback = callback;
    }

    @Override
    public String doInBackground(String... strArr) {
        BufferedReader bufferedReader;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(strArr[0]).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb.append(readLine);
                } else {
                    bufferedReader.close();
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    @Override
    public void onPostExecute(String str) {
        Utilities.Callback<String> callback = this.callback;
        if (callback != null) {
            if (this.exception == null) {
                callback.run(str);
            } else {
                callback.run(null);
            }
        }
    }
}
