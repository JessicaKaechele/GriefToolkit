package com.example.memorytracker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Utils {
    public static int convertDpToPx(Context context, float dp) {
        //return (int) (dp * context.getResources().getDisplayMetrics().density);
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static String readFromFile(Context context, String filename) throws IOException {
        String result = "";

        InputStream inputStream = context.openFileInput(filename);

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String temp = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((temp = bufferedReader.readLine()) != null) {
                stringBuilder.append(temp);
                stringBuilder.append("\n");
            }

            inputStream.close();
            result = stringBuilder.toString();
        }
        return result;
    }

    public static void writeToFile(Context context, Object data, String filename) {
        Gson gson = new GsonBuilder().create();
        String jsonData = gson.toJson(data);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename,
                    Context.MODE_PRIVATE));
            outputStreamWriter.write(jsonData);
            outputStreamWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> fromJson(String data) {
        ArrayList<String> list;
        if (data == null) {
            list = new ArrayList<>();
        } else {
            list = new Gson().fromJson(data, ArrayList.class);
        }
        return list;
    }

    public static String copyFileToInternalStorage(Context context, Uri uri, String dirName, String filename) {
        String newFilePath = null;
        InputStream data = null;
        try {
            String newFilePathDirectory = makeDirectory(context, dirName);
            newFilePath = newFilePathDirectory + "/" + filename;
            data = context.getContentResolver().openInputStream(uri);
            File newFile = new File(newFilePath);
            OutputStream output = new FileOutputStream(newFile);
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = data.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                data.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newFilePath;
    }

    @NonNull
    public static String makeDirectory(Context context, String name) {
        String newFilePathDirectory = context.getFilesDir().getAbsolutePath() + "/" + name;
        File directory = new File(newFilePathDirectory);
        if (!directory.exists()){
            directory.mkdirs();
        }
        return newFilePathDirectory;
    }


    public static String getFileName(Context context, Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            fileName = uri.getLastPathSegment();
        }
        else if (scheme.equals("content")) {
            String[] proj = { MediaStore.MediaColumns.DISPLAY_NAME };
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                fileName = cursor.getString(columnIndex);
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }

    public static boolean deleteFile(String uri){
        File fdelete = new File(uri);

        if (fdelete.exists()) {
            return fdelete.delete();
        }
        return false;
    }

    @NonNull
    public static List<String> getContentStrings(LinkedHashMap<String, LinkedHashMap<String, String>> data) {
        List<String> list = new LinkedList<>();
        Collection<LinkedHashMap<String, String>> allSources = data.values();
        for (LinkedHashMap<String, String> map: allSources){
            list.addAll(map.keySet());
        }
        return list;
    }

}