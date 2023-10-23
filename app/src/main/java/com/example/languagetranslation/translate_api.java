package com.example.languagetranslation;

import android.os.AsyncTask;
// This is the library which is used for the purpose of performing the background task in the android application
import android.util.Log;

import org.json.JSONArray;

import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class translate_api extends AsyncTask<String, String, String> {
    private OnTranslationCompleteListener listener;

    @Override
    protected String doInBackground(String... strings) {
        String[] strArr = strings;
//        an internal array is intialized which will be used for the purpose of having all the strings which will be given as arguements in the functions
//        This array will have three strings
//        first one will be the text that we have to make translation
//        second string will give us the text or the code from which we have to perform translation from
//        Third string is the target string where we have to perform the text translation into

        String str = "";

        try {
            String encode = URLEncoder.encode(strArr[0], "utf-8");
//            Now the text which is recived as input from the text field will now be used for the purpose of translation
//            That text is now encoded into utf 8 encoding system

            StringBuilder urlBuilder = new StringBuilder();
//            new stringbuilder object is created which will make the whole url based on all the in[uts that are taken into the string

            urlBuilder.append("https://translate.googleapis.com/translate_a/single?client=gtx&sl=");
            urlBuilder.append(strArr[1]);
            urlBuilder.append("&tl=");
            urlBuilder.append(strArr[2]);
            urlBuilder.append("&dt=t&q=");
//            Now the url has source and target language associated with it and hence we can use that url further
            urlBuilder.append(encode);
//            After all that whole encoded string which was taken as input is now appended to the strings which can be given to the client

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlBuilder.toString())
                    .build();
//            The object of the http client has been created and then a request is made
            Response response = client.newCall(request).execute();
//            response is asked from the API and hence it can be used for further process.

            if (response.isSuccessful()) {
//                if we have got any of the response from the API
                ResponseBody responseBody = response.body();
//                whole response is stored in the the form of whole body
                if (responseBody != null) {
                    String responseBodyString = responseBody.string();
                    responseBody.close();
//                    it is converted into string and then that body is closed

                    JSONArray jsonArray = new JSONArray(responseBodyString).getJSONArray(0);
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONArray jsonArray2 = jsonArray.getJSONArray(i);
                        result.append(jsonArray2.get(0).toString());
                    }
                    return result.toString();
//                    result is created and it is converted into strings
                }
            } else {
                throw new IOException("HTTP request failed: " + response.code() + " " + response.message());
            }
        } catch (Exception e) {
            Log.e("TranslateAPI", e.getMessage());
            if (listener != null) {
                listener.onError(e);
            }
            return str;
        }
        return str;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (listener != null) {
            listener.onStartTranslation();
        }
    }

    @Override
    protected void onPostExecute(String text) {
        if (listener != null) {
            listener.onCompleted(text);
        }
    }

    public interface OnTranslationCompleteListener {
        void onStartTranslation();
        void onCompleted(String text);
        void onError(Exception e);
    }

    public void setOnTranslationCompleteListener(OnTranslationCompleteListener listener) {
        this.listener = listener;
    }
}
