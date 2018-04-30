/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.buffs.buffclasses.adventurer;

import client.MapleBuffStat;
import client.MapleJob;
import client.MonsterStatus;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Itzik
 */
public class MagicianBuff extends AbstractBuffClass {

    public MagicianBuff() {
        buffs = new int[]{
            //一轉
            2001002, //魔心防禦Magic Guard

            //二轉
            //火毒
            //2101001, //精神強化Meditation
            2101008, //極速詠唱Magic Booster
            //冰雷
            2201009, //寒冰迅移
            //2201001, //精神強化Meditation
            2201010, //極速詠唱Magic Booster
            //僧侶
            //2300009, //祝福福音Blessed Ensemble - passive but buff?
            2301004, //天使祝福Bless
            2301008, //極速詠唱Magic Booster
            2301003, //神聖之光Invicible

            //三轉
            //火毒
            2111011, //元素適應(火、毒)Elemental Adaptation (Fire, Poison)
            2111008, //自然力重置Elemental Decrease
            2111007, //瞬間移動精通Teleport Mastery
            //冰雷
            2211012, //元素適應(雷、冰)Elemental Adaptation (Ice, Lightning)
            2211008, //自然力重置Elemental Decrease
            2211007, //瞬間移動精通Teleport Mastery
            //祭司
            //            2311011, //神聖之泉Holy Fountain
            2311012, //聖靈守護Divine Protection
            2311002, //時空門Mystic Door
            2311003, //神聖祈禱Holy Symbol
            2311007, //瞬間移動精通Teleport Mastery
            2311009, //聖十字魔法盾Holy Magic Shield

            //四轉
            //火毒
            2121004, //魔力無限Infinity
            2121000, //楓葉祝福Maple Warrior
            2120010,//神秘狙擊
            //冰雷
            2221004, //魔力無限Infinity
            2221000, //楓葉祝福Maple Warrior
            2220010,//神秘狙擊
            //主教
            2221001, //核爆術
            2321004, //魔力無限Infinity
            2321005, //進階祝福Advanced Blessing
            2321000, //楓葉祝福Maple Warrior
            2320011,//神秘狙擊

            //超技
            //火毒
            2121053, //傳說冒險Epic Adventure
            2121054, //火靈結界Inferno Aura
            //冰雷
            2221053, //傳說冒險Epic Adventure
            2221054, //冰雪結界Absolute Zero Aura
            //主教
            2321052, //天堂之門
            2321053, //傳說冒險Epic Adventure
            2321054, //復仇天使Avenging Angel
        };
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is冒險家(job) && job / 100 == 2;
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 2001002: //魔心防禦Magic Guard
                eff.statups.put(MapleBuffStat.MAGIC_GUARD, eff.info.get(MapleStatInfo.x));
                break;
            case 2101008: //極速詠唱Magic Booster
            case 2201010: //極速詠唱Magic Booster
            case 2301008: //極速詠唱Magic Booster
                eff.statups.put(MapleBuffStat.BOOSTER, eff.info.get(MapleStatInfo.x));
                break;
            case 2201009: //寒冰迅移
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(MapleBuffStat.CHILLING_STEP, 1);
                break;
            case 2301004: //天使祝福Bless   
                eff.statups.put(MapleBuffStat.BLESS, (int) eff.getLevel());
                break;
            case 2301003: //神聖之光Invicible
                eff.statups.put(MapleBuffStat.INVINCIBLE, eff.info.get(MapleStatInfo.x));
                break;
            case 2111011: //元素適應(火、毒)Elemental Adaptation (Fire, Poison)
            case 2211012: //元素適應(雷、冰)Elemental Adaptation (Ice, Lightning)
            case 2311012: //聖靈守護Divine Protection
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(MapleBuffStat.PRESSURE_VOID, 1);
                break;
            case 2111008: //自然力重置Elemental Decrease
            case 2211008: //自然力重置Elemental Decrease
                eff.statups.put(MapleBuffStat.ELEMENT_RESET, eff.info.get(MapleStatInfo.x));
                break;
            case 2111007: //瞬間移動精通Teleport Mastery
            case 2211007: //瞬間移動精通Teleport Mastery
            case 2311007: //瞬間移動精通Teleport Mastery
                eff.info.put(MapleStatInfo.mpCon, eff.info.get(MapleStatInfo.y));
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(MapleBuffStat.TELEPORT_MASTERY, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case 2311002: //時空門Mystic Door
                eff.statups.put(MapleBuffStat.MYSTIC_DOOR, eff.info.get(MapleStatInfo.x));
                break;
            case 2311003: //神聖祈禱Holy Symbol
                eff.statups.put(MapleBuffStat.HOLY_SYMBOL, eff.info.get(MapleStatInfo.x));
                break;
            case 2311009: //聖十字魔法盾Holy Magic Shield
                eff.statups.put(MapleBuffStat.HOLY_MAGIC_SHELL, eff.info.get(MapleStatInfo.x));
                break;
            case 2121004: //魔力無限Infinity
            case 2221004: //魔力無限Infinity
            case 2321004: //魔力無限Infinity
                eff.statups.put(MapleBuffStat.STANCE, eff.info.get(MapleStatInfo.prop));
                eff.statups.put(MapleBuffStat.INFINITY, eff.info.get(MapleStatInfo.damage) + 1);
                break;
            case 2121000: //楓葉祝福Maple Warrior
            case 2221000: //楓葉祝福Maple Warrior
            case 2321000: //楓葉祝福Maple Warrior
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.info.get(MapleStatInfo.x));
                break;
            case 2221001: //核爆術
                eff.statups.put(MapleBuffStat.核爆術, 1);
                break;
            case 2321005: //進階祝福Advanced Blessing
                eff.statups.put(MapleBuffStat.HOLY_SHIELD, eff.info.get(MapleStatInfo.x));
                eff.statups.put(MapleBuffStat.HP_BOOST, eff.info.get(MapleStatInfo.indieMhp));
                eff.statups.put(MapleBuffStat.MP_BOOST, eff.info.get(MapleStatInfo.indieMmp));
                break;
            case 2320011://神秘狙擊
            case 2220010://神秘狙擊
            case 2120010://神秘狙擊
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(MapleBuffStat.MANY_USES, eff.info.get(MapleStatInfo.x));
                break;
            case 2121053: //傳說冒險Epic Adventure
            case 2221053: //傳說冒險Epic Adventure
            case 2321053: //傳說冒險Epic Adventure
                eff.statups.put(MapleBuffStat.DAMAGE_PERCENT, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(MapleBuffStat.DAMAGE_CAP_INCREASE, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case 2121054: //火靈結界Inferno Aura
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(MapleBuffStat.INFERNO_AURA, 1);
                break;
            case 2221054: //冰雪結界Absolute Zero Aura
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(MapleBuffStat.ABSOLUTE_ZERO_AURA, 1);
                eff.statups.put(MapleBuffStat.所有屬性抵抗, eff.info.get(MapleStatInfo.v));
                eff.statups.put(MapleBuffStat.狀態異常耐性, eff.info.get(MapleStatInfo.v));
                break;
            case 2321052: //天堂之門
                eff.statups.put(MapleBuffStat.天堂之門, 1);
                break;
            case 2321054: //復仇天使Avenging Angel
                eff.statups.clear();
                eff.statups.put(MapleBuffStat.IGNORE_DEF, eff.info.get(MapleStatInfo.ignoreMobpdpR));
                eff.statups.put(MapleBuffStat.AVENGING_ANGEL, (int) eff.getLevel());
                eff.statups.put(MapleBuffStat.INDIE_MAD, eff.info.get(MapleStatInfo.indieMad));
                eff.statups.put(MapleBuffStat.ATTACK_SPEED, eff.info.get(MapleStatInfo.indieBooster) - 2);
                eff.statups.put(MapleBuffStat.DAMAGE_CAP_INCREASE, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            default:
                System.out.println("法師技能未處理,技能代碼: " + skill);
                break;
        }
    }
}
