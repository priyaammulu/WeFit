package wefit.com.wefit.pojo;

/**
 * Created by lorenzo on 11/4/17.
 */

public class Category {


    /**
     * This is the category id
     */
    private String name;
    private int image;

    public Category(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
