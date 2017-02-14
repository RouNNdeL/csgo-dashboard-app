package com.roundel.csgodashboard.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by Krzysiek on 2017-02-13.
 */
public class GameState
{
    private static final String TAG = GameState.class.getSimpleName();

    //<editor-fold desc="private variables">
    private Team team;

    private String nameHome;
    private String nameAway;

    private int scoreHome;
    private int scoreAway;

    private int round;

    private Map map;

    private Phase mapPhase;
    private Phase roundPhase;

    private Player player;
    //</editor-fold>


    private GameState(Team team, String nameHome, String nameAway, int scoreHome, int scoreAway, int round, Map map, Phase mapPhase, Phase roundPhase, Player player)
    {
        this.team = team;
        this.nameHome = nameHome;
        this.nameAway = nameAway;
        this.scoreHome = scoreHome;
        this.scoreAway = scoreAway;
        this.round = round;
        this.map = map;
        this.mapPhase = mapPhase;
        this.roundPhase = roundPhase;
        this.player = player;
    }

    public static GameState fromJSON(JSONObject jsonObject) throws JSONException
    {
        final JSONObject player = jsonObject.getJSONObject("player");
        final JSONObject map = jsonObject.getJSONObject("map");
        final JSONObject round = jsonObject.getJSONObject("round");

        Team team;
        try
        {
            team = Objects.equals(player.getString("team"), "T") ? Team.T : Team.CT;
        }
        catch(JSONException e)
        {
            team = Team.NONE;
        }
        int roundNumber = map.getInt("round");

        JSONObject home = team == Team.T ? map.getJSONObject("team_t") : map.getJSONObject("team_ct");
        JSONObject away = team == Team.T ? map.getJSONObject("team_ct") : map.getJSONObject("team_t");

        String nameHome;
        try
        {
            nameHome = home.getString("name");
        }
        catch(JSONException e)
        {
            nameHome = null;
        }
        String nameAway;
        try
        {
            nameAway = away.getString("name");
        }
        catch(JSONException e)
        {
            nameAway = null;
        }

        int scoreHome = home.getInt("score");
        int scoreAway = away.getInt("score");

        Phase roundPhase = getPhaseFromString(round.getString("phase"));
        Phase mapPhase = getPhaseFromString(map.getString("phase"));

        Player playerObject = Player.fromJSON(player);

        return new GameState(team, nameHome, nameAway, scoreHome, scoreAway, roundNumber, null, mapPhase, roundPhase, playerObject);
    }

    private static GameState fromJSONString(String string) throws JSONException
    {
        return fromJSON(new JSONObject(string));
    }

    private static Phase getPhaseFromString(String phase)
    {
        switch(phase.toLowerCase())
        {
            case "live":
                return Phase.LIVE;
            case "over":
                return Phase.OVER;
            case "freezetime":
                return Phase.FREEZE_TIME;
            default:
                return Phase.UNKNOWN;
        }
    }

    private static Mode getModeFromString(String mode)
    {
        switch(mode.toLowerCase())
        {
            case "competitive":
                return Mode.COMPETITIVE;
            case "casual":
                return Mode.CASUAL;
            case "deathmatch":
                return Mode.DEATHMATCH;
            default:
                return Mode.UNKNOWN;
        }
    }

    @Override
    public String toString()
    {
        return "GameState{" +
                "team=" + team +
                ", nameHome='" + nameHome + '\'' +
                ", nameAway='" + nameAway + '\'' +
                ", scoreHome=" + scoreHome +
                ", scoreAway=" + scoreAway +
                ", round=" + round +
                ", map=" + map +
                ", mapPhase=" + mapPhase +
                ", roundPhase=" + roundPhase +
                ", player=" + player +
                '}';
    }

    public void update(JSONObject jsonObject) throws JSONException
    {
        final JSONObject player = jsonObject.getJSONObject("player");
        final JSONObject map = jsonObject.getJSONObject("map");
        final JSONObject round = jsonObject.getJSONObject("round");

        try
        {
            this.team = Objects.equals(player.getString("team"), "T") ? Team.T : Team.CT;
        }
        catch(JSONException e)
        {
            this.team = Team.NONE;
        }
        this.round = map.getInt("round");

        JSONObject home = team == Team.T ? map.getJSONObject("team_t") : map.getJSONObject("team_ct");
        JSONObject away = team == Team.T ? map.getJSONObject("team_ct") : map.getJSONObject("team_t");

        try
        {
            this.nameHome = home.getString("name");
        }
        catch(JSONException e)
        {
            this.nameHome = null;
        }
        try
        {
            this.nameAway = away.getString("name");
        }
        catch(JSONException e)
        {
            this.nameAway = null;
        }

        this.scoreHome = home.getInt("score");
        this.scoreAway = away.getInt("score");

        this.roundPhase = getPhaseFromString(round.getString("phase"));
        this.mapPhase = getPhaseFromString(map.getString("phase"));

        this.player = Player.fromJSON(player);
    }

    public Team getTeam()
    {
        return team;
    }

    public String getNameHome()
    {
        return nameHome;
    }

    public String getNameAway()
    {
        return nameAway;
    }

    public int getScoreHome()
    {
        return scoreHome;
    }

    public int getScoreAway()
    {
        return scoreAway;
    }

    public int getRound()
    {
        return round;
    }

    public Map getMap()
    {
        return map;
    }

    public Phase getMapPhase()
    {
        return mapPhase;
    }

    public Phase getRoundPhase()
    {
        return roundPhase;
    }

    public Player getPlayer()
    {
        return player;
    }

    private enum Team
    {
        CT, T, NONE
    }

    private enum Phase
    {
        LIVE, FREEZE_TIME, OVER, UNKNOWN
    }

    private enum Mode
    {
        COMPETITIVE, CASUAL, ARMS_RACE, DEATHMATCH, DEMOLITION, UNKNOWN
    }
}
