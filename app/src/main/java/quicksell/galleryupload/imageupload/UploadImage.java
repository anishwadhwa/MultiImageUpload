package quicksell.galleryupload.imageupload;

/**
 * Created by anish wadhwa on 5/22/2017.
 */

public class UploadImage {

    String imagePath;
    boolean isBeingUploaded;
    int progress;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isBeingUploaded() {
        return isBeingUploaded;
    }

    public void setBeingUploaded(boolean beingUploaded) {
        isBeingUploaded = beingUploaded;
    }
}
