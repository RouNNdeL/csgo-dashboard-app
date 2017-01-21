package com.roundel.csgodashboard.entities;

import android.content.Context;

import com.roundel.csgodashboard.R;

/**
 * Created by Krzysiek on 2017-01-21.
 */
public class Weapon
{
    public static final String TAG = "Weapon";

    public static final String WEAPON_AK47 = "weapon_ak47";
    public static final String WEAPON_AUG = "weapon_aug";
    public static final String WEAPON_AWP = "weapon_awp";
    public static final String WEAPON_BIZON = "weapon_bizon";
    public static final String WEAPON_C4 = "weapon_c4";
    public static final String WEAPON_CZ75A = "weapon_cz75a";
    public static final String WEAPON_DEAGLE = "weapon_deagle";
    public static final String WEAPON_DECOY = "weapon_decoy";
    public static final String WEAPON_ELITE = "weapon_elite";
    public static final String WEAPON_FAMAS = "weapon_famas";
    public static final String WEAPON_FIVESEVEN = "weapon_fiveseven";
    public static final String WEAPON_FLASHBANG = "weapon_flashbang";
    public static final String WEAPON_G3SG1 = "weapon_g3sg1";
    public static final String WEAPON_GALILAR = "weapon_galilar";
    public static final String WEAPON_GLOCK = "weapon_glock";
    public static final String WEAPON_HEALTHSHOT = "weapon_healthshot";
    public static final String WEAPON_HEGRENADE = "weapon_hegrenade";
    public static final String WEAPON_INCGRENADE = "weapon_incgrenade";
    public static final String WEAPON_HKP2000 = "weapon_hkp2000";
    public static final String WEAPON_KNIFE = "weapon_knife";
    public static final String WEAPON_KNIFE_T = "weapon_knife_t";
    public static final String WEAPON_KNIFE_CT = "weapon_knife_ct";
    public static final String WEAPON_M249 = "weapon_m249";
    public static final String WEAPON_M4A1 = "weapon_m4a1";
    public static final String WEAPON_M4A1_SILENCER = "weapon_m4a1_silencer";
    public static final String WEAPON_MAC10 = "weapon_mac10";
    public static final String WEAPON_MAG7 = "weapon_mag7";
    public static final String WEAPON_MOLOTOV = "weapon_molotov";
    public static final String WEAPON_MP7 = "weapon_mp7";
    public static final String WEAPON_MP9 = "weapon_mp9";
    public static final String WEAPON_NEGEV = "weapon_negev";
    public static final String WEAPON_NOVA = "weapon_nova";
    public static final String WEAPON_P250 = "weapon_p250";
    public static final String WEAPON_P90 = "weapon_p90";
    public static final String WEAPON_SAWEDOFF = "weapon_sawedoff";
    public static final String WEAPON_SCAR20 = "weapon_scar20";
    public static final String WEAPON_SG556 = "weapon_sg556";
    public static final String WEAPON_SSG08 = "weapon_ssg08";
    public static final String WEAPON_SMOKEGRENADE = "weapon_smokegrenade";
    public static final String WEAPON_TAGRENADE = "weapon_tagrenade";
    public static final String WEAPON_TASER = "weapon_taser";
    public static final String WEAPON_TEC9 = "weapon_tec9";
    public static final String WEAPON_UMP45 = "weapon_ump45";
    public static final String WEAPON_USP_SILENCER = "weapon_usp_silencer";
    public static final String WEAPON_XM1014 = "weapon_xm1014";
    public static final String WEAPON_REVOLVER = "weapon_revolver";

    private String name;
    private String codeName;

    private String paintKit;
    private String paintKitCodeName;

    private String type;

    private String state;

    private int ammo;
    private int ammoReserve;
    private int ammoMax;

    private int id;

    public Weapon(int id, String type, String state, int ammo, int ammoReserve, int ammoMax, String paintKitCodeName, String codeName)
    {
        this.id = id;
        this.type = type;
        this.state = state;
        this.ammo = ammo;
        this.ammoReserve = ammoReserve;
        this.ammoMax = ammoMax;
        this.paintKitCodeName = paintKitCodeName;
        this.codeName = codeName;
    }

    public Weapon(String codeName, String paintKitCodeName, String type, String state, int id)
    {
        this(id, type, state, -1, -1, -1, paintKitCodeName, codeName);
    }

    private String nameFromCodeName(Context context)
    {
        switch(this.codeName)
        {
            case WEAPON_AK47:
                return context.getString(R.string.weapon_ak47);
            case WEAPON_AUG:
                return context.getString(R.string.weapon_aug);
            case WEAPON_AWP:
                return context.getString(R.string.weapon_awp);
            case WEAPON_BIZON:
                return context.getString(R.string.weapon_bizon);
            case WEAPON_C4:
                return context.getString(R.string.weapon_c4);
            case WEAPON_CZ75A:
                return context.getString(R.string.weapon_cz75a);
            case WEAPON_DEAGLE:
                return "Desert Eagle";
            case WEAPON_DECOY:
                return "Decoy";
            case WEAPON_ELITE:
                return "Dual Berettas";
            case WEAPON_FAMAS:
                return "Famas";
            case WEAPON_FIVESEVEN:
                return "Five-SeveN";
            case WEAPON_FLASHBANG:
                return "Flashbang";
            case WEAPON_G3SG1:
                return "G3SG1";
            case WEAPON_GALILAR:
                return "Galil AR";
            case WEAPON_GLOCK:
                return "Glock-18";
            case WEAPON_HEALTHSHOT:
                return "Medi-Shot";
            case WEAPON_HEGRENADE:
                return "HE Grenade";
            case WEAPON_INCGRENADE:
                return "Incendiary grenade";
            case WEAPON_HKP2000:
                return "P2000";
            case WEAPON_KNIFE:
            case WEAPON_KNIFE_T:
            case WEAPON_KNIFE_CT:
                return "Knife";
            case WEAPON_M249:
                return "M249";
            case WEAPON_M4A1:
                return "M4A1";
            case WEAPON_M4A1_SILENCER:
                return "M4A1-S";
            case WEAPON_MAC10:
                return "MAC-10";
            case WEAPON_MAG7:
                return "MAG-7";
            case WEAPON_MOLOTOV:
                return "Molotov";
            case WEAPON_MP7:
                return "MP7";
            case WEAPON_MP9:
                return "MP9";
            case WEAPON_NEGEV:
                return "Negev";
            case WEAPON_NOVA:
                return "Nova";
            case WEAPON_P250:
                return "P250";
            case WEAPON_P90:
                return "P90";
            case WEAPON_SAWEDOFF:
                return "Sawed-Off";
            case WEAPON_SCAR20:
                return "SCAR-20";
            case WEAPON_SG556:
                return "SG-556";
            case WEAPON_SSG08:
                return "SSG-08";
            case WEAPON_SMOKEGRENADE:
                return "Smoke Grenade";
            case WEAPON_TAGRENADE:
                return "Tactical Awareness Grenade";
            case WEAPON_TASER:
                return "Zeus x27";
            case WEAPON_TEC9:
                return "Tec-9";
            case WEAPON_UMP45:
                return "UMP-45";
            case WEAPON_USP_SILENCER:
                return "USP-S";
            case WEAPON_XM1014:
                return "XM1014";
            case WEAPON_REVOLVER:
                return "Revolver";
            default:
                return this.codeName;
        }
    }
}
