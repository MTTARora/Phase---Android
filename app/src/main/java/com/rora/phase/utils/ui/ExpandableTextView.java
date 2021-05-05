package com.rora.phase.utils.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.rora.phase.R;

@SuppressLint("AppCompatCustomView")
public class ExpandableTextView extends TextView {
    private static final int DEFAULT_TRIM_LENGTH = 200;
    private static final String ELLIPSIS = "Read more";

    private CharSequence originalText;
    private CharSequence trimmedText;
    private BufferType bufferType;
    private boolean trim = true;
    private int trimLength;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        this.trimLength = typedArray.getInt(R.styleable.ExpandableTextView_trimLength, DEFAULT_TRIM_LENGTH);
        typedArray.recycle();
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setText() {
        super.setText(getDisplayableText(), bufferType);
    }

    private CharSequence getDisplayableText() {
        CharSequence displayText = trim ? trimmedText : getOriginalText();
        return displayText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        trimmedText = getTrimmedText(text);
        bufferType = type;
        setText();
    }

    private CharSequence getTrimmedText(CharSequence text) {
        SpannableString ss = new SpannableString(ELLIPSIS);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                trim = !trim;
                setText();
                requestFocusFromTouch();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getContext().getResources().getColor(R.color.colorPrimary));
                ds.setUnderlineText(false);
            }
        };
        if (originalText != null && originalText.length() > trimLength) {
            ss.setSpan(clickableSpan, 0, ELLIPSIS.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return new SpannableStringBuilder(originalText, 0, trimLength + 1).append("... ").append(ss);
        } else {
            return originalText;
        }
    }

    private CharSequence getOriginalText() {
        SpannableString ss = new SpannableString(" Show less");

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                trim = !trim;
                setText();
                requestFocusFromTouch();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getContext().getResources().getColor(R.color.colorPrimary));
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 0, " Show less".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return new SpannableStringBuilder(originalText, 0, originalText.length()).append(ss);
    }

    public void setTrimLength(int trimLength) {
        this.trimLength = trimLength;
        trimmedText = getTrimmedText(originalText);
        setText();
    }

    public int getTrimLength() {
        return trimLength;
    }
}
