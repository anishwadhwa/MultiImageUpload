package quicksell.galleryupload;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import quicksell.galleryupload.Utils.S3Constants;
import quicksell.galleryupload.Utils.S3Utils;
import quicksell.galleryupload.imageupload.ProgressRequestBody;
import quicksell.galleryupload.imageupload.RetrofitService;
import quicksell.galleryupload.imageupload.UploadImage;
import quicksell.galleryupload.imageupload.WebServiceInterface;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rv_images)
    RecyclerView rvImages;

    ImagesAdapter imagesAdapter;
    WebServiceInterface retrofitInterface;

    ArrayList<UploadImage> uploadImages;

    private static final String TAG = MainActivity.class.getSimpleName();

    private TransferUtility transferUtility;
    ArrayList<TransferObserver> observers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkForPermissions();
        retrofitInterface = RetrofitService.getRetrofitClient();

        transferUtility = S3Utils.getTransferUtility(this);
        observers = (ArrayList<TransferObserver>)transferUtility.getTransfersWithType(TransferType.UPLOAD);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
     * Begins to upload the file specified by the file path.
     */
    private void beginUpload(String filePath, int position) {
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);
        observers.set(position,transferUtility.upload(S3Constants.BUCKET_NAME, file.getName(),
                file));

        if (TransferState.WAITING.equals(observers.get(position).getState())
                || TransferState.WAITING_FOR_NETWORK.equals(observers.get(position).getState())
                || TransferState.IN_PROGRESS.equals(observers.get(position).getState())) {
            observers.get(position).setTransferListener(new UploadListener());
        }
        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
        // observer.setTransferListener(new UploadListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == 1){
                if(null != data){
                    uploadImages = new ArrayList<UploadImage>();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    if(data.getData()!=null){

                        Uri mImageUri=data.getData();

                        // Get the cursor
                        Cursor cursor = getContentResolver().query(mImageUri,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String imageEncoded  = cursor.getString(columnIndex);
                        UploadImage image = new UploadImage();
                        image.setImagePath(imageEncoded);
                        uploadImages.add(image);
                        cursor.close();

                    }else {
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                mArrayUri.add(uri);
                                // Get the cursor
                                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                // Move to first row
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String imageEncoded  = cursor.getString(columnIndex);
                                UploadImage image = new UploadImage();
                                image.setImagePath(imageEncoded);
                                uploadImages.add(image);
                                cursor.close();
                            }
                            Log.v(TAG, "Selected Images" + mArrayUri.size());
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this,"still fucked", Toast.LENGTH_SHORT).show();
                }

                loadRecyclerView();

            }
        }

    }

    private void loadRecyclerView() {
        imagesAdapter = new ImagesAdapter(MainActivity.this,uploadImages);
        rvImages.setAdapter(imagesAdapter);
        RecyclerView.LayoutManager manager1 = new GridLayoutManager(MainActivity.this, 2);
        rvImages.setLayoutManager(manager1);
        rvImages.setHasFixedSize(true);

        startUploadingImages();
    }

    private void startUploadingImages() {
        for (int i =0; i < uploadImages.size(); i++) {
            uploadImages.get(i).setBeingUploaded(true);
            beginUpload(uploadImages.get(i).getImagePath(), i);
        }
    }

    private void checkForPermissions() {
        try {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE};

            //here check for runtime gps permission
            int hasReadPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                    permissions[1]);
            int hasWritePermission = ContextCompat.checkSelfPermission(MainActivity.this,
                    permissions[0]);

            if (hasWritePermission !=
                    PackageManager.PERMISSION_GRANTED || hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, permissions, 111);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 111: {
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        checkForPermissions();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    checkForPermissions();
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private class UploadListener implements TransferListener {

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
            for (TransferObserver observer : observers) {
                if(observer.getId() == id){
                    updateProgress(observer);
                }
            }
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d(TAG, "onStateChanged: " + id + ", " + newState);
        }
    }

    private void updateProgress(TransferObserver observer) {
        int position = observers.indexOf(observer);
        int progress = (int) ((double) observer.getBytesTransferred() * 100 / observer
                .getBytesTotal());
        uploadImages.get(position).setProgress(progress);
        imagesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (observers != null && !observers.isEmpty()) {
            for (TransferObserver observer : observers) {
                observer.cleanTransferListener();
            }
        }
    }
}
