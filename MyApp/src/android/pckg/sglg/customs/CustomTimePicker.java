package android.pckg.sglg.customs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.TimePicker;

public class CustomTimePicker extends TimePicker {
	public CustomTimePicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomTimePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomTimePicker(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
			ViewParent p = getParent();
			if (p != null)
				p.requestDisallowInterceptTouchEvent(true);
		}

		return false;
	}
}