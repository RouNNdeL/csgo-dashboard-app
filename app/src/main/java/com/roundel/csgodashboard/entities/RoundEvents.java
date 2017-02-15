package com.roundel.csgodashboard.entities;

/**
 * Created by Krzysiek on 2017-02-15.
 */

public interface RoundEvents
{
    void onBombPlanted();

    void onBombExploded();

    void onBombDefused();

    void onFreezeTimeStart();

    void onRoundStart();

    void onRoundEnd();

    void onMatchStart();

    void onMatchEnd();

}