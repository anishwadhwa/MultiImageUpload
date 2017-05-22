package quicksell.galleryupload.Utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by anish wadhwa on 5/22/2017.
 */

public class Util {

    public static Uri getImageUri(Context context, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, Const.DEFAULT_IMAGE_NO_COMPRESSION, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Context context, Uri contentUri)
    {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /*public static int uploadImageToS3(Context context, String uploadType, String localImagePath,
                                      URL uploadURL, int maxHeight, int maxWidth){
        try {
            if(maxHeight == 0) maxHeight = Const.MAX_HEIGHT;
            if(maxWidth == 0) maxWidth = Const.MAX_WIDTH;

            HttpURLConnection connection = (HttpsURLConnection) uploadURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "image/jpeg");

            OutputStream output = connection.getOutputStream();
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(localImagePath, maxWidth, maxHeight, ScalingUtilities.ScalingLogic.FIT);
            Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, maxWidth, maxHeight,
                    ScalingUtilities.ScalingLogic.FIT);
            unscaledBitmap.recycle();

            Log.d(TAG, "image height is : " + scaledBitmap.getHeight());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int compressionQuality = Const.DEFAULT_IMAGE_COMPRESSION;

            switch(uploadType){
                case Const.S3_PHOTO:
                    compressionQuality = RiderUtils.getSharedPrefs(context).getInt(Const.POC_IMAGE_QUALITY,
                            Const.DEFAULT_IMAGE_COMPRESSION);
                    break;
                case Const.S3_SIGNATURE:
                    compressionQuality = RiderUtils.getSharedPrefs(context).getInt(Const.SIGNATURE_IMAGE_QUALITY,
                            Const.DEFAULT_IMAGE_COMPRESSION);
                    break;
            }

            if(compressionQuality == 0) compressionQuality = Const.DEFAULT_IMAGE_COMPRESSION;

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, out);
            output.write(out.toByteArray());
            output.flush();
            return connection.getResponseCode();
        }catch (Exception e){
            e.printStackTrace();
            Crashlytics.logException(e);
            Log.d(TAG, "Exception aagayi");
        }
        return 0;
    }*/
}
