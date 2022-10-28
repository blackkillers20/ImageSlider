package com.example.imageslider;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imageslider.RoomDatabase.DatabaseHelper;
import com.example.imageslider.RoomDatabase.ImageDatabase;
import com.example.imageslider.RoomDatabase.ImageEntity;
import com.example.imageslider.Ultis.AutoFitGridLayoutManager;
import com.example.imageslider.Ultis.ImageUtils;
import com.example.imageslider.Ultis.PermissionUtils;
import com.example.imageslider.Ultis.ViewUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private File capturedImageFile;
    private RecyclerView recyclerView;
    private List<File> fileList = new ArrayList<File>();
    private ImageAdapter imageAdapter;
    private LoaderManager loaderManager;
    public ImageDatabase imageDatabase;
    private final LoaderManager.LoaderCallbacks<List<File>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<File>>() {
        @NonNull
        @Override
        public Loader<List<File>> onCreateLoader(int id, @Nullable Bundle args) {
            return new AsyncTaskLoader<List<File>>(MainActivity.this)
            {
                @Override
                public void onStartLoading()
                {
                    forceLoad();
                }
                @Nullable
                @Override
                public List<File> loadInBackground() {
                    return ImageUtils.from(MainActivity.this).files().getFiles();
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<File>> loader, List<File> data) {
                fileList = data;
                imageAdapter = new ImageAdapter(fileList);
                LoadRecycleView();
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<File>> loader) {
                imageAdapter.setListImages(null);
        }
    };

    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // get captured image
                    assert result.getData() != null;
                    ViewUtils.showSnackbar(MainActivity.this, "Successful!");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loaderManager = LoaderManager.getInstance(MainActivity.this);
        loaderManager.initLoader(101, null, loaderCallbacks);

        imageDatabase = DatabaseHelper.getDatabase(this);


    }

    @Override
    public void onResume() {

        super.onResume();
        loaderManager.restartLoader(101, null, loaderCallbacks);
    }

    private void LoadRecycleView()
    {
        recyclerView = findViewById(R.id.recyclerImages);
        imageAdapter.setListener((view, position)->{
            ViewUtils.showSnackbar(this, "Clicked on position" + position);
        });
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(new AutoFitGridLayoutManager(this, 500));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.IdAddUrl)
        {
            ViewUtils.showInputDialog(this, "Add an URL", "https://", "", new ViewUtils.InputDialogCallback() {
                @Override
                public void onConfirm(String input) throws Exception {
                    ImageUtils.from(MainActivity.this).load(input).onDetails((uri) -> {
                        ImageEntity imageEntity = new ImageEntity(new File(uri).getName(), uri);
                        imageDatabase.getImageDAO().insert(imageEntity);
                    }).asFile();

                    ViewUtils.showSnackbar(MainActivity.this, "Image added to local and Database!");
                }

                @Override
                public void onException(Exception e) {
                    ViewUtils.showDialog(MainActivity.this, e.getClass().getSimpleName(), "Error: " + e.getMessage());
                }
            }, (input)->{
                if (input.getText().toString().isEmpty())
                {
                    input.setError("Required Field!");
                    return false;
                }
                else if (!Patterns.WEB_URL.matcher(input.getText().toString()).matches()) {
                    input.setError("The link is not valid.");
                    return false;
                }
                else
                {return true;}
            });
        }
        if (id == R.id.IdSlider)
        {
            Intent intent = new Intent(this, ImageActivity.class);
            startActivity(intent);
        }
        if (id == R.id.IdCamera)
        {
            PermissionUtils.checkPermission(this, "android.permission.CAMERA", new PermissionUtils.PermissionAskListener(){

                @Override
                public void onPermissionAsk() {
                    PermissionUtils.requestPermission(MainActivity.this, "android.permission.CAMERA", 100 );
                }

                @Override
                public void onPermissionPreviouslyDenied() {
                    PermissionUtils.requestPermission(MainActivity.this, "android.permission.CAMERA", 100 );
                }

                @Override
                public void onPermissionDisabled() {
                    ViewUtils.showToast(MainActivity.this, "PERMISSION DISABLED");
                }

                @Override
                public void onPermissionGranted() {
                    capturedImageFile = new File(getFilesDir(), ImageUtils.DEFAULT_IMAGE_NAME);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            FileProvider.getUriForFile(MainActivity.this,
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    capturedImageFile));
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    cameraLauncher.launch(intent);
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isURL(String url)
    {
        Pattern pattern = Patterns.WEB_URL;
        Matcher matcher = pattern.matcher(url.toLowerCase());
        return matcher.matches();

    }
}