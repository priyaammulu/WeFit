package wefit.com.wefit.utils.eventutils.category;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Category;

/**
 * Created by gioacchino on 21/11/2017.
 * This Factory is responsible to manage access to categories
 */

public class CategoryIconFactory {
    /**
     * Categories
     */
    private Map<String, Category> availableCategories = new HashMap<>();
    /**
     * Instance of the factory
     */
    private static final CategoryIconFactory ourInstance = new CategoryIconFactory();

    public static CategoryIconFactory getInstance() {
        return ourInstance;
    }

    private CategoryIconFactory() {

        this.availableCategories.put("archery", new Category("Archery", R.drawable.ic_archery, "archery"));
        this.availableCategories.put("bike", new Category("Bike", R.drawable.ic_bike, "bike"));
        this.availableCategories.put("running", new Category("Running", R.drawable.ic_running, "running"));
        this.availableCategories.put("football", new Category("Football", R.drawable.ic_football, "football"));
        this.availableCategories.put("volleyball", new Category("Volleyball", R.drawable.ic_volleyball, "volleyball"));
        this.availableCategories.put("cardio", new Category("Cardio", R.drawable.ic_gym_cardio, "cardio"));
        this.availableCategories.put("wlift", new Category("Weightlifting", R.drawable.ic_gym_weightlifting, "wlift"));

    }

    /**
     * Retrieve the category knowing the ID
     */
    public Category getCategoryByID(String categoryID) {
        return this.availableCategories.get(categoryID);
    }

    /**
     * Retrieve all the stored categories
     * @return categories
     */
    public Map<String, Category> getAvailableCategories() {
        return availableCategories;
    }
}
