package com.roundel.csgodashboard.entities;

import com.roundel.csgodashboard.util.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

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

    private MapPhase mapPhase;
    private RoundPhase roundPhase;

    private Player player;

    private Bomb bomb;

    private Mode mode;

    private RoundEvents roundEventsListener;
    //</editor-fold>


    public GameState(String nameHome, String nameAway, int scoreHome, int scoreAway, int round, Map map, MapPhase mapPhase, RoundPhase roundPhase, Player player, Bomb bomb, Mode mode)
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
        JSONObject player;
        try
        {
            player = jsonObject.getJSONObject("player");
        }
        catch(JSONException e)
        {
            player = null;
        }
        JSONObject map;
        try
        {
            map = jsonObject.getJSONObject("map");
        }
        catch(JSONException e)
        {
            map = null;
        }

        JSONObject round;
        try
        {
            round = jsonObject.getJSONObject("round");
        }
        catch(JSONException e)
        {
            round = null;
        }

        Player playerObject = Player.fromJSON(player);

        Team team = playerObject.getTeam();

        Bomb bomb = null;
        RoundPhase roundPhase = null;
        if(round != null)
        {
            try
            {
                bomb = getBombFromString(round.getString("ic_csgo_bomb"));
            }
            catch(JSONException igonerd)
            {
            }

            try
            {
                roundPhase = getRoundPhaseFromString(round.getString("phase"));
            }
            catch(JSONException ignored)
            {
            }
        }

        JSONObject home;
        JSONObject away;

        int roundNumber = 0;
        MapPhase mapPhase = null;
        Mode mode = null;

        String nameAway = null;
        int scoreAway = 0;

        String nameHome = null;
        int scoreHome = 0;
        if(map != null)
        {
            try
            {
                roundNumber = map.getInt("round");
            }
            catch(JSONException e)
            {
                roundNumber = 0;
            }
            try
            {
                mapPhase = getMapPhaseFromString(map.getString("phase"));
            }
            catch(JSONException e)
            {
                mapPhase = null;
            }

            try
            {
                home = team == Team.T ? map.getJSONObject("team_t") : map.getJSONObject("team_ct");
            }
            catch(JSONException e)
            {
                home = null;
            }
            try
            {
                away = (team == Team.T ? map.getJSONObject("team_ct") : map.getJSONObject("team_t"));
            }
            catch(JSONException e)
            {
                away = null;
            }


            try
            {
                mode = getModeFromString(map.getString("mode"));
            }
            catch(JSONException e)
            {
                mode = null;
            }

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

            if(away != null)
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
        }

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
                LogHelper.d("UnknownProperty", "Bomb: " + bomb);
                return Bomb.NONE;
        }
    }

    private static MapPhase getMapPhaseFromString(String phase)
    {
        if(phase == null)
            return null;

        switch(phase.toLowerCase())
        {
            case "live":
                return MapPhase.LIVE;
            case "warmup":
                return MapPhase.WARMUP;
            case "gameover":
                return MapPhase.GAMEOVER;
            case "intermission":
                return MapPhase.INTERMISSION;
            default:
                LogHelper.d("UnknownProperty", "MapPhase: " + phase);
                return null;
        }
    }


    private static RoundPhase getRoundPhaseFromString(String phase)
    {
        switch(phase.toLowerCase())
        {
            case "live":
                return RoundPhase.LIVE;
            case "over":
                return RoundPhase.OVER;
            case "freezetime":
                return RoundPhase.FREEZE_TIME;
            default:
                LogHelper.d("UnknownProperty", "RoundPhase: " + phase);
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
                LogHelper.d("UnknownProperty", "Mode: " + mode);
                return null;
        }
    }

    @Override
    public String toString()
    {
        return "GameState{" +
                "mode=" + mode +
                ", ic_csgo_bomb=" + bomb +
                ", player=" + player.toString() +
                ", roundPhase=" + roundPhase +
                ", mapPhase=" + mapPhase +
                ", map=" + map +
                ", round=" + round +
                ", scoreAway=" + scoreAway +
                ", scoreHome=" + scoreHome +
                ", nameAway='" + nameAway + '\'' +
                ", nameHome='" + nameHome + '\'' +
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
            player = null;
        }
        JSONObject map;
        try
        {
            map = jsonObject.getJSONObject("map");
        }
        catch(JSONException e)
        {
            map = null;
        }

        JSONObject round;
        try
        {
            round = jsonObject.getJSONObject("round");
        }
        catch(JSONException e)
        {
            round = null;
        }

        JSONObject provider;
        try
        {
            provider = jsonObject.getJSONObject("provider");
        }
        catch(JSONException e)
        {
            provider = null;
        }

        long timestamp = System.currentTimeMillis();
        if(provider != null)
        {
            //Used to trigger events at the proper time
            try
            {
                timestamp = provider.getLong("timestamp");
            }
            catch(JSONException ignored)
            {
            }
        }

        this.player.update(player);

        Team team = this.player.team;

        JSONObject home = null;
        JSONObject away = null;
        if(map != null)
        {
            try
            {
                this.round = map.getInt("round");
            }
            catch(JSONException e)
            {
                this.round = 0;
            }

            try
            {
                home = team == Team.T ? map.getJSONObject("team_t") : map.getJSONObject("team_ct");
            }
            catch(JSONException ignored)
            {
            }
            try
            {
                away = team == Team.T ? map.getJSONObject("team_ct") : map.getJSONObject("team_t");
            }
            catch(JSONException ignored)
            {
            }

            try
            {
                final MapPhase phase = getMapPhaseFromString(map.getString("phase"));
                if(roundEventsListener != null)
                {
                    if(this.mapPhase != phase && phase == MapPhase.LIVE)
                        roundEventsListener.onMatchStart(timestamp);
                    if(this.mapPhase != phase && phase == MapPhase.WARMUP)
                        roundEventsListener.onWarmupStart(timestamp);
                    if(this.mapPhase != phase && phase == MapPhase.GAMEOVER)
                        roundEventsListener.onMatchEnd(timestamp);
                }
                this.mapPhase = phase;
            }
            catch(JSONException e)
            {
                this.mapPhase = null;
            }

            try
            {
                this.mode = getModeFromString(map.getString("mode"));
            }
            catch(JSONException e)
            {
                this.mode = null;
            }
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
        else
        {
            this.nameHome = null;
            this.scoreHome = 0;
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
        else
        {
            this.nameAway = null;
            this.scoreAway = 0;
        }

        if(round != null)
        {
            try
            {
                final RoundPhase phase = getRoundPhaseFromString(round.getString("phase"));
                if(roundEventsListener != null)
                {
                    if(this.roundPhase != phase && phase == RoundPhase.LIVE)
                        roundEventsListener.onRoundStart(timestamp);
                    if(this.roundPhase != phase && phase == RoundPhase.OVER)
                        roundEventsListener.onRoundEnd(timestamp);
                    if(this.roundPhase != phase && phase == RoundPhase.FREEZE_TIME)
                        roundEventsListener.onFreezeTimeStart(timestamp);
                }
                this.roundPhase = phase;
            }
            catch(JSONException e)
            {
                this.roundPhase = null;
            }

            try
            {
                final Bomb bomb = getBombFromString(round.getString("ic_csgo_bomb"));
                if(roundEventsListener != null)
                {
                    if(this.bomb != bomb && bomb == Bomb.PLANTED)
                        roundEventsListener.onBombPlanted(timestamp);
                    if(this.bomb != bomb && bomb == Bomb.DEFUSED)
                        roundEventsListener.onBombDefused(timestamp);
                    if(this.bomb != bomb && bomb == Bomb.EXPLODED)
                        roundEventsListener.onBombExploded(timestamp);
                }
                this.bomb = bomb;
            }
            catch(JSONException e)
            {
                this.bomb = null;
            }
        }
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

    public MapPhase getMapPhase()
    {
        return mapPhase;
    }

    public RoundPhase getRoundPhase()
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

    public enum MapPhase
    {
        LIVE, WARMUP, GAMEOVER, INTERMISSION
    }

    private enum RoundPhase
    {
        LIVE, FREEZE_TIME, OVER
    }

    public enum Mode
    {
        COMPETITIVE, CASUAL, ARMS_RACE, DEATHMATCH, DEMOLITION
    }

    private enum Bomb
    {
        NONE, EXPLODED, DEFUSED, PLANTED
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
        private DecimalFormat mKdrFormat;
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
                state = null;
            }
            JSONObject stats;
            try
            {
                stats = jsonObject.getJSONObject("match_stats");
            }
            catch(JSONException e)
            {
                stats = null;
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
                team = getTeamFromString(jsonObject.getString("team"));
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
            int armor;
            boolean hasHelmet;
            int round_kills;
            int round_kills_hs;
            int money;
            if(state != null)
            {
                try
                {
                    health = state.getInt("health");
                }
                catch(JSONException e)
                {
                    health = 0;
                }
                try
                {
                    armor = state.getInt("armor");
                }
                catch(JSONException e)
                {
                    armor = 0;
                }
                try
                {
                    hasHelmet = state.getBoolean("helmet");
                }
                catch(JSONException e)
                {
                    hasHelmet = false;
                }

                try
                {
                    round_kills = state.getInt("round_kills");
                }
                catch(JSONException e)
                {
                    round_kills = 0;
                }
                try
                {
                    round_kills_hs = state.getInt("round_killhs");
                }
                catch(JSONException e)
                {
                    round_kills_hs = 0;
                }

                try
                {
                    money = state.getInt("money");
                }
                catch(JSONException e)
                {
                    money = 0;
                }
            }
            else
            {
                health = 0;
                armor = 0;
                hasHelmet = false;
                round_kills = 0;
                round_kills_hs = 0;
                money = 0;
            }

            int kills;
            int assists;
            int deaths;
            int mvp;
            int score;
            if(stats != null)
            {
                try
                {
                    kills = stats.getInt("kills");
                }
                catch(JSONException e)
                {
                    kills = 0;
                }
                try
                {
                    assists = stats.getInt("assists");
                }
                catch(JSONException e)
                {
                    assists = 0;
                }
                try
                {
                    deaths = stats.getInt("deaths");
                }
                catch(JSONException e)
                {
                    deaths = 0;
                }
                try
                {
                    mvp = stats.getInt("mvps");
                }
                catch(JSONException e)
                {
                    mvp = 0;
                }
                try
                {
                    score = stats.getInt("score");
                }
                catch(JSONException e)
                {
                    score = 0;
                }
            }
            else
            {
                kills = 0;
                assists = 0;
                deaths = 0;
                mvp = 0;
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
                    LogHelper.d("UnknowProperty", "Activity: " + activity);
                    return null;
            }
        }

        private static Team getTeamFromString(String team)
        {
            if(team == null)
                return Team.NONE;

            switch(team.toLowerCase())
            {
                case "t":
                    return Team.T;
                case "ct":
                    return Team.CT;
                default:
                    LogHelper.d("UnknownProperty", "Team: " + team);
                    return Team.NONE;
            }
        }

        @Override
        public String toString()
        {
            return "Player{" +
                    "name='" + name + '\'' +
                    ", steamId='" + steamId + '\'' +
                    ", hasHelmet=" + hasHelmet +
                    ", health=" + health +
                    ", armor=" + armor +
                    ", round_kills=" + round_kills +
                    ", round_kills_hs=" + round_kills_hs +
                    ", money=" + money +
                    ", kills=" + kills +
                    ", assists=" + assists +
                    ", deaths=" + deaths +
                    ", mvp=" + mvp +
                    ", score=" + score +
                    ", activity=" + activity +
                    ", team=" + team +
                    '}';
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
                state = null;
            }
            JSONObject stats;
            try
            {
                stats = jsonObject.getJSONObject("match_stats");
            }
            catch(JSONException e)
            {
                stats = null;
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
                team = getTeamFromString(jsonObject.getString("team"));
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


            if(state != null)
            {
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
            }

            if(stats != null)
            {
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

        public String getKdrString()
        {
            mKdrFormat = new DecimalFormat("0.##");
            return mKdrFormat.format(getKdr());
        }

        public float getKdr()
        {
            return deaths == 0 ? kills : ((float) kills) / ((float) deaths);
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
