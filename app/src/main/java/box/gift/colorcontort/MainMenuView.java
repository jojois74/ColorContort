package box.gift.colorcontort;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import static android.R.attr.data;
import static android.R.attr.x;
import static android.R.attr.y;

/**
 * Created by Joseph on 10/14/2017.
 */

public class MainMenuView extends View
{
    private int score;
    private int highScore;
    public static int width;
    public static int height;

    public MainMenuView(Context context) {
        super(context);
        init(context);
    }

    public MainMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MainMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        setWillNotDraw(false);
    }

    public void drawMenu(int score, int highScore, boolean useScore)
    {
        //Log.d("Score in menu view: ", score + "");
        if (!useScore)
            this.score = -1;
        else
            this.score = score;
        this.highScore = highScore;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        width = w;
        height = h;
        ((MainMenu) getContext()).giveHeight(h);
    }

    @Override
    protected void onDraw(Canvas menuDraw)
    {
        super.onDraw(menuDraw);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        GameView.drawToCanvas(menuDraw, null, paint, true, score);

        //High score
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
        int x = width / 2;
        int y = height / 2 - px;
        menuDraw.drawText("High score is " + highScore, x, y, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(Color.BLACK);
        menuDraw.drawText("High score is " + highScore, x, y, paint);
        paint.setStyle(Paint.Style.FILL);

        //Score
        if (score == -1) return;
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        x = width / 2;
        y = height / 2;
        menuDraw.drawText("You scored " + score, x, y, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(Color.BLACK);
        menuDraw.drawText("You scored " + score, x, y, paint);
    }
}
