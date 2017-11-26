package wefit.com.wefit.utils.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by gioacchino on 25/11/2017.
 */

public class ImageBase64Marshaller {


    /**
     * Generate a bitmap image from a BASE64 encoded string (serialized)
     *
     * @param encodedImage serialized string
     * @return decoded image
     */
    public static Bitmap decodeBase64BitmapString(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    /**
     * Generate a serialized string of the image (BASE 64)
     *
     * @param imageToEncode image to serialize
     * @return serialized string
     */
    public static String encodeBase64BitmapString(Bitmap imageToEncode) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageToEncode.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private ImageBase64Marshaller() {
    }
}
