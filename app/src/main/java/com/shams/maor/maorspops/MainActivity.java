package com.shams.maor.maorspops;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import java.util.Random;

/**
 * Copyright © 2017 Maor Shams. All rights reserved.
 */

public class MainActivity extends AppCompatActivity {

    private LinearLayout mainActivity;
    private final int columns = 5, rows = 8;
    private final String USER_SETTINGS = "user_settings", USER_LEVEL = "user_level";
    private ViewGroup[] vColumns = new ViewGroup[columns];
    private Util util;
    private SharedPreferences sp;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = (LinearLayout) findViewById(R.id.activity_main);

        // get user saved level, default -> 3
        sp = getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
        level = sp.getInt(USER_LEVEL, 3);

        // create another custom array with selected array
        util = new Util();
        util.addToArr(level);
        startGame();
    }

    private void startGame() {
        for (int i = 0; i < columns; i++) {
            // save references to each column
            ViewGroup col = (ViewGroup) getLayoutInflater().inflate(R.layout.column, null);
            vColumns[i] = col;

            //  fill each column with pop
            for (int j = 0; j < rows; j++) {
                View pop = getLayoutInflater().inflate(R.layout.pop, null);
                setView(pop);
                col.addView(pop);
            }
            // add column to root view
            mainActivity.addView(col);
        }
    }

    // check if there is no more pops
    private boolean isGameOver() {
        for (ViewGroup v : vColumns) {
            if (v.getChildCount() > 0) return false;
        }
        return true;
    }

    // when clicking on pop
    public void onClickPOP(View view) {
        // column of pop
        LinearLayout parent = ((LinearLayout) view.getParent());
        // index of pop from bottom to top
        int index = parent.getChildCount() - parent.indexOfChild(view);
        // if 4 in row
        if (isPerfectLine(index)) {
            for (int i = 0; i < columns; i++) {       //remove line
                parent = (LinearLayout) mainActivity.getChildAt(i);
                parent.removeViewAt(parent.getChildCount() - index);
            }
        } else { //remove 1 candy
            ((LinearLayout) view.getParent()).removeView(view);
        }

        // check if there is no more pops, show alert
        if (isGameOver()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.game_over)
                    .setMessage(R.string.start_again)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startGame();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_medal)
                    .show();
        }
    }

    //Check that all items are the same (4 in row)
    private boolean isPerfectLine(int index) {
        int counter = 0; //counter for same color
        ColorFilter lastInt = new ColorFilter();

        for (int i = 0; i < columns; i++) {
            LinearLayout parent = (LinearLayout) mainActivity.getChildAt(i);
            if (parent.getChildCount() - index < 0) break;
            View sameCandyColumn = parent.getChildAt(parent.getChildCount() - index);
            if (lastInt.equals(sameCandyColumn.getBackground().getColorFilter())) counter++;
            lastInt = sameCandyColumn.getBackground().getColorFilter();
        }
        return (counter == columns - 1);
    }

    // fill each pop with random color
    private void setView(View view) {
        Drawable drawable = getResources().getDrawable(R.drawable.circle, null);
        drawable.setColorFilter(util.getColorsArr()[new Random().nextInt(util.levelColors.size())], PorterDuff.Mode.SRC_ATOP);
        view.setBackground(drawable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // when clicking reset
        if (item.getItemId() == R.id.menu_reset) {
            mainActivity.removeAllViews();
            startGame();
        } else {
            selectLevel();
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectLevel() {
        // Programmatically create RatingBar
        final RatingBar rating = new RatingBar(this);
        rating.setNumStars(4);
        rating.setStepSize(1);
        rating.setRating(level);
        rating.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout parent = new LinearLayout(this);
        parent.setGravity(Gravity.CENTER);
        parent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        parent.addView(rating);

        // AlertDialog - select level
        AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        popDialog.setTitle(R.string.select_level)
                .setView(parent)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                saveUserLevel((int) rating.getRating());
                            }
                        }).create().show();
    }

    private void saveUserLevel(int rating) {
        // remove old pops, custom array
        util.levelColors = null;
        mainActivity.removeAllViews();
        // save in prefs
        sp.edit().putInt(USER_LEVEL, rating).apply();
        // set level, start game
        level = rating;
        util.addToArr(rating);
        startGame();
    }
}