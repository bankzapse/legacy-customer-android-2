package fonts.rsu;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class RSUEdit extends EditText {

    public static final String FONT = "fonts/RSU.ttf";

    public RSUEdit(Context context) {
        super(context);
        setTextFont();
    }

    public RSUEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextFont();

    }


    public RSUEdit(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTextFont();

    }

    private void setTextFont() {
        Typeface hindiFont = Typeface.createFromAsset(getContext().getAssets(), FONT);
        setTypeface(hindiFont,Typeface.NORMAL);
    }

}