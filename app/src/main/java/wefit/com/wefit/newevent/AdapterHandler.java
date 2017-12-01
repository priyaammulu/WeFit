package wefit.com.wefit.newevent;

import android.graphics.drawable.Drawable;

import wefit.com.wefit.pojo.Category;

/**
 * Created by lorenzo on 11/16/17.
 * This interface is used to let fragment communicate with the Adapter
 */

public interface AdapterHandler {
    /**
     * The category selected by the user is passed through
     */
    void onItemClick(Category category);

    /**
     * Returns the Drawable associated to a resource id
     */
    Drawable getDrawable(int drawable);
}
