package quicksell.galleryupload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import quicksell.galleryupload.Utils.Util;
import quicksell.galleryupload.imageupload.UploadImage;

/**
 * Created by anish wadhwa on 5/22/2017.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    ArrayList<UploadImage> images;
    Context context;
    public ImagesAdapter(Context context, ArrayList<UploadImage> images){
        this.images =   images;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_image, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap myBitmap = BitmapFactory.decodeFile(images.get(position).getImagePath().replace("file://",""));
        holder.ivUploadImage.setImageBitmap(myBitmap);
        holder.ivUploadImage.setAdjustViewBounds(true);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivUploadImage;
        TextView tvUploadProgress;
        View vTransparency;

        public ViewHolder(View itemView) {
            super(itemView);
            ivUploadImage = (ImageView)itemView.findViewById(R.id.iv_upload_image);
            tvUploadProgress = (TextView)itemView.findViewById(R.id.tv_progress);
            vTransparency = itemView.findViewById(R.id.view_transparent);
        }
    }
}
