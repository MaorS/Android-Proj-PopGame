package com.shams.maor.maorspops;

import android.graphics.Color;

import java.util.HashSet;
import java.util.Random;

/**
 * Copyright © 2017 Maor Shams. All rights reserved.
 */

class Util {

    HashSet<Integer> levelColors;

    // colors bank
    private int[] colors = new int[]{
            Color.parseColor("#9a3ddf"), Color.parseColor("#009113"),
            Color.parseColor("#177dcb"), Color.parseColor("#dc5110"),
            Color.parseColor("#0138ff"), Color.parseColor("#971244"),
            Color.parseColor("#FFFF00"), Color.parseColor("#129793"),
            Color.parseColor("#BB0000"), Color.parseColor("#feaa1d")
    };

    // add to unique random color array,
    // each level the user get different colors
    void addToArr(int level) {
        if (levelColors == null) levelColors = new HashSet<>();
        while (levelColors.size() < level * 2) {
            levelColors.add(getRandomColor());
        }
    }

    // ger random color from colors array
    private int getRandomColor() {
        return colors[new Random().nextInt(colors.length)];
    }

    // get Integers array from HashSet
    Integer[] getColorsArr() {
        if (levelColors.isEmpty()) return null;
        return levelColors.toArray(new Integer[levelColors.size()]);
    }
}
