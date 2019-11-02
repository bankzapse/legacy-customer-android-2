package fonts.DBHelvethaicaX;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CondTextView extends TextView {

    public static final String FONT = "fonts/DB Helvethaica X Cond v3.2.ttf";

	public CondTextView(Context context) {
		super(context);
		setTextFont();
	}

	public CondTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTextFont();

	}


	public CondTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTextFont();
		
	}

	private void setTextFont() {
		Typeface hindiFont = Typeface.createFromAsset(getContext().getAssets(), FONT);
		setTypeface(hindiFont,Typeface.NORMAL);
	}
	
}