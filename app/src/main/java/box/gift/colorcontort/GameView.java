package box.gift.colorcontort;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Joseph on 10/3/2017.
 */

public class GameView extends View
{
    private Paint paint;
    private GameEngine data;
    public static int width = 0;
    public static int height = 0;
    public boolean currentlyPainting = false;
    private boolean done = false;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas gameDraw)
    {
        if (done) return;
        currentlyPainting = true;
        super.onDraw(gameDraw);
        drawToCanvas(gameDraw, data, paint, false, -1);


/*
        //Movestate
        switch (data.player.movementState)
        {
            case Player.SPEEDING_UP:
                paint.setColor(Color.parseColor("#00ff00"));
                break;
            case Player.SLOWING_DOWN:
                paint.setColor(Color.parseColor("#0000ff"));
                break;
            case Player.STOPPED:
                paint.setColor(Color.parseColor("#ff0000"));
                break;
            case Player.MICRO_MOVE:
                paint.setColor(Color.parseColor("#ffff00"));
                break;
        }
        gameDraw.drawCircle(width/2 - 25, height/2 - 25, 50, paint);
        */
        currentlyPainting = false;
    }

    public static void drawToCanvas(Canvas gameDraw, GameEngine data, Paint paint, boolean forMainMenu, int score)
    {
        if (data == null && !forMainMenu) return;

        paint.setStyle(Paint.Style.FILL);

        int width = GameView.width;
        int height = GameView.height;
        if (forMainMenu)
        {
            width = MainMenuView.width;
            height = MainMenuView.height;
        }

        Player player = null;
        Obstacle obstacle = null;
        if (!forMainMenu)
        {
            player = data.player;
            obstacle = data.obstacle;
        }

        //Background
        paint.setColor(Color.WHITE);
        gameDraw.drawRect(0, 0, width, height, paint);

        int pWidth = 0;
        int pHeight = 0;
        if (!forMainMenu)
        {
            pWidth = player.width;
            pHeight = player.height;
        }

        /*
        //Gridlines
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(1);
        gameDraw.drawLine(width / 3, pHeight + (height - pHeight) / 12, width / 3, 23 * (height - pHeight) / 24, paint);
        gameDraw.drawLine(2 * width / 3, pHeight + (height - pHeight) / 12, 2 * width / 3, 23 * (height - pHeight) / 24, paint);
*/

        //Color hints
        int x = 0;
        int y = 0;
        int cWidth = width / 3;
        int cHeight = height;
        paint.setColor(Color.YELLOW);
        //paint.setAlpha(255 * data.showColorsFrames / GameEngine.FRAMES_SHOW_COLORS);
        gameDraw.drawRect(x, y, x + cWidth, y + cHeight, paint);

        x = width / 3;
        paint.setColor(Color.CYAN);
        //paint.setAlpha(255 * data.showColorsFrames / GameEngine.FRAMES_SHOW_COLORS);
        gameDraw.drawRect(x, y, x + cWidth, y + cHeight, paint);

        x = 2 * width / 3;
        paint.setColor(Color.MAGENTA);
        //paint.setAlpha(255 * data.showColorsFrames / GameEngine.FRAMES_SHOW_COLORS);
        gameDraw.drawRect(x, y, x + cWidth, y + cHeight, paint);

        //Bottom zone
        if (!forMainMenu)
        {
            paint.setColor(Color.WHITE);
            x = 0;
            y = 21 * height / 25;
            int zWidth = width;
            int zHeight = height - y;
            gameDraw.drawRect(x, y, x + zWidth, y + zHeight, paint);
        }

        //Player
        if (player != null) {
            switch (player.color) {
                case GameEngine.CYAN:
                    paint.setColor(Color.CYAN);
                    break;
                case GameEngine.MAGENTA:
                    paint.setColor(Color.MAGENTA);
                    break;
                case GameEngine.YELLOW:
                    paint.setColor(Color.YELLOW);
                    break;
                default:
                    paint.setColor(Color.DKGRAY);
                    break;
            }
            x = 0;
            y = 0;
            gameDraw.drawRect(x, y, x + pWidth, y + pHeight, paint);
        }

        //Obstacle
        if (obstacle != null && !forMainMenu) {
            /*switch (obstacle.color) {
                case GameEngine.CYAN:
                    paint.setColor(Color.CYAN);
                    break;
                case GameEngine.MAGENTA:
                    paint.setColor(Color.MAGENTA);
                    break;
                case GameEngine.YELLOW:
                    paint.setColor(Color.YELLOW);
                    break;
            }*/
            paint.setColor(Color.BLACK);
            int oWidth = obstacle.width;
            int oHeight = obstacle.height;
            x = 0;
            y = obstacle.y;
            gameDraw.drawRect(x, y, x + oWidth, y + oHeight, paint);
        }

        if (obstacle != null && obstacle.color != GameEngine.DEFAULT && !forMainMenu) {
            //Marker
            //Colored
            switch (obstacle.color) {
                case GameEngine.CYAN:
                    paint.setColor(Color.CYAN);
                    break;
                case GameEngine.MAGENTA:
                    paint.setColor(Color.MAGENTA);
                    break;
                case GameEngine.YELLOW:
                    paint.setColor(Color.YELLOW);
                    break;
            }
            Path path = new Path();
            path.moveTo((data.markerLane * width / 3) + width / 6, 35 * height / 40);
            path.lineTo((data.markerLane * width / 3) + width / 6 + width / 12, 37 * height / 40);
            path.lineTo((data.markerLane * width / 3) + width / 6 - width / 12, 37 * height / 40);
            path.close();
            gameDraw.drawPath(path, paint);

            path = new Path();
            path.moveTo((data.markerLane * width / 3) + width / 6, 36 * height / 40);
            path.lineTo((data.markerLane * width / 3) + width / 6 + width / 12, 38 * height / 40);
            path.lineTo((data.markerLane * width / 3) + width / 6 - width / 12, 38 * height / 40);
            path.close();
            gameDraw.drawPath(path, paint);

            path = new Path();
            path.moveTo((data.markerLane * width / 3) + width / 6, 37 * height / 40);
            path.lineTo((data.markerLane * width / 3) + width / 6 + width / 12, 39 * height / 40);
            path.lineTo((data.markerLane * width / 3) + width / 6 - width / 12, 39 * height / 40);
            path.close();
            gameDraw.drawPath(path, paint);
        }

        //Score
        if (!forMainMenu)
        {
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(234);
            paint.setColor(Color.WHITE);
            x = width / 2;
            y = (int) ((player.height / 2) - ((paint.descent() + paint.ascent()) / 2));
            gameDraw.drawText(data.score + "", x, y, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            paint.setColor(Color.BLACK);
            gameDraw.drawText(data.score + "", x, y, paint);
        }
    }

    public void giveData(GameEngine data)
    {
        this.data = data;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        width = w;
        height = h;
    }

    public Size getSize()
    {
        return new Size();
    }

    public void stopPainting() {
        done = true;
    }

    class Size
    {
        public Size() {}
        public int getWidth()
        {
            return width;
        }
        public int getHeight()
        {
            return height;
        }
    }
}