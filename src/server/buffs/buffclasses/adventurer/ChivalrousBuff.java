/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.buffs.buffclasses.adventurer;

import client.MapleBuffStat;
import client.MapleJob;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.AbstractBuffClass;

/**
 *
 * @author Administrator
 */
public class ChivalrousBuff extends AbstractBuffClass {

    public ChivalrousBuff() {
        buffs = new int[]{
            5081023, // 追影連擊
            5701013, // 真氣流貫
            5711024, // 天地無我
            5721000, // 楓葉祝福
            5721066}; // 千斤墜
    }

    @Override
    public boolean containsJob(int job) {
        return MapleJob.is冒險家(job) && (job == 508 || (job / 10 == 57));
    }

    @Override
    public void handleBuff(MapleStatEffect eff, int skill) {
        switch (skill) {
            case 5081023: // 追影連擊
                eff.statups.put(MapleBuffStat.BOOSTER, eff.info.get(MapleStatInfo.x));
                break;
            case 5701013: // 真氣流貫
                eff.statups.put(MapleBuffStat.DAMAGE_PERCENT, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(MapleBuffStat.CRITICAL_PERCENT_UP, eff.info.get(MapleStatInfo.indieCr));
                eff.statups.put(MapleBuffStat.STR, eff.info.get(MapleStatInfo.str));
                eff.statups.put(MapleBuffStat.DEX, eff.info.get(MapleStatInfo.dex));
                break;
            case 5711024: // 天地無我
                eff.statups.put(MapleBuffStat.HAMSTRING, eff.info.get(MapleStatInfo.x));
                eff.statups.put(MapleBuffStat.ACCURACY_PERCENT, eff.info.get(MapleStatInfo.y));
                eff.statups.put(MapleBuffStat.迴避率提升, eff.info.get(MapleStatInfo.prop));
                break;
            case 5721000: // 楓葉祝福
                eff.statups.put(MapleBuffStat.MAPLE_WARRIOR, eff.info.get(MapleStatInfo.x));
                break;
            case 5721066: // 千斤墜
                eff.statups.put(MapleBuffStat.STANCE, eff.info.get(MapleStatInfo.prop));
                eff.statups.put(MapleBuffStat.CRITICAL_PERCENT_UP, eff.info.get(MapleStatInfo.indieCr));
                eff.statups.put(MapleBuffStat.INDIE_ALL_STATE, eff.info.get(MapleStatInfo.indieAllStat));
                break;
            default:
                System.out.println("未知的 蒼龍俠客(572) Buff技能: " + skill);
                break;
        }
    }
}
