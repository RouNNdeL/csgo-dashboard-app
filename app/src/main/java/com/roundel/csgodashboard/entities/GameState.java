package com.roundel.csgodashboard.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * Created by Krzysiek on 2017-02-13.
 */
public class GameState
{
    private static final String TAG = GameState.class.getSimpleName();

    //<editor-fold desc="private variables">
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


    private GameState(String nameHome, String nameAway, int scoreHome, int scoreAway, int round, Map map, Phase mapPhase, Phase roundPhase, Player player)
    {
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

        return new GameState(nameHome, nameAway, scoreHome, scoreAway, roundNumber, null, mapPhase, roundPhase, playerObject);
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
                return null;
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
                return null;
        }
    }

    @Override
    public String toString()
    {
        return "GameState{" +
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

        Team team;
        try
        {
            team = Objects.equals(player.getString("team"), "T") ? Team.T : Team.CT;
        }
        catch(JSONException e)
        {
            team = Team.NONE;
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

    public enum Team
    {
        CT, T, NONE
    }

    private enum Phase
    {
        LIVE, FREEZE_TIME, OVER
    }

    private enum Mode
    {
        COMPETITIVE, CASUAL, ARMS_RACE, DEATHMATCH, DEMOLITION
    }

    /**
     * Created by Krzysiek on 2017-02-13.
     */
    public static class Player
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
        private Team team;
        //</editor-fold>


        private Player(String name, String steamId, Team team, boolean hasHelmet, int health, int armor, int round_kills, int round_kills_hs, int money, int kills, int assists, int deaths, int mvp, int score, Activity activity)
        {
            this.name = name;
            this.steamId = steamId;
            this.team = team;
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
         * @param jsonObject a {@link JSONObject} to create the instance from. Format:
         *                   <code><br>{<br> "steamid": "(...)",<br> "name": "(...)",<br> "weapons":
         *                   {(...)}, <br> "match_stats": {(...)}<br> }</code>
         *
         * @return a new {@link Player} instance
         * @throws JSONException when the <code>jsonObject</code> was incorrect
         */
        private static Player fromJSON(JSONObject jsonObject) throws JSONException
        {
            JSONObject state = jsonObject.getJSONObject("state");
            JSONObject stats = jsonObject.getJSONObject("match_stats");

            String steamId = jsonObject.getString("steamid");
            String name = jsonObject.getString("name");

            Team team;
            try
            {
                team = Objects.equals(jsonObject.getString("team").toLowerCase(), "t") ? Team.T : Team.CT;
            }
            catch(JSONException e)
            {
                team = Team.NONE;
            }
            Activity activity = activityFromString(jsonObject.getString("activity"));

            int health = state.getInt("health");
            int armor = state.getInt("armor");
            boolean hasHelmet = state.getBoolean("helmet");

            int round_kills = state.getInt("round_kills");
            int round_kills_hs = state.getInt("round_killhs");

            int money = state.getInt("money");

            int kills = stats.getInt("kills");
            int assists = stats.getInt("assists");
            int deaths = stats.getInt("deaths");
            int mvp = stats.getInt("mvps");
            int score = stats.getInt("score");

            return new Player(name, steamId, team, hasHelmet, health, armor, round_kills, round_kills_hs, money, kills, assists, deaths, mvp, score, activity);
        }

        private static Activity activityFromString(String activity)
        {
            switch(activity)
            {
                case "playing":
                    return Activity.PLAYING;
                case "menu":
                    return Activity.MENU;
                case "textinput":
                    return Activity.TEXT_INPUT;
                default:
                    return null;
            }
        }

        private void update(JSONObject jsonObject) throws JSONException
        {
            JSONObject state = jsonObject.getJSONObject("state");
            JSONObject stats = jsonObject.getJSONObject("match_stats");

            this.steamId = jsonObject.getString("steamid");
            this.name = jsonObject.getString("name");

            try
            {
                this.team = Objects.equals(jsonObject.getString("team").toLowerCase(), "t") ? Team.T : Team.CT;
            }
            catch(JSONException e)
            {
                this.team = Team.NONE;
            }
            this.activity = activityFromString(jsonObject.getString("activity"));

            this.health = state.getInt("health");
            this.armor = state.getInt("armor");
            this.hasHelmet = state.getBoolean("helmet");

            this.round_kills = state.getInt("round_kills");
            this.round_kills_hs = state.getInt("round_killhs");

            this.money = state.getInt("money");

            this.kills = stats.getInt("kills");
            this.assists = stats.getInt("assists");
            this.deaths = stats.getInt("deaths");
            this.mvp = stats.getInt("mvps");
            this.score = stats.getInt("score");
        }

        public String getName()
        {
            return name;
        }

        public String getSteamId()
        {
            return steamId;
        }

        public boolean hasHelmet()
        {
            return hasHelmet;
        }

        public int getHealth()
        {
            return health;
        }

        public int getArmor()
        {
            return armor;
        }

        public int getRoundKills()
        {
            return round_kills;
        }

        public int getRoundKillsHeadshots()
        {
            return round_kills_hs;
        }

        public int getMoney()
        {
            return money;
        }

        public int getKills()
        {
            return kills;
        }

        public int getAssists()
        {
            return assists;
        }

        public int getDeaths()
        {
            return deaths;
        }

        public int getMvp()
        {
            return mvp;
        }

        public String getKDRString()
        {
            DecimalFormat df = new DecimalFormat("0.##");
            return df.format(getKDR());
        }

        public float getKDR()
        {
            return deaths == 0 ? kills : kills / deaths;
        }

        public int getScore()
        {
            return score;
        }

        public Activity getActivity()
        {
            return activity;
        }

        public Team getTeam()
        {
            return team;
        }

        /**
         * Don't confuse with {@link android.app.Activity}
         */
        private enum Activity
        {
            PLAYING, MENU, TEXT_INPUT
        }
    }
}
