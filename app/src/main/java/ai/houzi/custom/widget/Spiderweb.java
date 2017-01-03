package ai.houzi.custom.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ai.houzi.custom.R;

/**
 * 蜘蛛网
 */
public class Spiderweb extends View {
    private static final String TAG = "Spiderweb";

    private Paint mPaint;
    private Paint mValuePaint;
    private TextPaint mTextPaint;
    private int mWidth, mHeight;//宽高
    private int mRadius;//半径
    private int mCenterX, mCenterY;//中心点
    private int mCornerSize = 0;//尖角个数（边数）
    private int mLayerSize = 4;//层数
    private int startAngle = 0;//起始角度
    private int mPerAngle;//每个顶点之间的角度
    private int mPointRadius = 0;//小圆点半径

    private int clockwise = 1;//1顺时针，-1逆时针
    private int degress = 0;//

    private List<Spider> spiders;

    public Spiderweb(Context context) {
        this(context, null);
    }

    public Spiderweb(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Spiderweb(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        spiders = new ArrayList<>();
        if (isInEditMode()) {
            List<Spider> list = new ArrayList<>();
            Random random = new Random(1);
            for (int i = 0; i < 3; i++) {
                list.add(new Spider("1" + i, "title" + i, random.nextFloat() / 2 + 0.5f));
            }
            setDatas(list);
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Spiderweb);
        mLayerSize = a.getInt(R.styleable.Spiderweb_layer_size, 4);
//        degress = a.getInt(R.styleable.Spiderweb_start_angle, 0);
        mPointRadius = a.getDimensionPixelSize(R.styleable.Spiderweb_point_radius, (int) (getResources().getDisplayMetrics().density * 2 + 0.5f));
        float line_width = a.getDimension(R.styleable.Spiderweb_line_width, 1);
        float spider_width = a.getDimension(R.styleable.Spiderweb_spider_width, 1);
        float title_size = a.getDimension(R.styleable.Spiderweb_title_size, (int) (getResources().getDisplayMetrics().density * 14 + 0.5f));
        int spider_color = a.getColor(R.styleable.Spiderweb_spider_color, Color.parseColor("#029ccc"));
//        clockwise = a.getBoolean(R.styleable.Spiderweb_clockwise, true) ? 1 : -1;
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(line_width);
        mPaint.setStyle(Paint.Style.STROKE);
        mValuePaint = new Paint();
        mValuePaint.setAntiAlias(true);
        mValuePaint.setColor(spider_color);
        mValuePaint.setStrokeWidth(spider_width);
        mValuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(title_size);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.DKGRAY);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mCornerSize == 0) {
            return;
        }
        mWidth = w;
        mHeight = h;
        mRadius = Math.min(mWidth / 2, mHeight / 2) - (int) mTextPaint.measureText("一二三四");
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        mPerAngle = 360 / mCornerSize;
        switch (mCornerSize) {
            case 3:
                startAngle = 30;
                mCenterY = (int) (mCenterY + (mCenterY - mCenterY * sin(startAngle)) / 2);
                break;
            case 4:
                startAngle = 45;
                break;
            case 5:
                startAngle = 54;
                mCenterY = (int) (mCenterY + (mCenterY - mCenterY * sin(startAngle)) / 2);
                break;
            case 7:
                startAngle = 64;
                mCenterY = (int) (mCenterY + (mCenterY - mCenterY * sin(startAngle)) / 2);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        //移动坐标原点到中心点
        canvas.translate(mCenterX, mCenterY);
        canvas.save();
        canvas.rotate(degress);
        mPaint.setColor(Color.LTGRAY);
        drawPolygon(canvas);
        drawLines(canvas);
        drawTexts(canvas);
        mPaint.setColor(Color.BLACK);
        drawRegion(canvas);
        canvas.restore();
    }

