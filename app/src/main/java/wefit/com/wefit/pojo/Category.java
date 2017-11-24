package wefit.com.wefit.pojo;

/**
 * Created by lorenzo on 11/4/17.
 */

public class Category {


    /**
     * This is the category displayName in the cloud
     */
    private String displayName;

    /**
     * Category displayName in the local system
     */
    private int image;

    public Category(String name, int imageRef) {
        this.displayName = name;
        this.image = imageRef;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "Category{" +
                "displayName='" + displayName + '\'' +
                ", image=" + image +
                '}';
    }
}
