package wefit.com.wefit.utils.userhandling;

import wefit.com.wefit.pojo.User;

/**
 * Created by gioacchino on 26/11/2017.
 */

public class DefaultUserFiller {
    private static final DefaultUserFiller ourInstance = new DefaultUserFiller();

    public static DefaultUserFiller getInstance() {
        return ourInstance;
    }

    private DefaultUserFiller() {
    }

    public User fillNewUserWithDefaultValues(User newUser) {

        // TODO retrieve from strings
        String defaultImage = "iVBORw0KGgoAAAANSUhEUgAAAKAAAAB4CAIAAAD6wG44AAAAA3NCSVQICAjb4U/gAAAD5ElEQVR4\nnO2dzU4TURiG328oiJg0caFgu5UoS4WEsKHxDgjsuAfkWhDdaOI1iF6EIbEY124Bf7aEBdDO5wIw\nEXuaOWX++uZ9dmTOvGd6nk5Pydc5x6anpxFga2srdGggu7u7Ue0H5pvDLQF6AIAk9/ww6avXb8xh\nnrilBeQXff2D8xtRESXglgKJo2HAtea/ZBr30do7ppIUaQLz2F5qTcPdQ8fMbMjR2zQecoqj8XXu\nc/fBtzSB+T938J1396Ly3+J9zNWcYW0CH3opgGyvo5LxiW1sm5uboXP29/ezX427r6ysRL2Ggfkp\n/OzF6Wnn/OqvsnDDk+/z89+fAr0bU0OISsYnNr/RbrdD5xwdHWXvAECr1YpqPzDf3adOJhNMuMHi\n3vG3whzNZrPdfgQkGd9YlYxPbH6mt6oYXySYHAkmR4LJkWByJJgcCSZHgsmRYHIkmBwJJkeCyZFg\ncoKCY+uX7m5m2duPUB+tFXUbn1C+ra6uDukjeweXfUS1H5jv7r+Xfv149rPkciGAuYPZ2e5c9nJh\nJeMTm291+00WgIO57peH3fIF9z/1eh/72QWPxW+yNAeTI8HkSDA5EkyOBJMjweRIMDkSTI4EkyPB\n5EgwORJMjgSTExQcW7+MZUj+WNSJKxyfKIY9Pnp4eBiVNSQqKv9k5gSzUUn50Gw277ZnspcLqxqf\nqPzG+vp66IRK6pfuPrU2mTyeiIrKhYWFhef3F7MLrmR8YvM1B5MjweRIMDm1E+zX3xxvLLFTAing\nSP5bu2m8qZ1geAL0DGnG1chyw/oAYGmB//pUQe0WQkvML/aS872+4eLGoWK/hXpj6eWiOdwaZS7f\nVDS1u4Pt6g5G2aNs5wDc2Fa6q53gy6UMgbT0a0uuL6DcbgumdoJFvkgwORJMjgSTI8HkSDA5EkxO\nHZ8PVn6O+bqDyZFgciSYHAkmR4LJkWByJJgcCSZHgsmRYHIkmBwJJkeCyZFgciSYnNw2iAawvLwc\n1V75JeTntkG0u29sbEStvqD8EvJz+4gueg8G5Y+WrzmYHAkmR4LJkWByJJgcCSZHgsmRYHIkmBwJ\nJkeCyZFgciSYHOt0OqFjlWxorPx884MPgLv79vZ29j7MbGdnJ/trUH45+cGP6HHZo175w/M1B5Mj\nweRIMDkSTI4EkyPB5EgwORJMjgSTI8HkSDA5EkyOBJMTfHzU3Y+Pj6PKVa1WK6ocpvwS8oMbRF/W\nFzOmXzJC/VL5RecH9y4cYQ/5Quujyh8tX3MwORJMjgSTI8HkSDA5EkyOBJMjweRIMDkSTI4EkyPB\n5EgwOXq6kDxfzweT5+sOJs/XHEzOH/3BqRFWGcG3AAAAAElFTkSuQmCC\n";
        String defaultBio = "Just registered!";

        newUser.setPhoto(defaultImage);
        newUser.setBiography(defaultBio);

        return newUser;

    }
}
