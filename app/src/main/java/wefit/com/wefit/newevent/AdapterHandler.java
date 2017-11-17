package wefit.com.wefit.newevent;

import android.graphics.drawable.Drawable;

import wefit.com.wefit.pojo.Category;

/**
 * Created by lorenzo on 11/16/17.
 */

public interface AdapterHandler {
    void onItemClick(Category category);

    Drawable getDrawable(int drawable);
}
