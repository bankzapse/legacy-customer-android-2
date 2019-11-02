package fonts.DBHelvethaicaX;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MedTextView extends TextView {

    public static final String FONT = "fonts/DB Helvethaica X Med v3.2.ttf";

	public MedTextView(Context context) {
		super(context);
		setTextFont();
	}

	public MedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTextFont();

	}


	public MedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTextFont();
		
	}

	private void setTextFont() {
		Typeface hindiFont = Typeface.createFromAsset(getContext().getAssets(), FONT);
		setTypeface(hindiFont,Typeface.NORMAL);
	}
	
}