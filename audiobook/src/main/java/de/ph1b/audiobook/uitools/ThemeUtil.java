package de.ph1b.audiobook.uitools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.AnyRes;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;

import de.ph1b.audiobook.R;

public class ThemeUtil {

    public static void theme(@NonNull SeekBar seekBar) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int colorAccent = seekBar.getResources().getColor(ThemeUtil.getResourceId(
                    seekBar.getContext(), R.attr.colorAccent));
            tint(seekBar.getProgressDrawable(), colorAccent);
        }
    }

    private static void tint(Drawable drawable, @ColorInt int color) {
        Drawable wrapped = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTint(wrapped, color);
    }

    public static void theme(@NonNull Snackbar bar) {
        Context c = bar.getView().getContext();
        Resources r = c.getResources();
        TextView textView = (TextView) bar.getView().findViewById(android.support.design.R.id.snackbar_text);
        if (textView != null) {
            int theme = ThemeUtil.getTheme(c);
            switch (theme) {
                case R.style.DarkTheme:
                    bar.getView().setBackgroundColor(r.getColor(R.color.background_material_light));
                    textView.setTextColor(r.getColor(R.color.abc_primary_text_material_light));
                    break;
                default:
                    break;
            }
        }
    }

    public static int getTheme(@NonNull Context c) {
        Resources r = c.getResources();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        String theme = sp.getString(r.getString(R.string.pref_key_theme), null);
        switch (theme == null ? "light" : theme) {
            case "light":
                return R.style.LightTheme;
            case "dark":
                return R.style.DarkTheme;
            default:
                throw new AssertionError("Unknown theme found=" + theme);
        }
    }

    @AnyRes
    public static int getResourceId(@NonNull Context c, @AttrRes int attr) {
        TypedValue typedValue = new TypedValue();
        c.getTheme().resolveAttribute(attr, typedValue, true);
        int[] attrArray = new int[]{attr};
        TypedArray typedArray = c.obtainStyledAttributes(typedValue.data, attrArray);
        int resId = typedArray.getResourceId(0, -1);
        if (resId == -1) {
            throw new IllegalArgumentException("Resource with attr=" + attr + " not found");
        }
        typedArray.recycle();
        return resId;
    }

    public static void theme(@NonNull NumberPicker numberPicker) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            final int count = numberPicker.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = numberPicker.getChildAt(i);
                if (child instanceof EditText) {
                    try {
                        Field selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
                        selectorWheelPaintField.setAccessible(true);
                        int colorAccent = numberPicker.getResources().getColor(ThemeUtil.getResourceId(numberPicker.getContext(), R.attr.colorAccent));
                        ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(colorAccent);
                        ((EditText) child).setTextColor(colorAccent);
                        numberPicker.invalidate();
                    } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
