package com.roundel.csgodashboard.entities;

import java.util.ArrayList;

/**
 * Created by Krzysiek on 2017-02-12.
 */
public class MoneyInfo extends ArrayList<MoneyInfo.Entry>
{
    private static final String TAG = MoneyInfo.class.getSimpleName();

    //<editor-fold desc="private variables">
    private int mHalfGameRound = Integer.MAX_VALUE;
//</editor-fold>

    @Override
    public boolean add(Entry entry)
    {
        if(hasRound(entry.round))
            return false;
        return super.add(entry);
    }

    public int getMaxMoney()
    {
        int max = -1;
        for(Entry entry : this)
        {
            if(entry.money > max)
            {
                max = entry.money;
            }
        }
        return max;
    }

    public int getMaxRound()
    {
        int max = -1;
        for(Entry entry : this)
        {
            if(entry.round > max)
            {
                max = entry.round;
            }
        }
        return max;
    }

    public int getHalfGameRound()
    {
        return mHalfGameRound;
    }

    public void setHalfGameRound(int halfGameRound)
    {
        this.mHalfGameRound = halfGameRound;
    }

    private boolean hasRound(int round)
    {
        for(Entry entry : this)
        {
            if(entry.round == round)
                return true;
        }
        return false;
    }

    public static class Entry
    {
        //<editor-fold desc="private variables">
        int round;
        int money;
        float x;
        float y;
        //</editor-fold>

        public Entry(int round, int money)
        {
            this.round = round;
            this.money = money;
        }

        @Override
        public String toString()
        {
            return "Entry{" +
                    "round=" + round +
                    ", money=" + money +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }

        public int getRound()
        {
            return round;
        }

        public int getMoney()
        {
            return money;
        }

        public float getX()
        {
            return x;
        }

        public void setX(float x)
        {
            this.x = x;
        }

        public float getY()
        {
            return y;
        }

        public void setY(float y)
        {
            this.y = y;
        }
    }
}
