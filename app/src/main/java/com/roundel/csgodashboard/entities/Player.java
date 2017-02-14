package com.roundel.csgodashboard.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Krzysiek on 2017-02-13.
 */
public class Player
{
    private static final String TAG = Player.class.getSimpleName();

    //<editor-fold desc="private variables">
    private String name;
    private String steamId;

    private boolean hasHelmet;
    private int health;
    private int armor;

    private int round_kills;
    private int round_kills_hs;

    private int money;

    private int kills;
    private int assists;
    private int deaths;
    private int mvp;
    private int score;

    private Activity activity;
    private android.app.Activity activity1;
    //</editor-fold>


    private Player(String name, String steamId, boolean hasHelmet, int health, int armor, int round_kills, int round_kills_hs, int money, int kills, int assists, int deaths, int mvp, int score, Activity activity)
    {
        this.name = name;
        this.steamId = steamId;
        this.hasHelmet = hasHelmet;
        this.health = health;
        this.armor = armor;
        this.round_kills = round_kills;
        this.round_kills_hs = round_kills_hs;
        this.money = money;
        this.kills = kills;
        this.assists = assists;
        this.deaths = deaths;
        this.mvp = mvp;
        this.score = score;
        this.activity = activity;
    }

    /**
     * @param jsonObject a {@link JSONObject} to create the instance from. Format: <code><br>{<br>
     *                   "steamid": "(...)",<br> "name": "(...)",<br> "weapons": {(...)}, <br>
     *                   "match_stats": {(...)}<br> }</code>
     *
     * @return a new {@link Player} instance
     * @throws JSONException when the <code>jsonObject</code> was incorrect
     */
    public static Player fromJSON(JSONObject jsonObject) throws JSONException
    {
        return new Player(null, null, true, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Activity.PLAYING);
    }

    private void update(JSONObject jsonObject) throws JSONException
    {

    }

    /**
     * Don't confuse with {@link android.app.Activity}
     */
    private enum Activity
    {
        PLAYING, MENU, TEXT_INPUT
    }
}
