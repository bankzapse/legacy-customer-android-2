package fonts.DBHelvethaicaX;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class MedEditText extends EditText {

    public static final String FONT = "fonts/DB Helvethaica X Med v3.2.ttf";

    public MedEditText(Context context) {
        super(context);
        setTextFont();
    }

    public MedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextFont();

    }


    public MedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTextFont();

    }

    private void setTextFont() {
        Typeface hindiFont = Typeface.createFromAsset(getContext().getAssets(), FONT);
        setTypeface(hindiFont,Typeface.NORMAL);
    }

}