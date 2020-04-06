package com.quickCodes.quickCodes.dialpad;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.quickCodes.quickCodes.R;

import java.util.Map;

import static com.quickCodes.quickCodes.fragments.MainFragment.simcards;

public class MyCustomView extends View {
    private String text = "";
    private int circleColor,labelColor;
    private String circleText;
    private Path circle;
    private Paint tPaint;

    public MyCustomView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);

        TypedArray a = context.getTheme()
            .obtainStyledAttributes(attributeSet,R.styleable.MyCustomView,0,0);
        try {
            circleColor = a.getInteger(R.styleable.MyCustomView_circleColor,0);
            labelColor = a.getInteger(R.styleable.MyCustomView_labelColor,0);
            circleText = a.getString(R.styleable.MyCustomView_circleLabel);
        }finally {
            a.recycle();
        }



//        setBackgroundResource(R.drawable.call);


        tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setStyle(Paint.Style.FILL);
//        tPaint.setAntiAlias(true);
        for (Map.Entry<String, String> entry : simcards.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            circleText = key;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //available height
        int viewWidthHalf = getMeasuredWidth()/2;
        int viewHeightHalf = getMeasuredHeight()/2;
        //radious
        int radious =0;
        if(viewWidthHalf>viewHeightHalf)radious = viewHeightHalf-10;
        else radious = viewWidthHalf-10;

        //draw circle
        tPaint.setColor(circleColor);
        canvas.drawCircle(viewWidthHalf,viewHeightHalf,radious,tPaint);

        //add text
        tPaint.setColor(labelColor);
        tPaint.setTextAlign(Paint.Align.CENTER);
        tPaint.setTextSize(30);

//        canvas.drawText(circleText,viewWidthHalf,viewHeightHalf,tPaint);

        circle = new Path();
        circle.addCircle(viewWidthHalf,viewHeightHalf,radious, Path.Direction.CCW);
        canvas.drawTextOnPath(circleText,circle,0,0,tPaint);
    }
}
