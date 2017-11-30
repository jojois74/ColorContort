package box.gift.colorcontort;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Map;

public class GameActivity extends Activity {
    public GameView gameScreen;
    private GameEngine fallBreak;
    public static Context context = null;
    private Thread gameThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //In maifest, this activity handles its own orientation changes
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this.getBaseContext();
        setContentView(R.layout.activity_game);

        if (savedInstanceState == null)
        {
            gameThread = new Thread(new GameRunnable()); //Use new thread so graphics can happen on the side
            gameThread.start();
        }

        /*
        //Get dimensions of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        */

        /*
        //Create the Bitmap and Canvas
        gameScreen = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888);
        gameDraw = new Canvas(gameScreen);
        */


    }

    private View.OnTouchListener passTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (fallBreak != null && !fallBreak.stopped)
            {
                fallBreak.touch(event);
            }
            return true;
        }
    };

    private void launchGame()
    {
        gameScreen = (GameView) findViewById(R.id.game);
        gameScreen.setOnTouchListener(passTouch);
        //Create a game engine and start it up (give it a paint method-object, ref to current activity to get ui thread, Size object so the game can get the size of the view dynamically)
        fallBreak = new GameEngine(new CanvasPainter(gameScreen), this, gameScreen.getSize());
        fallBreak.startGame();
    }

    public void gameOver(int score)
    {
        //Log.d("Score", score + "");
        //Log.d("bool", Boolean.toString(score >= 0));
        if (score >= 0)
        {
            //Log.d("Return the score", ".");
            Intent returnIntent = new Intent();
            returnIntent.putExtra("score", score);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        else
        {
            //If game ended early
            //Log.d("Canceled", ".");
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    class CanvasPainter
    {
        View view;

        public CanvasPainter(View view)
        {
            this.view = view;
        }

        public void paint() //We need data package from the caller so we know where to paint things
        {
            view.invalidate(); //Force redraw of the gamescreen
        }

        public void giveData(GameEngine gameEngine) {
            ((GameView) view).giveData(gameEngine); //Before drawing, make sure the view has the most up to date data
        }
    }

    class GameRunnable implements Runnable
    {
        public void run()
        {
            launchGame();
        }
    }
}
