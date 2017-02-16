package com.roundel.csgodashboard.entities;

/**
 * Created by Krzysiek on 2017-02-15.
 */

public interface RoundEvents
{
    void onBombPlanted(long serverTimestamp);

    void onBombExploded(long serverTimestamp);

    void onBombDefused(long serverTimestamp);

    void onFreezeTimeStart(long serverTimestamp);

    void onRoundStart(long serverTimestamp);

    void onRoundEnd(long serverTimestamp);

    void onMatchStart(long serverTimestamp);

    void onWarmupStart(long serverTimestamp);

}