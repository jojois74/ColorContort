package box.gift.colorcontort;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import static android.R.attr.defaultValue;

public class MainMenu extends Activity {
    private MainMenuView menuScreen;
    private int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        highScore = sharedPref.getInt("highScore", 0);
        menuScreen = (MainMenuView) findViewById(R.id.menu);
        menuScreen.drawMenu(-1, highScore, false);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    public void giveHeight(int h)
    {
        ImageButton playButton = (ImageButton) findViewById(R.id.play);
        playButton.setMinimumHeight(h / 3);
        playButton.setMaxHeight(h / 3);

        //menuScreen = (MainMenuView) findViewById(R.id.menu);
        //menuScreen.drawMenu(-1, highScore, false);
    }

    public void play(View v)
    {
        Intent startGame = new Intent(this, GameActivity.class);
        startActivityForResult(startGame, 1);
        this.overridePendingTransition(0, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            menuScreen = (MainMenuView) findViewById(R.id.menu);
            if(resultCode == Activity.RESULT_OK)
            {
                int score = data.getIntExtra("score", 0);
                if (score > highScore)
                {
                    highScore = score;
                    SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("highScore", highScore);
                    editor.commit();
                }
                //Log.d("Score in menu", score + "");
                menuScreen.drawMenu(score, highScore, true);
            }
            else if (resultCode == Activity.RESULT_CANCELED)
            {
                //Log.d("Canceled", ".");
                menuScreen.drawMenu(-1, highScore, false);
            }
        }
    }
}
