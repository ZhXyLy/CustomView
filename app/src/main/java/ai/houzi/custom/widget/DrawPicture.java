package ai.houzi.custom.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class DrawPicture extends View {
    private static final String TAG = "DrawPicture";

    public DrawPicture(Context context) {
        this(context, null);
    }

    public DrawPicture(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawPicture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        recording();    // 调用录制
    }

    private void recording() {
        if (isInEditMode()) {
            return;
        }
        mPicture = new Picture();
        Canvas canvas = mPicture.beginRecording(300, 500);
        // 创建一个画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        // 在Canvas中具体操作
        // 位移
        canvas.translate(150, 150);
        // 绘制一个圆
        canvas.drawCircle(0, 0, 100, paint);
//        paint.setColor(Color.GREEN);
//        paint.setTextSize(30);
//        canvas.drawText("Pictures", 60, 60, paint);
        mPicture.endRecording();
//        mDrawable = new PictureDrawable(mPicture);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) {
            return;
        }
        canvas.drawColor(Color.RED);
        Log.e(TAG, "onDraw: " + mPicture);
        canvas.drawPicture(mPicture);

//        mDrawable.setBounds(0, 200, getWidth(), 300);
//        mDrawable.draw(canvas);

//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        mPicture.writeToStream(os);
//        InputStream is = new ByteArrayInputStream(os.toByteArray());
//        canvas.translate(0, 300);
//        canvas.drawPicture(Picture.createFromStream(is));
    }

    private Picture mPicture;
//    private Drawable mDrawable;
}
