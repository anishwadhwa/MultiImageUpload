package quicksell.galleryupload.imageupload;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by anish wadhwa on 5/22/2017.
 */

public interface WebServiceInterface {
    @Multipart
    @POST("/{bucket}/{folder}")
    Call<JsonObject> uploadImage(@Part MultipartBody.Part file, @Path("bucket") String bucket, @Path("folder") String folder);
}
