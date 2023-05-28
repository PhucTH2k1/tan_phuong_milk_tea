package com.tanphuong.milktea.bill.util;

import android.text.InputFilter;
import android.text.Spanned;

public class CreditCardInputFilter implements InputFilter {
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (dest != null & dest.toString().trim().length() > 24) return null;
        if (source.length() == 1 && (dstart == 4 || dstart == 9 || dstart == 14))
            return " " + new String(source.toString());
        return null; // keep original
    }
}
