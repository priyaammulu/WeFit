package wefit.com.wefit.newevent;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Category;
import wefit.com.wefit.pojo.Event;

import static android.app.Activity.RESULT_OK;


public class NewEventFragmentSecond extends Fragment implements AdapterHandler {
    private static final int IMAGE_RESULT = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<Category> staticCategories = Arrays.asList(
            new Category("Volleyball", R.drawable.ic_volleyball),
            new Category("Cardio", R.drawable.ic_gym_cardio),
            new Category("Cardio", R.drawable.ic_gym_cardio),
            new Category("Weightlifting", R.drawable.ic_gym_weightlifting));

    private NewFragmentListener mListener;
    private Button mButtonFinish;
    private EditText mDescription;
    private ImageView mImage;
    private Category mCategory;
    private ScrollView scrollView;

    public NewEventFragmentSecond() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_RESULT) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    mImage.setImageBitmap(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void bind(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.category_event_recycler_view);
        initRecyclerView();
        mButtonFinish = (Button) view.findViewById(R.id.new_event_finish);
        mImage = (ImageView) view.findViewById(R.id.new_event_image);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select file to upload "), IMAGE_RESULT);
            }
        });
        mDescription = (EditText) view.findViewById(R.id.new_event_description);
        scrollView = (ScrollView) view.findViewById(R.id.new_event_scrollview) ;
        scrollView.scrollTo((int) mDescription.getX(), (int) mDescription.getY());
        mButtonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event newEvent = mListener.getNewEvent();
                newEvent.setDescription(mDescription.getText().toString());
                Bitmap bitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageInByte = baos.toByteArray();
                newEvent.setImage(imageInByte.toString()); // todo delete toString
                newEvent.setCategory(mCategory);
                newEvent.setPublished(new Date());
                mListener.finish(newEvent);
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
        mAdapter = new CategoryAdapter(staticCategories, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_event_fragment_second, container, false);
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
}
