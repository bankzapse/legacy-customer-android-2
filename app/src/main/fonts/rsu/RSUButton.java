package th.co.interactive.watchup.fonts.rsu;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class RSUButton extends Button {

    public static final String FONT = "fonts/RSU.ttf";

	public RSUButton(Context context) {
		super(context);
		setTextFont();
	}

	public RSUButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTextFont();

	}


	public RSUButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTextFont();
		
	}

	private void setTextFont() {
		Typeface hindiFont = Typeface.createFromAsset(getContext().getAssets(), FONT);
		setTypeface(hindiFont,Typeface.NORMAL);
	}
	
}