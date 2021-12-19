package com.example.memorytracker.memorabilia;

import static com.example.memorytracker.Utils.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.memorytracker.R;
import com.example.memorytracker.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class Memorabilia extends AppCompatActivity {

    public static final String dataFolder = "memorabilia";
    private final String filename = "memorabilia_data.json";

    private ArrayList<String> memorabiliaList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorabilia);

        memorabiliaList = getMemorabiliaList();

        listView = findViewById(R.id.list);
        ImageButton addMemorabiliaButton = findViewById(R.id.addMemorabilie);

        addMemorabiliaButton.setOnClickListener(v -> {
            showBottomSheetDialog();
        });

        updateMemorabiliaList();
    }

    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_memorabilia);

        LinearLayout img = bottomSheetDialog.findViewById(R.id.imgLinearLayout);
        LinearLayout camera = bottomSheetDialog.findViewById(R.id.cameraLinearLayout);

        img.setOnClickListener(v -> {
            String[] t = {"image/*"};
            getMediaContent.launch(t);
            bottomSheetDialog.dismiss();
        });

        camera.setOnClickListener(v -> {
            File imagePath = new File(this.getFilesDir(), dataFolder);
            File newFile = new File(imagePath, new SimpleDateFormat("yyyyMMddHHmmss'.jpg'").format(new Date()));

            Uri uri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider",
                    newFile);;
            captureImage.launch(uri);
            bottomSheetDialog.dismiss();

            saveMemorabilia(String.valueOf(uri));
        });

        bottomSheetDialog.show();

    }

    final ActivityResultLauncher<String[]> getMediaContent = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
            uri -> {
                String filename = getFileName(this, uri);
                String newFilePath = copyFileToInternalStorage(this, uri, dataFolder, filename);
                saveMemorabilia(newFilePath);
                updateMemorabiliaList();
            });

    final ActivityResultLauncher<Uri> captureImage = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            result -> {
                updateMemorabiliaList();
            });

    private void updateMemorabiliaList() {
        ArrayAdapter<String> memorabiliaListAdapter = new CustomListViewAdapter(
                this,
                R.layout.input_view_img,
                R.id.imageView,
                memorabiliaList
        );
        listView.setAdapter(memorabiliaListAdapter);
    }

    private void saveMemorabilia(String memorabilia) {
        if (!memorabiliaList.contains(memorabilia)) {
            memorabiliaList.add(memorabilia);
            writeToFile(this, memorabiliaList, filename);
        }
    }

    public void onDeleteButtonClick(View view) {
        View v = (View) view.getParent();
        ImageView itemTextView = v.findViewById(R.id.imageView);
        String memorabiliaItem = String.valueOf(itemTextView.getTag());

        String uri = String.valueOf(itemTextView.getTag());
        if (uri.contains("content")){
            String filename = Utils.getFileName(this, Uri.parse(uri));
            uri = getFilesDir().getAbsolutePath() + "/" + dataFolder + "/" + filename;
        }
        Utils.deleteFile(uri);
        deleteMemorabilia(memorabiliaItem);

        updateMemorabiliaList();
    }

    private void deleteMemorabilia(String memorabilia) {
        memorabiliaList.remove(memorabilia);
        writeToFile(this, memorabiliaList, filename);
    }

    @NonNull
    private ArrayList<String> getMemorabiliaList() {
        String memorabiliaData = null;
        try {
            memorabiliaData = readFromFile(Memorabilia.this, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fromJson(memorabiliaData);
    }
}