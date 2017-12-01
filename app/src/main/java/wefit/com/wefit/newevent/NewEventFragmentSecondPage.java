package wefit.com.wefit.newevent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import wefit.com.wefit.R;
import wefit.com.wefit.WefitApplication;
import wefit.com.wefit.pojo.Category;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.utils.eventutils.category.CategoryIconFactory;
import wefit.com.wefit.utils.image.ImageBase64Marshaller;
import wefit.com.wefit.viewmodels.UserViewModel;

/**
 * Created by lorenzo on 10/28/17.
 * This Fragment is the second step in the creation of an Event flow
 */
public class NewEventFragmentSecondPage extends Fragment implements AdapterHandler {
    /**
     * Constants
     */
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int RESULT_OK = -1;
    /**
     * Recycler View
     */
    private RecyclerView mRecyclerView;
    /**
     * Reference to the activity
     */
    private NewFragmentListener mListener;
    /**
     * Layout
     */
    private ImageView mImage;
    private Category mCategory;
    private UserViewModel mUserViewModel;
    private LinearLayout mImagePickerLabel;
    /**
     * Image picked
     */
    private boolean isImagePicked = false;

    public NewEventFragmentSecondPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mUserViewModel = ((WefitApplication) getActivity().getApplication()).getUserViewModel();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Binds UI views to fields
     */
    private void bind(View view) {

        mImagePickerLabel = (LinearLayout) view.findViewById(R.id.image_picker_label);

        ImageView mBackButton = (ImageView) view.findViewById(R.id.new_event_page2_backbutton);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.category_event_recycler_view);
        initRecyclerView();
        Button mButtonNextPage = (Button) view.findViewById(R.id.new_event_finish);


        mImage = (ImageView) view.findViewById(R.id.new_event_image);

        // listener, take picture
        /*
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        */

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CAPTURE);
            }
        });

        // category picker
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.new_event_scrollview);

        // button finish
        mButtonNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFormFilled()) {
                    Event newEvent = mListener.getEvent();

                    // set creator ID
                    newEvent.setAdminID(mUserViewModel.retrieveCachedUser().getId());

                    // take encode image
                    Bitmap bitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
                    newEvent.setImage(ImageBase64Marshaller.encodeBase64BitmapString(bitmap));

                    newEvent.setCategoryID(mCategory.getId());

                    mListener.thirdFragment(newEvent);
                } else {

                    showRetrieveErrorPopupDialog();

                }
            }


        });
    }

    /**
     * It initializes the recycler view
     */
    private void initRecyclerView() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        RecyclerView.Adapter mAdapter = new CategoryAdapter(new ArrayList<>(CategoryIconFactory.getInstance().getAvailableCategories().values()), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_event_fragment_page_two, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewFragmentListener) {
            mListener = (NewFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NewFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(Category category) {
        mCategory = category;
    }

    @Override
    public Drawable getDrawable(int drawable) {
        return getResources().getDrawable(drawable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // result for the image capture
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImage.setImageBitmap(imageBitmap);

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                mImage.setImageBitmap(bitmap);
                isImagePicked = true;
                mImagePickerLabel.setVisibility(View.GONE);
            } catch (IOException e) {
                Toast.makeText(getContext(), R.string.select_image_error_toast, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Return true if the form is filled
     */
    private boolean isFormFilled() {
        return isImagePicked && mCategory != null;
    }

    /**
     * It shows an error popup dialog
     */
    private void showRetrieveErrorPopupDialog() {
        // there was an error, show a popup message
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.form_not_completely_filled_popup)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, null);
        AlertDialog alert = builder.create();
        alert.show();
    }

}
