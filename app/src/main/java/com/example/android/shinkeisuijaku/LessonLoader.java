//package com.example.android.shinkeisuijaku;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * Created by Nathan Henninger on 2017.05.04.
// * https://github.com/nhenninger
// * nathanhenninger@u.boisestate.edu
// */
//
//public class LessonLoader {
//    private static final String TAG = "LessonLoader";
//    private static final String SYLLABARY = "syllabary";
//    private static final String KANJI = "kanji";
//
//    public List<Card> loadCards(Context context, int lessonNum) {
//        StringBuilder builder = new StringBuilder();
//        try {
//            String fileName = "lesson" + lessonNum + ".json";
//            InputStream json = context.getAssets().open(fileName);
//            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
//
//            String str;
//            while ((str = in.readLine()) != null) {
//                builder.append(str);
//            }
//            in.close();
//
//            return parseCards(builder.toString(), lessonNum);
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//            Log.e(TAG, "loadCards: ", e);
//        }
//        return null;
//    }
//
//    private List<Card> parseCards(String str, int lessonNum)
//            throws IOException, JSONException {
//
//        JSONObject jsonObject = new JSONObject(str);
//        Gson gson = new GsonBuilder().create();
//        JSONArray jsonArray;
//        if (lessonNum == 1 || lessonNum == 2) {
//            jsonArray = jsonObject.getJSONArray(SYLLABARY);
//        } else if (lessonNum >= 3 && lessonNum > MainActivity.NUMBER_OF_LESSONS) {
//            jsonArray = jsonObject.getJSONArray(KANJI);
//        } else {
//            throw new IllegalArgumentException("Lesson outside number range");
//        }
//        return new ArrayList<>(Arrays
//                .asList(gson.fromJson(jsonArray.toString(), Card[].class)));
//    }
//}
