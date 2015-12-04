package com.desk.android.sdk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.desk.android.sdk.R;

import java.util.Random;

/**
 * <p>
 *     View which supports displaying an avatar within a colored background. TODO add support for image
 * </p>
 *
 * Created by Matt Kranzler on 10/21/15.
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class AvatarView extends FrameLayout {

    @VisibleForTesting static final float DEFAULT_TEXT_SIZE_SINGLE = 16.0f;
    @VisibleForTesting static final float DEFAULT_TEXT_SIZE_DOUBLE = 12.0f;
    @VisibleForTesting TextView text;

    private static TypedArray colors;
    private static final Random RANDOM = new Random();

    private float textSizeSingle;
    private float textSizeDouble;
    private boolean borderEnabled;
    private int avatarColor;

    /**
     * Selects a random color from {@code avatar_colors} array.
     *
     * @param context the context used to access {@code avatar_colors}
     * @param excludeColor the color to exclude
     * @return a randomly selected color from {@code avatar_colors}
     */
    @SuppressWarnings("deprecation")
    public static int getRandomAvatarColor(Context context, int excludeColor) {
        // lazy initialize colors
        if (colors == null) {
            colors = context.getResources().obtainTypedArray(R.array.avatar_colors);
        }

        int color = colors.getColor(RANDOM.nextInt(colors.length()), context.getResources().getColor(R.color.avatar_blue));

        // recurse if color is excluded
        if (color == excludeColor) {
            return getRandomAvatarColor(context, excludeColor);
        }

        return color;
    }

    public AvatarView(Context context) {
        this(context, null);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    /**
     * Set the name to be used to generate the avatar text. The name will display at most 2 letters,
     * using the first letter of each word in the name. For example, if the name passed in is 'Matt',
     * the text will be 'M'. If the name passed in is 'Matt Kranzler', it will be MK.
     * @param name the name
     */
    public void setAvatarName(@NonNull String name) {
        if (TextUtils.isEmpty(name)) {
            return;
        }

        StringBuilder text = new StringBuilder();
        String[] words = name.split("\\s");
        text.append(words[0].substring(0, 1));
        if (words.length > 1) {
            text.append(words[1].substring(0, 1));
            this.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeDouble);
        } else {
            this.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSingle);
        }
        this.text.setText(text);
    }

    /**
     * Set the color of the avatar background.
     * @param color the color
     */
    public void setAvatarColor(Integer color) {
        if (color == null) {
            return;
        }

        avatarColor = color;
        if (borderEnabled) {
            Drawable border = DrawableCompat.wrap(new ShapeDrawable(new OvalShape()));
            DrawableCompat.setTint(border, Color.WHITE);

            Drawable background = DrawableCompat.wrap(new ShapeDrawable(new OvalShape()));
            DrawableCompat.setTint(background, color);

            Drawable[] layers = { border, background };

            int padding = getResources().getDimensionPixelSize(R.dimen.border_width);
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            layerDrawable.setLayerInset(0, 0, 0, 0, 0);
            layerDrawable.setLayerInset(1, padding, padding, padding, padding);

            setBackground(layerDrawable);
        } else {
            Drawable circle = DrawableCompat.wrap(new ShapeDrawable(new OvalShape()));
            DrawableCompat.setTint(circle, color);
            setBackground(circle);
        }
    }

    public int getAvatarColor() {
        return avatarColor;
    }

    /**
     * Show or hides the border.
     * @param show if true, the border will be shown. Otherwise it will be removed.
     */
    public void toggleBorder(boolean show) {
        borderEnabled = show;
        setAvatarColor(avatarColor);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(getContext()).inflate(R.layout.avatar_view, this, true);
        text = (TextView) findViewById(R.id.avatar_text);

        TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.AvatarView, defStyleAttr, 0);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        textSizeSingle = ta.getDimensionPixelSize(
                R.styleable.AvatarView_textSizeSingle,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE_SINGLE, displayMetrics)
        );
        textSizeDouble = ta.getDimensionPixelSize(
                R.styleable.AvatarView_textSizeDouble,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE_DOUBLE, displayMetrics)
        );

        borderEnabled = ta.getBoolean(R.styleable.AvatarView_enableBorder, false);

        String name = ta.getString(R.styleable.AvatarView_avatarName);
        if (!TextUtils.isEmpty(name)) {
            setAvatarName(name);
        }

        int color = ta.getColor(R.styleable.AvatarView_avatarColor, -1);
        if (color != -1) {
            setAvatarColor(color);
        }

        ta.recycle();
    }

    @Override public boolean isInEditMode() {
        return true;
    }
}
