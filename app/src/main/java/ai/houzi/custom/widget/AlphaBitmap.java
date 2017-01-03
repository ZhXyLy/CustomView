package ai.houzi.custom.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

import ai.houzi.custom.R;


public class AlphaBitmap extends View {

    private Bitmap mBitmap1;
    private Bitmap mBitmap2;
    private Bitmap mBitmap3;
    private Shader mShader;
    private Paint mPaint;

    public AlphaBitmap(Context context) {
        this(context, null);
    }

    public AlphaBitmap(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphaBitmap(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(true);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;//缩小倍数
        InputStream is = getResources().openRawResource(R.raw.app_sample_code);
        mBitmap1 = BitmapFactory.decodeStream(is, new Rect(0, 0, 100, 100), options);
        mBitmap2 = mBitmap1.extractAlpha();
        mBitmap3 = Bitmap.createBitmap(200, 200, Bitmap.Config.ALPHA_8);
        drawIntoBitmap(mBitmap3);
        mShader = new LinearGradient(0, 0, 100, 70, new int[]{
                Color.RED, Color.GREEN, Color.BLUE},
                null, Shader.TileMode.MIRROR);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(mBitmap1, 10, 10, mPaint);
        canvas.drawBitmap(mBitmap2, 10, mBitmap1.getHeight() + 10, mPaint);
        mPaint.setShader(mShader);
        canvas.drawBitmap(mBitmap3, 10, mBitmap1.getHeight() + 10 + mBitmap2.getHeight() + 10, mPaint);
    }

    private static void drawIntoBitmap(Bitmap bm) {
        float x = bm.getWidth();
        float y = bm.getHeight();
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setAntiAlias(true);

        p.setAlpha(0x80);
        c.drawCircle(x / 2, y / 2, x / 2, p);

        p.setAlpha(0x30);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        p.setTextSize(60);
        p.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm = p.getFontMetrics();
        c.drawText("Alpha", x / 2, (y - fm.ascent) / 2, p);
    }
}
