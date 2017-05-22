package quicksell.galleryupload.imageupload;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.String.format;

/**
 * Created by anish wadhwa on 5/22/2017.
 */

public class RetrofitService {

    private RetrofitService(){}

    public static WebServiceInterface getRetrofitClient(){
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("https://s3.amazonaws.com/");

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client =
                    new OkHttpClient.Builder()
                            .addInterceptor(interceptor).build();

            builder.client(client);

        return builder.build().create(WebServiceInterface.class);
    }
}