    //绘制区域
    private void drawRegion(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < mCornerSize; i++) {
            float p = mRadius * spiders.get(i).percent;
            int angle = (startAngle + mPerAngle * i) * clockwise;
            int x = (int) (p * cos(angle));
            int y = (int) (p * sin(angle));
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
            canvas.drawCircle(x, y, mPointRadius, mPaint);
        }
        path.close();
        canvas.drawPath(path, mPaint);

        mValuePaint.setAlpha(127);
        canvas.drawPath(path, mValuePaint);

    }

    //绘制顶点文字
    private void drawTexts(Canvas canvas) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        int space = 10;
        for (int i = 0; i < mCornerSize; i++) {
            String title = spiders.get(i).title;
            int angle = (startAngle + mPerAngle * i) * clockwise;
            int x = (int) (mRadius * cos(angle));
            int y = (int) (mRadius * sin(angle));
            Log.e(TAG, angle + "drawTexts: " + x + "==y==" + y);
            if (angle == 0) {
                canvas.drawText(title, x + space, y + fontHeight / 2, mTextPaint);
            } else if (angle == 90) {
                float dis = mTextPaint.measureText(title);//文本长度
                canvas.drawText(title, x - dis / 2, y + fontHeight, mTextPaint);
            } else if (angle == 180) {
                float dis = mTextPaint.measureText(title);//文本长度
                canvas.drawText(title, x - dis - space, y + fontHeight / 2, mTextPaint);
            } else if (angle == 270) {
                float dis = mTextPaint.measureText(title);//文本长度
                canvas.drawText(title, x - dis / 2, y - space, mTextPaint);
            } else if (angle > 0 && angle < 90) {//0-90度
                canvas.drawText(title, x, y + fontHeight, mTextPaint);
            } else if (angle > 90 && angle < 180) {//90-180度
                float dis = mTextPaint.measureText(title);//文本长度
                canvas.drawText(title, x - dis, y + fontHeight, mTextPaint);
            } else if (angle > 180 && angle < 270) {//180-270度
                float dis = mTextPaint.measureText(title);//文本长度
                canvas.drawText(title, x - dis, y - space, mTextPaint);
            } else if (angle > 270 && angle < 360) {//270-360度
                float dis = mTextPaint.measureText(title);//文本长度
                canvas.drawText(title, x, y - space, mTextPaint);
            }
        }
    }

    //  绘制放射线
    private void drawLines(Canvas canvas) {
        Path path = new Path();
        for (int j = 0; j < mCornerSize; j++) {
            int angle = (startAngle + mPerAngle * j) * clockwise;
            path.reset();
            path.lineTo(mRadius * cos(angle), mRadius * sin(angle));
            canvas.drawPath(path, mPaint);
        }
    }

    //绘制多边形
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        Log.e(TAG, "drawPolygon: " + startAngle);
        int r = mRadius / mLayerSize;//蜘蛛网间距
        for (int i = 1; i < mLayerSize + 1; i++) {
            int curR = i * r;
            path.reset();
            for (int j = 0; j < mCornerSize; j++) {
                if (j == 0) {
                    path.moveTo(curR * cos(startAngle), curR * sin(startAngle));
                } else {
                    int angle = (startAngle + mPerAngle * j) * clockwise;
                    path.lineTo(curR * cos(angle), curR * sin(angle));
                }
            }
            path.close();
            canvas.drawPath(path, mPaint);
        }
    }

    private float cos(int angle) {
        return (float) Math.cos(angle * Math.PI / 180);
    }

    private float sin(int angle) {
        return (float) Math.sin(angle * Math.PI / 180);
    }

    class Spider {
        String id;
        String title;
        float percent;

        public Spider(String id, String title, float percent) {
            this.id = id;
            this.title = title;
            this.percent = percent;
        }
    }

    public void setDatas(List<Spider> datas) {
        if (datas == null || datas.isEmpty()) {
            Log.e(TAG, "setDatas: The List is null or empty");
            return;
        }
        this.spiders.clear();
        this.spiders.addAll(datas);
        mCornerSize = spiders.size();
        requestLayout();
    }
}
