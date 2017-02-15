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

    private Bomb bomb;

    private Mode mode;

    private RoundEvents roundEventsListener;
    //</editor-fold>


    public GameState(String nameHome, String nameAway, int scoreHome, int scoreAway, int round, Map map, Phase mapPhase, Phase roundPhase, Player player, Bomb bomb, Mode mode)
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
        this.bomb = bomb;
        this.mode = mode;
    }

    public static GameState fromJSON(JSONObject jsonObject) throws JSONException
    {
        final JSONObject player = jsonObject.getJSONObject("player");
        final JSONObject map = jsonObject.getJSONObject("map");
        final JSONObject round = jsonObject.getJSONObject("round");

        Team team;
        try
        {
            if(Objects.equals(player.getString("team").toLowerCase(), "t"))
                team = Team.T;
            else if(Objects.equals(player.getString("team").toLowerCase(), "ct"))
                team = Team.CT;
            else
                team = Team.NONE;
        }
        catch(JSONException e)
        {
            team = Team.NONE;
        }

        Bomb bomb;
        try
        {
            bomb = getBombFromString(round.getString("bomb"));
        }
        catch(JSONException e)
        {
            bomb = null;
        }
        int roundNumber;
        try
        {
            roundNumber = map.getInt("round");
        }
        catch(JSONException e)
        {
            roundNumber = 0;
        }

        JSONObject home;
        try
        {
            home = team == Team.T ? map.getJSONObject("team_t") : map.getJSONObject("team_ct");
        }
        catch(JSONException e)
        {
            home = null;
        }
        JSONObject away;
        try
        {
            away = (team == Team.T ? map.getJSONObject("team_ct") : map.getJSONObject("team_t"));
        }
        catch(JSONException e)
        {
            away = null;
        }

        String nameHome = null;
        int scoreHome = 0;
        if(home != null)
        {
            try
            {
                nameHome = home.getString("name");
            }
            catch(JSONException ignored)
            {

            }
            try
            {
                scoreHome = home.getInt("score");
            }
            catch(JSONException e)
            {
                scoreHome = 0;
            }
        }

        String nameAway = null;
        int scoreAway = 0;
        if(home != null)
        {
            try
            {
                nameAway = away.getString("name");
            }
            catch(JSONException ignored)
            {

            }
            try
            {
                scoreAway = away.getInt("score");
            }
            catch(JSONException ignored)
            {

            }
        }

        Mode mode;
        try
        {
            mode = getModeFromString(map.getString("mode"));
        }
        catch(JSONException e)
        {
            mode = null;
        }

        Phase roundPhase;
        try
        {
            roundPhase = getPhaseFromString(round.getString("phase"));
        }
        catch(JSONException e)
        {
            roundPhase = null;
        }
        Phase mapPhase;
        try
        {
            mapPhase = getPhaseFromString(map.getString("phase"));
        }
        catch(JSONException e)
        {
            mapPhase = null;
        }

        Player playerObject = Player.fromJSON(player);

        return new GameState(nameHome, nameAway, scoreHome, scoreAway, roundNumber, null, mapPhase, roundPhase, playerObject, bomb, mode);
    }


    private static GameState fromJSONString(String string) throws JSONException
    {
        return fromJSON(new JSONObject(string));
    }

    private static Bomb getBombFromString(String bomb)
    {
        switch(bomb)
        {
            case "planted":
                return Bomb.PLANTED;
            case "defused":
                return Bomb.DEFUSED;
            case "exploded":
                return Bomb.EXPLODED;
            default:
                return Bomb.NONE;
        }
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
            case "warmup":
                return Phase.WARMUP;
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

    public void update(JSONObject jsonObject)
    {
        JSONObject player;
        try
        {
            player = jsonObject.getJSONObject("player");
        }
        catch(JSONException e)
        {
            player = new JSONObject();
        }
        JSONObject map;
        try
        {
            map = jsonObject.getJSONObject("map");
        }
        catch(JSONException e)
        {
            map = new JSONObject();
        }
        JSONObject round;
        try
        {
            round = jsonObject.getJSONObject("round");
        }
        catch(JSONException e)
        {
            round = new JSONObject();
        }

        Team team;
        try
        {
            if(Objects.equals(player.getString("team").toLowerCase(), "t"))
                team = Team.T;
            else if(Objects.equals(player.getString("team").toLowerCase(), "ct"))
                team = Team.CT;
            else
                team = Team.NONE;
        }
        catch(JSONException e)
        {
            team = Team.NONE;
        }

        try
        {

            final Bomb bomb = getBombFromString(round.getString("bomb"));
            if(roundEventsListener != null)
            {
                if(this.bomb != bomb && bomb == Bomb.PLANTED)
                    roundEventsListener.onBombPlanted();
                if(this.bomb != bomb && bomb == Bomb.DEFUSED)
                    roundEventsListener.onBombDefused();
            }
            this.bomb = bomb;
        }
        catch(JSONException e)
        {
            this.bomb = null;
        }
        try
        {
            this.round = map.getInt("round");
        }
        catch(JSONException e)
        {
            this.round = 0;
        }

        JSONObject home;
        try
        {
            home = team == Team.T ? map.getJSONObject("team_t") : map.getJSONObject("team_ct");
        }
        catch(JSONException e)
        {
            home = null;
        }
        JSONObject away;
        try
        {
            away = team == Team.T ? map.getJSONObject("team_ct") : map.getJSONObject("team_t");
        }
        catch(JSONException e)
        {
            away = null;
        }

        if(home != null)
        {
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
                this.scoreHome = home.getInt("score");
            }
            catch(JSONException e)
            {
                this.scoreHome = 0;
            }
        }

        if(away != null)
        {
            try
            {
                this.nameAway = away.getString("name");
            }
            catch(JSONException e)
            {
                this.nameAway = null;
            }
            try
            {
                this.scoreAway = away.getInt("score");
            }
            catch(JSONException e)
            {
                this.scoreAway = 0;
            }
        }

        try
        {
            this.mode = getModeFromString(map.getString("mode"));
        }
        catch(JSONException e)
        {
            this.mode = null;
        }

        try
        {
            final Phase phase = getPhaseFromString(round.getString("phase"));
            if(roundEventsListener != null)
            {
                if(this.roundPhase != phase && phase == Phase.LIVE)
                    roundEventsListener.onRoundStart();
                if(this.roundPhase != phase && phase == Phase.OVER)
                    roundEventsListener.onRoundEnd();
            }
            this.roundPhase = phase;
        }
        catch(JSONException e)
        {
            this.roundPhase = null;
        }

        try
        {
            final Phase phase = getPhaseFromString(map.getString("phase"));
            if(roundEventsListener != null)
            {
                if(this.mapPhase != phase && phase == Phase.LIVE)
                    roundEventsListener.onMatchStart();
                if(this.mapPhase != phase && phase == Phase.WARMUP)
                    roundEventsListener.onMatchEnd();
            }
            this.mapPhase = phase;
        }
        catch(JSONException e)
        {
            this.mapPhase = null;
        }

        this.player.update(player);
    }

    public void setRoundEventsListener(RoundEvents roundEventsListener)
    {
        this.roundEventsListener = roundEventsListener;
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
        LIVE, FREEZE_TIME, WARMUP, OVER
    }

    private enum Mode
    {
        COMPETITIVE, CASUAL, ARMS_RACE, DEATHMATCH, DEMOLITION
    }

    public enum Bomb
    {
        NONE, EXPLODED, DEFUSED, PLANTED
    }

    public interface RoundEvents
    {
        void onBombPlanted();

        void onBombDefused();

        void onRoundStart();

        void onRoundEnd();

        void onMatchStart();

        void onMatchEnd();

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
         */
        private static Player fromJSON(JSONObject jsonObject)
        {
            JSONObject state;
            try
            {
                state = jsonObject.getJSONObject("state");
            }
            catch(JSONException e)
            {
                state = new JSONObject();
            }
            JSONObject stats;
            try
            {
                stats = jsonObject.getJSONObject("match_stats");
            }
            catch(JSONException e)
            {
                stats = new JSONObject();
            }

            String steamId;
            try
            {
                steamId = jsonObject.getString("steamid");
            }
            catch(JSONException e)
            {
                steamId = null;
            }
            String name;
            try
            {
                name = jsonObject.getString("name");
            }
            catch(JSONException e)
            {
                name = null;
            }

            Team team;
            try
            {
                if(Objects.equals(jsonObject.getString("team").toLowerCase(), "t"))
                    team = Team.T;
                else if(Objects.equals(jsonObject.getString("team").toLowerCase(), "ct"))
                    team = Team.CT;
                else
                    team = Team.NONE;
            }
            catch(JSONException e)
            {
                team = Team.NONE;
            }

            Activity activity;
            try
            {
                activity = getActivityFromString(jsonObject.getString("activity"));
            }
            catch(JSONException e)
            {
                activity = null;
            }

            int health;
            try
            {
                health = state.getInt("health");
            }
            catch(JSONException e)
            {
                health = 0;
            }
            int armor;
            try
            {
                armor = state.getInt("armor");
            }
            catch(JSONException e)
            {
                armor = 0;
            }
            boolean hasHelmet;
            try
            {
                hasHelmet = state.getBoolean("helmet");
            }
            catch(JSONException e)
            {
                hasHelmet = false;
            }

            int round_kills;
            try
            {
                round_kills = state.getInt("round_kills");
            }
            catch(JSONException e)
            {
                round_kills = 0;
            }
            int round_kills_hs;
            try
            {
                round_kills_hs = state.getInt("round_killhs");
            }
            catch(JSONException e)
            {
                round_kills_hs = 0;
            }

            int money;
            try
            {
                money = state.getInt("money");
            }
            catch(JSONException e)
            {
                money = 0;
            }

            int kills;
            try
            {
                kills = stats.getInt("kills");
            }
            catch(JSONException e)
            {
                kills = 0;
            }
            int assists;
            try
            {
                assists = stats.getInt("assists");
            }
            catch(JSONException e)
            {
                assists = 0;
            }
            int deaths;
            try
            {
                deaths = stats.getInt("deaths");
            }
            catch(JSONException e)
            {
                deaths = 0;
            }
            int mvp;
            try
            {
                mvp = stats.getInt("mvps");
            }
            catch(JSONException e)
            {
                mvp = 0;
            }
            int score;
            try
            {
                score = stats.getInt("score");
            }
            catch(JSONException e)
            {
                score = 0;
            }

            return new Player(name, steamId, team, hasHelmet, health, armor, round_kills, round_kills_hs, money, kills, assists, deaths, mvp, score, activity);
        }

        private static Activity getActivityFromString(String activity)
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

        private void update(JSONObject jsonObject)
        {
            JSONObject state;
            try
            {
                state = jsonObject.getJSONObject("state");
            }
            catch(JSONException e)
            {
                state = new JSONObject();
            }
            JSONObject stats;
            try
            {
                stats = jsonObject.getJSONObject("match_stats");
            }
            catch(JSONException e)
            {
                stats = new JSONObject();
            }

            try
            {
                this.steamId = jsonObject.getString("steamid");
            }
            catch(JSONException e)
            {
                this.steamId = null;
            }

            try
            {
                this.name = jsonObject.getString("name");
            }
            catch(JSONException e)
            {
                this.name = null;
            }

            try
            {
                if(Objects.equals(jsonObject.getString("team").toLowerCase(), "t"))
                    this.team = Team.T;
                else if(Objects.equals(jsonObject.getString("team").toLowerCase(), "ct"))
                    this.team = Team.CT;
                else
                    this.team = Team.NONE;
            }
            catch(JSONException e)
            {
                this.team = Team.NONE;
            }


            try
            {
                this.activity = getActivityFromString(jsonObject.getString("activity"));
            }
            catch(JSONException e)
            {
                this.activity = null;
            }


            try
            {
                this.health = state.getInt("health");
            }
            catch(JSONException e)
            {
                this.health = 0;
            }

            try
            {
                this.armor = state.getInt("armor");
            }
            catch(JSONException e)
            {
                this.armor = 0;
            }
            try
            {
                this.hasHelmet = state.getBoolean("helmet");
            }
            catch(JSONException e)
            {
                this.hasHelmet = false;
            }

            try
            {
                this.round_kills = state.getInt("round_kills");
            }
            catch(JSONException e)
            {
                this.round_kills = 0;
            }
            try
            {
                this.round_kills_hs = state.getInt("round_killhs");
            }
            catch(JSONException e)
            {
                this.round_kills_hs = 0;
            }

            try
            {
                this.money = state.getInt("money");
            }
            catch(JSONException e)
            {
                this.money = 0;
            }

            try
            {
                this.kills = stats.getInt("kills");
            }
            catch(JSONException e)
            {
                this.kills = 0;
            }
            try
            {
                this.assists = stats.getInt("assists");
            }
            catch(JSONException e)
            {
                this.assists = 0;
            }

            try
            {
                this.deaths = stats.getInt("deaths");
            }
            catch(JSONException e)
            {
                this.deaths = 0;
            }
            try
            {
                this.mvp = stats.getInt("mvps");
            }
            catch(JSONException e)
            {
                this.mvp = 0;
            }
            try
            {
                this.score = stats.getInt("score");
            }
            catch(JSONException e)
            {
                this.score = 0;
            }
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
