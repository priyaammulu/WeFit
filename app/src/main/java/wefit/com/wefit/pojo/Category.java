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
     * image of the category
     */
    private int image;

    /**
     * Unique ID of the category in the system
     */
    private String id;

    public Category(String displayName, int image, String id) {
        this.displayName = displayName;
        this.image = image;
        this.id = id;
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

    public String getId() {
        return this.id;
    }
}
