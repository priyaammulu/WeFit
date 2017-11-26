package wefit.com.wefit.newevent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.util.ArrayList;

import wefit.com.wefit.R;
import wefit.com.wefit.WefitApplication;
import wefit.com.wefit.pojo.Category;
import wefit.com.wefit.pojo.Event;
import wefit.com.wefit.utils.eventutils.category.CategoryIconFactory;
import wefit.com.wefit.utils.image.ImageBase64Marshaller;
import wefit.com.wefit.viewmodels.UserViewModel;

public class NewEventFragmentSecondPage extends Fragment implements AdapterHandler {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int RESULT_OK = -1;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private NewFragmentListener mListener;
    private Button mButtonFinish;
    private ImageView mImage;
    private Category mCategory;
    private ScrollView scrollView;
    private UserViewModel mUserViewModel;

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

    private void bind(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.category_event_recycler_view);
        initRecyclerView();
        mButtonFinish = (Button) view.findViewById(R.id.new_event_finish);


        mImage = (ImageView) view.findViewById(R.id.new_event_image);

        // listener, take picture
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        // TODO devi fare controlli sulla lunghezza oppure ci sta in XML mDescription = (EditText) view.findViewById(R.id.new_event_description);

        // category picker
        scrollView = (ScrollView) view.findViewById(R.id.new_event_scrollview) ;

        // button finish
        mButtonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Event newEvent = mListener.getEvent();

                // set creator ID
                newEvent.setAdminID(mUserViewModel.retrieveCachedUser().getId());

                // take encode image
                Bitmap bitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
                newEvent.setImage(ImageBase64Marshaller.encodeBase64BitmapString(bitmap));

                newEvent.setCategoryID(mCategory.getId());

                mListener.thirdFragment(newEvent);
            }
        });
    }

    private void initRecyclerView() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CategoryAdapter(new ArrayList<>(CategoryIconFactory.getInstance().getAvailableCategories().values()), this);
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

    public Drawable getDrawable(int drawable) {
        return getResources().getDrawable(drawable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // result for the image capture
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImage.setImageBitmap(imageBitmap);
        }
    }
}
