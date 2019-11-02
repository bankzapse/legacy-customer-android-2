package fonts.rsu;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by Paniti on 7/16/2015.
 */
public class RSUCheckbox extends CheckBox {
    public static final String FONT = "fonts/RSU.ttf";

    public RSUCheckbox(Context context) {
        super(context);
        setTextFont();
    }

    public RSUCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextFont();

    }


    public RSUCheckbox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTextFont();

    }

    private void setTextFont() {
        Typeface hindiFont = Typeface.createFromAsset(getContext().getAssets(), FONT);
        setTypeface(hindiFont,Typeface.NORMAL);
    }

}