package fonts.rsu;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class RSURadio extends RadioButton {

    public static final String FONT = "fonts/RSU.ttf";

    public RSURadio(Context context) {
        super(context);
        setTextFont();
    }

    public RSURadio(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextFont();

    }


    public RSURadio(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTextFont();

    }

    private void setTextFont() {
        Typeface hindiFont = Typeface.createFromAsset(getContext().getAssets(), FONT);
        setTypeface(hindiFont,Typeface.NORMAL);
    }

}