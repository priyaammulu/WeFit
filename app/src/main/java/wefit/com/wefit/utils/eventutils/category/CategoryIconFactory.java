package wefit.com.wefit.utils.eventutils.category;

import java.util.HashMap;
import java.util.Map;

import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Category;

/**
 * Created by gioacchino on 21/11/2017.
 */

public class CategoryIconFactory {

    private Map<String, Category> availableCategories = new HashMap<>();

    private static final CategoryIconFactory ourInstance = new CategoryIconFactory();

    public static CategoryIconFactory getInstance() {
        return ourInstance;
    }

    private CategoryIconFactory() {

        // TODO si può caricare meglio sta cosa
        this.availableCategories.put("volleyball", new Category("Volleyball", R.drawable.ic_volleyball));
        this.availableCategories.put("cardio", new Category("Cardio", R.drawable.ic_gym_cardio));
        this.availableCategories.put("wlift", new Category("Weightlifting", R.drawable.ic_gym_weightlifting));

    }

    /**
     * Retrieve the category knowing the ID
     */
    public Category getCategoryByID(String categoryID) {
        return this.availableCategories.get(categoryID);
    }
}
