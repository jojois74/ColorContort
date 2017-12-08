package box.gift.colorcontort;

import android.content.Context;
import android.os.Vibrator;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;

import java.util.Random;

import android.app.Activity;
import android.view.MotionEvent;

class GameEngine
{
    private static final int TARGET_FPS = 60;
    private static final double EXPECTED_TIME_MS = 1000 / TARGET_FPS;
    private GameActivity.CanvasPainter canvasPainter;
    public boolean stopped = false;
    private long totalUpdateTime = Math.round(EXPECTED_TIME_MS);
    private Activity parentActivity;
    private GameView.Size size;
    private double gameDifficulty = 1;
    private double gameDifficultyAcceleration = .0008; //Per frame

    private double lastActualTouchX = 0.0;
    private double lastTouchX = 0.0;
    private double prevTouchX = 0.0;
    private MotionEvent lastTouchEvent;

    private boolean touching = false;
    private boolean started = false;

    private Random random = new Random();
    private Vibrator rumble = (Vibrator) GameActivity.context.getSystemService(Context.VIBRATOR_SERVICE);

    public Player player = new Player();
    public Obstacle obstacle;
    public int markerLane;
    private boolean hardMode = false;

    public static final int YELLOW = 0;
    public static final int CYAN = 1;
    public static final int MAGENTA = 2;
    public static final int DEFAULT = 3;
    public static final int FRAMES_SHOW_COLORS = 100;
    public int showColorsFrames = FRAMES_SHOW_COLORS;

    public int score = 0;

    public boolean cleaningUp = false;

    public GameEngine(GameActivity.CanvasPainter canvasPainter, Activity parentActivity, final GameView.Size size)
    {
        this.parentActivity = parentActivity;
        this.canvasPainter = canvasPainter; //So we can call its paint method when we have something to update
        canvasPainter.giveData(this);
        this.size = size;
        player = new Player();
    }

    private int randomBetween(int min, int max)
    {
        return random.nextInt(max + 1 - min) + min;
    }

    public void startGame()
    {
        if (!started)
        {
            started = true;
            runGame();
        }
    }

