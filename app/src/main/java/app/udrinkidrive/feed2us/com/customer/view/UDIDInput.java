package app.udrinkidrive.feed2us.com.customer.view;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import app.udrinkidrive.feed2us.com.customer.R;

/**
 * Created by TL3 on 8/15/2016 AD.
 */
public class UDIDInput {

    //Dialog
    public Dialog UDIDInput;
    public Button dBtnCancel;
    public Button dBtnConfirm;
    public EditText edtInput;

    private UDIDInput(){

    }

    public UDIDInput(Activity activity, String hint) {
        bindWidget(activity);

        edtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    edtInput.setText(s);
                    int textLength = edtInput.getText().length();
                    edtInput.setSelection(textLength, textLength);
                }
            }
        });

        dBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UDIDInput.dismiss();
            }
        });

        edtInput.setHint(hint);
        edtInput.requestFocus();
    }

    private void bindWidget(Activity activity) {

        UDIDInput = new Dialog(activity);
        UDIDInput.requestWindowFeature(Window.FEATURE_NO_TITLE);
        UDIDInput.setContentView(R.layout.udid_input_alertview);
        UDIDInput.setCancelable(false);
        UDIDInput.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        edtInput = (EditText) UDIDInput.findViewById(R.id.edt_input);
        dBtnCancel = (Button) UDIDInput.findViewById(R.id.dBtnCancel);
        dBtnConfirm = (Button) UDIDInput.findViewById(R.id.dBtnConfirm);

    }

}
