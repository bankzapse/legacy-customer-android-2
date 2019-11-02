package fonts.rsu;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RSUBOLDFont extends TextView {

    public static final String FONT = "fonts/RSU-BOLD.ttf";

	public RSUBOLDFont(Context context) {
		super(context);
		setTextFont();
	}

	public RSUBOLDFont(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTextFont();

	}


	public RSUBOLDFont(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTextFont();
		
	}

	private void setTextFont() {
		Typeface hindiFont = Typeface.createFromAsset(getContext().getAssets(), FONT);
		setTypeface(hindiFont,Typeface.NORMAL);
	}
	
}