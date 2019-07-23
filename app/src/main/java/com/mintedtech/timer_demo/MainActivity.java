package com.mintedtech.timer_demo;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity
{
    private Handler mHandler;
    private Runnable mRunnable;

    private TextView mTextViewSecondsElapsed;
    private FloatingActionButton mFab;

    private boolean mTimerPaused;
    private long mSecondsElapsed;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        setupToolbar ();
        setupViewReferences ();
        setupTimer ();
        pauseAndResetTimerOnInitialRun (savedInstanceState);
        setupFAB ();
    }

    private void setupToolbar ()
    {
        Toolbar toolbar = findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);
    }

    private void setupViewReferences ()
    {
        mTextViewSecondsElapsed = findViewById (R.id.tv_seconds_elapsed);
        mFab = findViewById (R.id.fab);
    }

    private void setupTimer () {
        // Create the Handler object
        mHandler = new Handler ();

        // Create the Runnable that, after being called,
        // calls the on timer tick method and then itself one second later, and on and on...
        mRunnable = new Runnable() {
            @Override
            public void run() {
                onTimerTick ();
                mHandler.postDelayed(this, 1000);
            }
        };
    }

    private void pauseAndResetTimerOnInitialRun (Bundle savedInstanceState)
    {
        // If this is being called on initial run as opposed to due to a device rotation
        if (savedInstanceState == null) {
            mTimerPaused = true;
            resetTimer ();
        }
    }

    private void resetTimer ()
    {
        mSecondsElapsed = 0;
        updateTextView ();
    }

    private void updateTextView ()
    {
        mTextViewSecondsElapsed.setText(String.format (Locale.US,"%d", mSecondsElapsed));
    }

    private void setupFAB ()
    {
        FloatingActionButton fab = findViewById (R.id.fab);
        fab.setOnClickListener (new View.OnClickListener ()
        {
            @Override
            public void onClick (View view)
            {
                pauseResumeTimer ();
            }
        });
    }

    private void pauseResumeTimer ()
    {
        if (mTimerPaused) {
            resumeTimer ();
        }
        else {
            pauseTimer ();
        }
    }

    private void resumeTimer() {
        mTimerPaused = false;
        mHandler.postDelayed(mRunnable, 1000);

        mFab.setImageDrawable (
                ContextCompat.getDrawable(this,android.R.drawable.ic_media_pause));

    }

    private void pauseTimer ()
    {
        mFab.setImageDrawable (
                ContextCompat.getDrawable(this,android.R.drawable.ic_media_play));

        mHandler.removeCallbacks(mRunnable);
        mTimerPaused = true;
    }

    @Override protected void onResume ()
    {
        super.onResume ();

        // If the timer was running then resume it
        if (!mTimerPaused)
            resumeTimer ();
    }

    @Override protected void onPause ()
    {
        super.onPause ();

        // If the timer is running then temporarily pause the timer.
        // But then reset the flag to false so that, when the app resumes,
        // it will also resume the timer automatically
        if (!mTimerPaused) {
            pauseTimer ();
            mTimerPaused = false;
        }
    }

    /**
     * This is the method that runs after each second has elapsed
     */
    private void onTimerTick ()
    {
        mSecondsElapsed++;
        updateTextView ();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        if (id == R.id.reset_timer) {
            resetTimer ();
            return true;
        }

        return super.onOptionsItemSelected (item);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        // get saved elapsed seconds and put this into the EditText
        mSecondsElapsed = savedInstanceState.getLong("SECONDS_ELAPSED");
        updateTextView ();

        // get saved timer status
        mTimerPaused = savedInstanceState.getBoolean("TIMER_PAUSED");
        // onResume(), called later in the Activity Lifecycle,
        // will resume the timer if it was not paused
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("SECONDS_ELAPSED", mSecondsElapsed);
        outState.putBoolean("TIMER_PAUSED", mTimerPaused);
    }
}
