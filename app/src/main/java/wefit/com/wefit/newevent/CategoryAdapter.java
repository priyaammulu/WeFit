package wefit.com.wefit.newevent;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import wefit.com.wefit.R;
import wefit.com.wefit.pojo.Category;

/**
 * Created by lorenzo on 11/15/17.
 * This class is responsible to populate Categories in the selected listview
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> categories;
    private AdapterHandler handler;
    private LinearLayout currentlySelected;

    public CategoryAdapter(List<Category> categories, AdapterHandler handler) {
        this.categories = categories;
        this.handler = handler;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_new_event_item, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(categories.get(position).getDisplayName());
        holder.mImage.setImageResource(categories.get(position).getImage());
        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (currentlySelected != null)
                        currentlySelected.setBackground(handler.getDrawable(R.drawable.border_unchecked_category));
                    handler.onItemClick(categories.get(position));
                    holder.mLinearLayout.setBackground(handler.getDrawable(R.drawable.border_checked_category));
                    currentlySelected = holder.mLinearLayout;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * Created by lorenzo on 11/3/17.
     * ViewHolder pattern
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mLinearLayout;
        public TextView mTextView;
        public ImageView mImage;

        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
            mTextView = (TextView) v.findViewById(R.id.newEvent_text);
            mImage = (ImageView) v.findViewById(R.id.newEvent_image);
        }
    }
}
