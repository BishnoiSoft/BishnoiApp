package com.bss.bishnoi.http;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpPostAsyncTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        String urlString = params[0];
        String postData = params[1];

        try {
            // Create a URL object
            URL url = new URL(urlString);

            // Create a HttpURLConnection object
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            urlConnection.setRequestMethod("POST");

            // Set the content type of the request to json
            urlConnection.setRequestProperty("Content-Type", "application/json");

            // Enable output (sending data to the server)
            urlConnection.setDoOutput(true);

            // Create a stream to write the data to the server
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(postData.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            // Get the response from the server
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String response = bufferedReader.readLine();
            bufferedReader.close();
            inputStream.close();

            // Return the response to onPostExecute
            return response;

        } catch (IOException e) {
            e.printStackTrace();
        }

        // If an error occurred, return null
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // Handle the response from the server here
    }
}

