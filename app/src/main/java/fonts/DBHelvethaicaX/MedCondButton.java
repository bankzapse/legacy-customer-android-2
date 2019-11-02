package fonts.DBHelvethaicaX;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class MedCondButton extends Button {

    public static final String FONT = "fonts/DB Helvethaica X Med Cond v3.2.ttf";

	public MedCondButton(Context context) {
		super(context);
		setTextFont();
	}

	public MedCondButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTextFont();

	}


	public MedCondButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTextFont();
		
	}

	private void setTextFont() {
		Typeface hindiFont = Typeface.createFromAsset(getContext().getAssets(), FONT);
		setTypeface(hindiFont,Typeface.NORMAL);
	}
	
}