    private void runGame()
    {
        while (size.getWidth() == 0) {;} //Wait until the view is given a size
        //Initialize
        player.color = DEFAULT;
        player.width = size.getWidth();
        player.height = size.getHeight() / 6;
        while (!stopped) {
            long startTime = System.currentTimeMillis();
            //Use the time it took for the last update to forge a delta for this update
            double delta = ((double) Math.max(totalUpdateTime, EXPECTED_TIME_MS)) / EXPECTED_TIME_MS; //Delta cannot be less than 1, because we would have added an extra delay to compensate
            //Log.d("tag", "DELTA: " + delta);

            enterFrame(delta); //Run frame code

            if (!stopped) {
                parentActivity.runOnUiThread(new Runnable() { //Must run graphics code on the UI thread
                    @Override
                    public void run() {
                        if (canvasPainter != null)
                        {
                            try
                            {
                                canvasPainter.paint(); //Paint the frame
                            }
                            catch (NullPointerException e) {}
                        }
                    }
                });

                long endTime = System.currentTimeMillis();
                totalUpdateTime = endTime - startTime;
                //Log.d("tag", "totalUpdateTime: " + totalUpdateTime);

                long amountToDelay = Math.max(Math.round(EXPECTED_TIME_MS) - totalUpdateTime, 0); //If there is any time left, delay
                try {
                    Thread.sleep(amountToDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void enterFrame(double delta)
    {
        double deltaWithDifficulty = delta * gameDifficulty; // Further increase values based on difficulty

        int width = size.getWidth();
        int height = size.getHeight();

        showColorsFrames--;
        if (showColorsFrames < 0) showColorsFrames = 0;
        else return;

        if (obstacle == null)
        {
            obstacle = new Obstacle(randomBetween(0, 2));
            obstacle.y = height + 500;
            obstacle.width = width;
            obstacle.height = height / 12;
            if (hardMode)
            {
                markerLane = randomBetween(0, 2);
            }
            else
                markerLane = obstacle.color;
        }
        obstacle.y -= obstacle.speed * deltaWithDifficulty;
        if (obstacle.y < player.height - obstacle.speed)
        {
            if (obstacle.color != player.color)
            {
                gameOver();
            }
            else
            {
                score++;
                player.color = DEFAULT;
                rumble.vibrate(50);
            }
            obstacle = null;
        }

        gameDifficulty += gameDifficultyAcceleration;
        //Log.d("dif", gameDifficulty + "");
        if (gameDifficulty >= 1.4 && !hardMode)
        {
            hardMode = true;
            rumble.vibrate(350);
        }
    }

    private void gameOver()
    {
        stopped = true;
        while(((GameActivity) parentActivity).gameScreen.currentlyPainting) {} //Wait until view is done painting
        ((GameActivity) parentActivity).gameScreen.stopPainting();
        player = null;
        random = null;
        rumble.cancel();
        rumble = null;
        obstacle = null;
        size = null;
        canvasPainter = null;
        ((GameActivity) parentActivity).gameOver(score);
    }

    public void touch(MotionEvent event) //Activity passes the touch event so we know what to do
    {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN)
        {
            lastTouchX = event.getX();
        }

        //Set player color
        if (lastTouchX <= size.getWidth() / 3)
        {
            player.color = YELLOW;
        }
        else if (lastTouchX <= 2 * size.getWidth() / 3)
        {
            player.color = CYAN;
        }
        else
        {
            player.color = MAGENTA;
        }
    }

    /*
    * int initialPlayerMovementState = player.movementState;
        int playerErrorX = (int) Math.abs(lastTouchX - player.x);

        if (Math.abs(prevTouchX - lastTouchX) < Player.LOCKOUT_RANGE)
        {
            if (lastTouchX > player.x)
            {
                player.direction = Player.RIGHT;
            }
            else if (lastTouchX < player.x)
            {
                player.direction = Player.LEFT;
            }
            else
            {
                player.direction = Player.NONE;
            }
        }

        if (playerErrorX >= Player.SLOWDOWN_RANGE)
        {
            player.exitedSlowdownRangeSinceLastSlowdown = true;
        }

        if (player.movementState == Player.STOPPED || (player.movementState == Player.MICRO_MOVE && playerErrorX > 0))
        {
            if (playerErrorX >= Player.SLOWDOWN_RANGE)
            {
                player.movementState = Player.SPEEDING_UP;
            }
            else if (playerErrorX >= Player.LOCKOUT_RANGE)
            {
                player.movementState = Player.MICRO_MOVE;
            }
        }
        if (player.movementState == Player.MICRO_MOVE && playerErrorX < Player.LOCKOUT_RANGE)
        {
            player.movementState = Player.STOPPED;
        }
        if (player.movementState == Player.SPEEDING_UP && playerErrorX < Player.SLOWDOWN_RANGE && Math.abs(touchVelocity) < 50)
        {
            player.movementState = Player.SLOWING_DOWN;
        }
        if (player.movementState == Player.SLOWING_DOWN && (playerErrorX == 0 || player.velocity == 0))
        {
            player.movementState = Player.STOPPED;
        }
        if (player.movementState == Player.MICRO_MOVE && playerErrorX == 0)
        {
            player.movementState = Player.STOPPED;
        }
        if (!touching)
        {
            if (player.velocity == 0)
                player.movementState = Player.STOPPED;
            else
                player.movementState = Player.SLOWING_DOWN;
            if (player.direction == Player.RIGHT)
            {
                playerErrorX = Player.SLOWDOWN_RANGE - 1;
            }
            else if (player.direction == Player.LEFT)
            {
                playerErrorX = 1 - Player.SLOWDOWN_RANGE;
            }
        }

        //Set velocities
        if (player.movementState == Player.SPEEDING_UP)
        {
            double deccel = Player.ABSOLUTE_ACCELERATION * Player.FRICTION_MULTIPLIER;
            if (player.direction == Player.RIGHT)
            {
                if (player.velocity < 0)
                {
                    player.velocity += deccel;
                }
                else {
                    player.velocity += player.acceleration;
                }
            }
            else if (player.direction == Player.LEFT)
            {
                if (player.velocity > 0)
                {
                    player.velocity -= deccel;
                }
                else {
                    player.velocity -= player.acceleration;
                }
            }
            else if (player.direction == Player.NONE)
            {
                ;
            }
        }
        else if (player.movementState == Player.SLOWING_DOWN)
        {
            double deccel = Player.ABSOLUTE_ACCELERATION * Player.FRICTION_MULTIPLIER;
            if (player.direction == Player.RIGHT)
            {
                player.velocity -= deccel;
                if (player.velocity < 0) player.velocity = 0;
            }
            else if (player.direction == Player.LEFT)
            {
                player.velocity += deccel;
                if (player.velocity > 0) player.velocity = 0;
            }
            else if (player.direction == Player.NONE)
            {
                ;
            }
        }
        else if (player.movementState == Player.STOPPED)
        {
            if (player.direction == Player.RIGHT)
            {
                player.velocity = 0;
            }
            else if (player.direction == Player.LEFT)
            {
                player.velocity = 0;
            }
            else if (player.direction == Player.NONE)
            {
                player.velocity = 0;
            }
        }
        else if (player.movementState == Player.MICRO_MOVE)
        {
            if (player.direction == Player.RIGHT)
            {
                player.velocity = Player.ABSOLUTE_MINSPEED;
            }
            else if (player.direction == Player.LEFT)
            {
                player.velocity = -Player.ABSOLUTE_MINSPEED;
            }
            else if (player.direction == Player.NONE)
            {
                player.velocity = 0;
            }
        }
        player.velocity = clamp(player.velocity, Player.ABSOLUTE_TOPSPEED);
        player.x += player.velocity * delta;
        player.x = (int) clamp((double) player.x, 0.0, (double) width);*/
}
