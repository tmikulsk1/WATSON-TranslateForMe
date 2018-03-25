package com.example.tmikulsk1.watsontranslateforme;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmikulsk1 on 23/03/2018.
 */

public final class Utils {

    public static final String LOG_TAG = Utils.class.getName();

    private Utils() {
    }

    public static String fetchTranslation(String requestURL) {

        String finalURL = requestURL.replaceAll(" ", "%20");
        URL url = createUrl(finalURL);
        String httpResponse = null;

        try {

            httpResponse = makeHttpRequest(url);

        } catch (IOException e) {

            Log.e(LOG_TAG, "Problem with HTTP request: ", e);

        }

        return httpResponse;

    }

    public static List<AvailableLanguages> fetchLanguages(String requestURL) {

        URL url = createUrl(requestURL);
        String jsonResponse = null;

        try {

            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {

            Log.e(LOG_TAG, "Problem with HTTP request: ", e);

        }

        List<AvailableLanguages> availableLanguages = extractAvailableLanguages(jsonResponse);

        return availableLanguages;

    }

    public static URL createUrl(String requestURL) {

        URL url = null;

        try {

            url = new URL(requestURL);

        } catch (MalformedURLException e) {

            Log.e(LOG_TAG, "Problem with URL: ", e);

        }

        return url;

    }

    public static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = null;

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {

            //TEST IMPLEMENT
            /*if (urlConnection != null){
                String ver = "true";
            }*/

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {

                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);

            } else {

                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());

            }

        } catch (IOException e) {

            Log.e(LOG_TAG, "Problem retrieving data: ", e);

        } finally {
            if (urlConnection != null) {

                urlConnection.disconnect();

            }
            if (inputStream != null) {

                inputStream.close();

            }
        }

        return jsonResponse;
    }

    public static String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();

        if (inputStream != null) {

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            while (line != null) {

                output.append(line);
                line = reader.readLine();

            }

        }

        return output.toString();
    }

    public static List<AvailableLanguages> extractAvailableLanguages(String availableLanguagesJSON) {

        if (TextUtils.isEmpty(availableLanguagesJSON)) {
            return null;
        }

        List<AvailableLanguages> availableLanguages = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(availableLanguagesJSON);
            JSONArray availableLanguageArray = baseJsonResponse.getJSONArray("languages");

            for (int i = 0; i < availableLanguageArray.length(); i++) {

                JSONObject currentAvailableLanguage = availableLanguageArray.getJSONObject(i);

                String language = currentAvailableLanguage.getString("name");

                AvailableLanguages aLanguages = new AvailableLanguages(language);
                availableLanguages.add(aLanguages);

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the results: ", e);
        }

        return availableLanguages;
    }

}
