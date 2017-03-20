package android.pckg.sglg.customs;

import java.util.HashMap;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.pckg.sglg.R;
import android.pckg.sglg.Business.SettingCore;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.ImageLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** An array adapter that knows how to render views when given CustomData classes */
public class CustomArrayAdapter extends ArrayAdapter<CustomData> {
    private LayoutInflater mInflater;
    public ImageLoader imageLoader;

    public CustomArrayAdapter(Context context, CustomData[] values) {
        super(context, R.layout.listview_horizontal_items_row_list, values);
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context.getApplicationContext());
    }

    @Override
    public View getView(int position, View vi, ViewGroup parent) {
        Holder holder;

		if (vi == null)
			vi = mInflater.inflate(R.layout.listview_horizontal_items_row_list, null);
        // Set the color
//        vi.setBackgroundColor(getItem(position).getBackgroundColor());
//        SettingCore score = new SettingCore(context);
		TextView title = (TextView) vi.findViewById(R.id.items_title); // title
		ImageView thumb_image = (ImageView) vi.findViewById(R.id.horizontal_list_items_image); // thumb
//		if(!score.getImageview())
			thumb_image.setVisibility(ImageView.GONE);
		title.setText(getItem(position).getPname());
		String path = Environment.getExternalStorageDirectory().toString();
		imageLoader.DisplayImage(AMSTools.GetServerIP() + "LPIMWS/"
				+ getItem(position).getImage(),
				thumb_image);
//		thumb_image.setImageBitmap(BitmapFactory.decodeFile(path + "/SGLG/"+getItem(position).getImage()));
		OnClickListener itemListener = new OnClickListener() {
			public void onClick(View v) {
//				mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
//				addToList(pid, lid);
			}
		};
		vi.setOnClickListener(itemListener);   
        return vi;
    }

    /** View holder for the views we need access to */
    private static class Holder {
        public TextView textView;
    }
}
