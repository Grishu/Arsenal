package com.arsenal.leadingmargin;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan.LeadingMarginSpan2;
import android.widget.ImageView;
import android.widget.TextView;

public class LeadingMarginSpanActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		String text = getString(R.string.text);

		// Get the icon and its width
		Drawable dIcon = getResources().getDrawable(R.drawable.icon);
		int leftMargin = dIcon.getIntrinsicWidth() + 10;

		// Set the icon in R.id.icon
		ImageView icon = (ImageView) findViewById(R.id.icon);
		icon.setBackgroundDrawable(dIcon);

		SpannableString ss = new SpannableString(text);
		// Expose the indent for the first three rows
		ss.setSpan(new MyLeadingMarginSpan2(3, leftMargin), 0, ss.length(), 0);

		TextView messageView = (TextView) findViewById(R.id.message_view);
		messageView.setText(ss);

	}

	class MyLeadingMarginSpan2 implements LeadingMarginSpan2 {
		private int margin;
		private int lines;

		MyLeadingMarginSpan2(int lines, int margin) {
			this.margin = margin;
			this.lines = lines;
		}

		/*
		 * Returns the amount by which to adjust the leading margin. Positive
		 * values move away from the leading edge of the paragraph, negative
		 * values move towards it.
		 */
		@Override
		public int getLeadingMargin(boolean first) {
			if (first) {
				/*
				 * This indentation is applied to the number of rows returned
				 * getLeadingMarginLineCount ()
				 */
				return margin;
			} else {
				// Offset for all other Layout layout ) { }
				/*
				 * Returns * the number of rows which should be applied * indent
				 * returned by getLeadingMargin (true) Note:* Indent only
				 * applies to N lines of the first paragraph.
				 */
				return 0;
			}
		}

		@Override
		public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
				int top, int baseline, int bottom, CharSequence text,
				int start, int end, boolean first, Layout layout) {
		}

		/*
		 * Returns the number of lines of text to which this object is attached
		 * that the "first line" margin will apply to. Note that if this returns
		 * N, the first N lines of the region, not the first N lines of each
		 * paragraph, will be given the special margin width.
		 */

		@Override
		public int getLeadingMarginLineCount() {
			return lines;
		}
	};
}