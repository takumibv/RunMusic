package jp.ac.titech.itpro.sdl.runmusic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Date;

/**
 * Created by couchpotatobv on 2017/07/11.
 */

public class GraphView extends View {

    private final static String TAG = "GraphView";
    private final static float Ymax = 20;
    private final static int NDATA_INIT = 256;

    private final static int WIDTH_ADJUST = 120;
    private final static int CENTER_ADJUST = 25;

    private int ndata = NDATA_INIT;
    private float[] vs = new float[NDATA_INIT];
    private int idx = 0;
    private int x0, y0, ewidth;
    private int dw = 5, dh = 1;

    private float val = 0;
    private final static float alpha = 0.95F;

    private final Paint paint = new Paint();

    public GraphView(Context context) {
        this(context, null);
    }

    public GraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG, "onSizeChanged: w=" + w + " h=" + h);
        w -= WIDTH_ADJUST;
        ndata = w / dw;
        x0 = (w - dw * ndata) / 2 + WIDTH_ADJUST/2;
        y0 = h / 2;
        ewidth = x0 + dw * (ndata - 1);

        if (y0 / Ymax >= dh + 1)
            dh = (int) (y0 / Ymax);
        if (ndata > vs.length) {
            idx = 0;
            vs = new float[ndata];
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // grid lines
        paint.setColor(Color.argb(75, 255, 255, 255));
        paint.setStrokeWidth(1);
        int h = canvas.getHeight();
//        for (int y = y0; y < h; y += dh * 5)
//            canvas.drawLine(x0, y, ewidth, y, paint);
//        for (int y = y0; y > 0; y -= dh * 5)
//            canvas.drawLine(x0, y, ewidth, y, paint);
//        for (int x = x0; x < dw * ndata; x += dw * 5)
//            canvas.drawLine(x, 0, x, h, paint);

//        // y0 line
        paint.setColor(Color.CYAN);
//        canvas.drawLine(0, y0, ewidth, y0, paint);
//
//        // graph
//        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(2);
        for (int i = 0; i < ndata - 1; i++) {
            int j = (idx + i) % ndata;
            int x1 = x0 + dw * i;
            int x2 = x0 + dw * (i + 1);
            int y1 = (int) (y0 + dh * vs[j]);
            int y2 = (int) (y0 + dh * vs[(j + 1) % ndata]);
            canvas.drawLine(x1, y1, x2, y2, paint);
        }
    }

    public void addData(float val, boolean invalidate) {
        vs[idx] = val;
        idx = (idx + 1) % ndata;
        if (invalidate)
            invalidate();
    }

    public int mathX(int n){
        return n + CENTER_ADJUST - NDATA_INIT/2;
    }

    public double getVibration(int x){
        return Math.cos(x/5.0)*Math.pow(1.04, -Math.abs(x))*Math.sin(new Date().getTime()/45);
    }

    public void updateData(float v, boolean invalidate){
        val = alpha * val + (1 - alpha) * v;
        for(int i=0; i<NDATA_INIT; i++){
            vs[i] = (float)getVibration(mathX(i))*val*3;
//            vs[i] = mathX(i)/10;
        }
        if (invalidate)
            invalidate();
    }
}
