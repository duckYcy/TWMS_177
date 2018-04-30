package client.inventory;

import constants.EventConstants;
import constants.GameConstants;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import server.Randomizer;

public class Equip extends Item implements Serializable {

    public static enum ScrollResult {

        SUCCESS,
        FAIL,
        CURSE
    }
    public static final long ARMOR_RATIO = 350000L;
    public static final long WEAPON_RATIO = 700000L;
    //charm: -1 = has not been initialized yet, 0 = already been worn, >0 = has teh charm exp
    private byte state = 0, bonusState = 0, upgradeSlots = 0, level = 0, vicioushammer = 0, enhance = 0, enhanctBuff = 0, reqLevel = 0, yggdrasilWisdom = 0, bossDamage = 0, ignorePDR = 0, totalDamage = 0, allStat = 0, karmaCount = -1, fire = -1, starforce;
    private short str = 0, dex = 0, _int = 0, luk = 0, hp = 0, mp = 0, watk = 0, matk = 0, wdef = 0, mdef = 0, acc = 0, avoid = 0, hands = 0, speed = 0, jump = 0, charmExp = 0, pvpDamage = 0, soulname, soulenchanter, soulpotential;
    private int durability = -1, incSkill = -1, potential1 = 0, potential2 = 0, potential3 = 0, bonuspotential1 = 0, bonuspotential2 = 0, bonuspotential3 = 0, fusionAnvil = 0, socket1 = 0, socket2 = 0, socket3 = 0, soulskill;
    private long itemEXP = 0;
    private boolean finalStrike = false;
    private boolean trace = false;
    private int failCount = 0;
    private MapleRing ring = null;
    private MapleAndroid android = null;
    private List<EquipStat> stats = new LinkedList();
    private List<EquipSpecialStat> specialStats = new LinkedList();
    private Map<EquipStat, Long> statsTest = new LinkedHashMap<>();

    public Equip(int id, short position, byte flag) {
        super(id, position, (short) 1, flag);
    }

    public Equip(int id, short position, int uniqueid, short flag) {
        super(id, position, (short) 1, flag, uniqueid);
    }

    @Override
    public Item copy() {
        Equip ret = new Equip(getItemId(), getPosition(), getUniqueId(), getFlag());
        ret.str = str;
        ret.dex = dex;
        ret._int = _int;
        ret.luk = luk;
        ret.hp = hp;
        ret.mp = mp;
        ret.matk = matk;
        ret.mdef = mdef;
        ret.watk = watk;
        ret.wdef = wdef;
        ret.acc = acc;
        ret.avoid = avoid;
        ret.hands = hands;
        ret.speed = speed;
        ret.jump = jump;
        ret.enhance = enhance;
        ret.upgradeSlots = upgradeSlots;
        ret.level = level;
        ret.itemEXP = itemEXP;
        ret.durability = durability;
        ret.vicioushammer = vicioushammer;
        ret.state = state;
        ret.potential1 = potential1;
        ret.potential2 = potential2;
        ret.potential3 = potential3;
        ret.bonusState = bonusState;
        ret.bonuspotential1 = bonuspotential1;
        ret.bonuspotential2 = bonuspotential2;
        ret.bonuspotential3 = bonuspotential3;
        ret.fusionAnvil = fusionAnvil;
        ret.socket1 = socket1;
        ret.socket2 = socket2;
        ret.socket3 = socket3;
        ret.charmExp = charmExp;
        ret.pvpDamage = pvpDamage;
        ret.incSkill = incSkill;
        ret.enhanctBuff = enhanctBuff;
        ret.reqLevel = reqLevel;
        ret.yggdrasilWisdom = yggdrasilWisdom;
        ret.finalStrike = finalStrike;
        ret.bossDamage = bossDamage;
        ret.ignorePDR = ignorePDR;
        ret.totalDamage = totalDamage;
        ret.allStat = allStat;
        ret.karmaCount = karmaCount;
        ret.fire = fire;
        ret.setGiftFrom(getGiftFrom());
        ret.setOwner(getOwner());
        ret.setQuantity(getQuantity());
        ret.setExpiration(getExpiration());
        ret.stats = stats;
        ret.specialStats = specialStats;
        ret.statsTest = statsTest;
        ret.soulname = soulname;
        ret.soulenchanter = soulenchanter;
        ret.soulpotential = soulpotential;
        ret.soulskill = soulskill;
        ret.starforce = starforce;
        return ret;
    }

    @Override
    public byte getType() {
        return 1;
    }

    public byte getUpgradeSlots() {
        return upgradeSlots;
    }

    public short getStr() {
        return str;
    }

    public short getDex() {
        return dex;
    }

    public short getInt() {
        return _int;
    }

    public short getLuk() {
        return luk;
    }

    public short getHp() {
        return hp;
    }

    public short getMp() {
        return mp;
    }

    public short getWatk() {
        return watk;
    }

    public short getMatk() {
        return matk;
    }

    public short getWdef() {
        return wdef;
    }

    public short getMdef() {
        return mdef;
    }

    public short getAcc() {
        return acc;
    }

    public short getAvoid() {
        return avoid;
    }

    public short getHands() {
        return hands;
    }

    public short getSpeed() {
        return speed;
    }

    public short getJump() {
        return jump;
    }

    public void setStr(short str) {
        if (str < 0) {
            str = 0;
        }
        this.str = str;
    }

    public void setDex(short dex) {
        if (dex < 0) {
            dex = 0;
        }
        this.dex = dex;
    }

    public void setInt(short _int) {
        if (_int < 0) {
            _int = 0;
        }
        this._int = _int;
    }

    public void setLuk(short luk) {
        if (luk < 0) {
            luk = 0;
        }
        this.luk = luk;
    }

    public void setHp(short hp) {
        if (hp < 0) {
            hp = 0;
        }
        this.hp = hp;
    }

    public void setMp(short mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public void setWatk(short watk) {
        if (watk < 0) {
            watk = 0;
        }
        this.watk = watk;
    }

    public void setMatk(short matk) {
        if (matk < 0) {
            matk = 0;
        }
        this.matk = matk;
    }

    public void setWdef(short wdef) {
        if (wdef < 0) {
            wdef = 0;
        }
        this.wdef = wdef;
    }

    public void setMdef(short mdef) {
        if (mdef < 0) {
            mdef = 0;
        }
        this.mdef = mdef;
    }

    public void setAcc(short acc) {
        if (acc < 0) {
            acc = 0;
        }
        this.acc = acc;
    }

    public void setAvoid(short avoid) {
        if (avoid < 0) {
            avoid = 0;
        }
        this.avoid = avoid;
    }

    public void setHands(short hands) {
        if (hands < 0) {
            hands = 0;
        }
        this.hands = hands;
    }

    public void setSpeed(short speed) {
        if (speed < 0) {
            speed = 0;
        }
        this.speed = speed;
    }

    public void setJump(short jump) {
        if (jump < 0) {
            jump = 0;
        }
        this.jump = jump;
    }

    public void setUpgradeSlots(byte upgradeSlots) {
        this.upgradeSlots = upgradeSlots;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getViciousHammer() {
        return vicioushammer;
    }

    public void setViciousHammer(byte ham) {
        vicioushammer = ham;
    }

    public long getItemEXP() {
        return itemEXP;
    }

    public void setItemEXP(long itemEXP) {
        if (itemEXP < 0) {
            itemEXP = 0;
        }
        this.itemEXP = itemEXP;
    }

    public long getEquipExp() {
        if (itemEXP <= 0) {
            return 0;
        }
        //aproximate value
        if (GameConstants.isWeapon(getItemId())) {
            return itemEXP / WEAPON_RATIO;
        } else {
            return itemEXP / ARMOR_RATIO;
        }
    }

    public long getEquipExpForLevel() {
        if (getEquipExp() <= 0) {
            return 0;
        }
        long expz = getEquipExp();
        for (int i = getBaseLevel(); i <= GameConstants.getMaxLevel(getItemId()); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return expz;
    }

    public long getExpPercentage() {
        if (getEquipLevel() < getBaseLevel() || getEquipLevel() > GameConstants.getMaxLevel(getItemId()) || GameConstants.getExpForLevel(getEquipLevel(), getItemId()) <= 0) {
            return 0;
        }
        return getEquipExpForLevel() * 100 / GameConstants.getExpForLevel(getEquipLevel(), getItemId());
    }

    public int getEquipLevel() {
        if (GameConstants.getMaxLevel(getItemId()) <= 0) {
            return 0;
        } else if (getEquipExp() <= 0) {
            return getBaseLevel();
        }
        int levelz = getBaseLevel();
        long expz = getEquipExp();
        for (int i = levelz; (GameConstants.getStatFromWeapon(getItemId()) == null ? (i <= GameConstants.getMaxLevel(getItemId())) : (i < GameConstants.getMaxLevel(getItemId()))); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                levelz++;
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return levelz;
    }

    public int getBaseLevel() {
        return (GameConstants.getStatFromWeapon(getItemId()) == null ? 1 : 0);
    }

    @Override
    public void setQuantity(short quantity) {
        if (quantity < 0 || quantity > 1) {
            throw new RuntimeException("Setting the quantity to " + quantity + " on an equip (itemid: " + getItemId() + ")");
        }
        super.setQuantity(quantity);
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(final int dur) {
        durability = dur;
    }

    public byte getEnhanctBuff() {
        return enhanctBuff;
    }

    public void setEnhanctBuff(byte enhanctBuff) {
        this.enhanctBuff = enhanctBuff;
    }

    public byte getReqLevel() {
        return reqLevel;
    }

    public void setReqLevel(byte reqLevel) {
        this.reqLevel = reqLevel;
    }

    public byte getYggdrasilWisdom() {
        return yggdrasilWisdom;
    }

    public void setYggdrasilWisdom(byte yggdrasilWisdom) {
        this.yggdrasilWisdom = yggdrasilWisdom;
    }

    public boolean getFinalStrike() {
        return finalStrike;
    }

    public void setFinalStrike(boolean finalStrike) {
        this.finalStrike = finalStrike;
    }

    public byte getBossDamage() {
        return bossDamage;
    }

    public void setBossDamage(byte bossDamage) {
        this.bossDamage = bossDamage;
    }

    public byte getIgnorePDR() {
        return ignorePDR;
    }

    public void setIgnorePDR(byte ignorePDR) {
        this.ignorePDR = ignorePDR;
    }

    public byte getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(byte totalDamage) {
        this.totalDamage = totalDamage;
    }

    public byte getAllStat() {
        return allStat;
    }

    public void setAllStat(byte allStat) {
        this.allStat = allStat;
    }

    public void setFailCount(int value) {
        failCount = value;
    }

    public int getFailCount() {
        return failCount;
    }

    public boolean isTrace() {
        return trace;
    }

    public void setTrace(boolean value) {
        trace = value;
    }

    public byte getKarmaCount() {
        return karmaCount;
    }

    public void setKarmaCount(byte karmaCount) {
        this.karmaCount = karmaCount;
    }

    public byte getEnhance() {
        return enhance;
    }

    public void setEnhance(final byte en) {
        enhance = en;
    }

    public int getPotential1() {
        return potential1;
    }

    public void setPotential1(final int en) {
        potential1 = en;
    }

    public int getPotential2() {
        return potential2;
    }

    public void setPotential2(final int en) {
        potential2 = en;
    }

    public int getPotential3() {
        return potential3;
    }

    public void setPotential3(final int en) {
        potential3 = en;
    }

    public int getBonusPotential1() {
        return bonuspotential1;
    }

    public void setBonusPotential1(final int en) {
        bonuspotential1 = en;
    }

    public int getBonusPotential2() {
        return bonuspotential2;
    }

    public void setBonusPotential2(final int en) {
        bonuspotential2 = en;
    }

    public int getBonusPotential3() {
        return bonuspotential3;
    }

    public void setBonusPotential3(final int en) {
        bonuspotential3 = en;
    }

    public int getFusionAnvil() {
        return fusionAnvil;
    }

    public void setFusionAnvil(final int en) {
        fusionAnvil = en;
    }

    public byte getState() {
        return state;
    }

    public void setState(final byte en) {
        state = en;
    }

    public byte getBonusState() {
        return bonusState;
    }

    public void setBonusState(final byte en) {
        bonusState = en;
    }

    public void updateState() {
        updateState(false);
    }

    public void updateBonusState() {
        updateState(true);
    }

    public void updateState(boolean bonus) {
        int ret = 0;
        int v1;
        int v2;
        int v3;
        if (!bonus) {
            v1 = potential1;
            v2 = potential2;
            v3 = potential3;
        } else {
            v1 = bonuspotential1;
            v2 = bonuspotential2;
            v3 = bonuspotential3;
        }
        if (v1 >= 40000 || v2 >= 40000 || v3 >= 40000) {
            ret = 20;//傳說
        } else if (v1 >= 30000 || v2 >= 30000 || v3 >= 30000) {
            ret = 19;//罕見
        } else if (v1 >= 20000 || v2 >= 20000 || v3 >= 20000) {
            ret = 18;//稀有
        } else if (v1 >= 1 || v2 >= 1 || v3 >= 1) {
            ret = 17;//特殊
        } else if (v1 < 0 || v2 < 0 || v3 < 0) {
            ret = Math.abs(v1);//隱藏
            if (ret > 100000000) {
                ret %= 100000000;
            }
            if (ret > 10000000) {
                ret %= 10000000;
            }
            if (ret > 1000000) {
                ret %= 1000000;
            }
            if (ret > 100000) {
                ret = Math.abs(v3);
            }
            ret -= 16;
        }

        if (!bonus) {
            setState((byte) ret);
        } else {
            setBonusState((byte) ret);
        }
    }

    public short getSoulName() {
        return soulname;
    }

    public void setSoulName(final short soulname) {
        this.soulname = soulname;
    }

    public short getSoulEnchanter() {
        return soulenchanter;
    }

    public void setSoulEnchanter(final short soulenchanter) {
        this.soulenchanter = soulenchanter;
    }

    public short getSoulPotential() {
        return soulpotential;
    }

    public void setSoulPotential(final short soulpotential) {
        this.soulpotential = soulpotential;
    }

    public int getSoulSkill() {
        return soulskill;
    }

    public void setSoulSkill(final int skillid) {
        this.soulskill = skillid;
    }

    public byte getFire() {
        return fire;
    }

    public void setFire(byte fire) {
        this.fire = fire;
    }

    public byte getStarForce() {
        return starforce;
    }

    public void setStarForce(byte starforce) {
        this.starforce = starforce;
    }

    public void resetPotential_Fuse(boolean half, int potentialState) {
        resetPotential_Fuse(half, potentialState, false);
    }

    public void resetBonusPotential_Fuse(boolean half, int potentialState) {
        resetPotential_Fuse(half, potentialState, true);
    }

    public void resetPotential_Fuse(boolean half, int potentialState, boolean bonus) { //maker skill - equip first receive
        //no legendary, 0.16% chance unique, 4% chance epic, else rare
        potentialState = -potentialState;
        if (Randomizer.nextInt(100) < 4) {
            potentialState -= Randomizer.nextInt(100) < 4 ? 2 : 1;
        }
        if (!bonus) {
            setPotential1(potentialState);
            setPotential2((Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0)); //1/10 chance of 3 line
            setPotential3(0); //just set it theoretically
        } else {
            setBonusPotential1(potentialState);
            setBonusPotential2((Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0)); //1/10 chance of 3 line
            setBonusPotential3(0); //just set it theoretically
        }
        updateState(bonus);
    }

    public void resetPotential() {
        resetPotential(0, false);
    }

    public void resetPotential(int state) {
        resetPotential(state, false);
    }

    public void resetPotential(boolean fullLine) {
        resetPotential(0, fullLine);
    }

    public void resetPotential(int state, boolean fullLine) {
        resetPotential(state, fullLine, false);
    }

    public void resetBonusPotential() {
        resetBonusPotential(0);
    }

    public void resetBonusPotential(int state) {
        resetBonusPotential(state, false);
    }

    public void resetBonusPotential(boolean fullLine) {
        resetBonusPotential(0, fullLine);
    }

    public void resetBonusPotential(int state, boolean fullLine) {
        resetPotential(state, fullLine, true);
    }

    public void resetPotential(int state, boolean fullLine, boolean bonus) {
        final int rank;
        switch (state) {
            case 1:
                rank = -17;
                break;
            case 2:
                rank = -18;
                break;
            case 3:
                rank = -19;
                break;
            case 4:
                rank = -20;
                break;
            default:
                rank = Randomizer.nextInt(100) < 4 ? (Randomizer.nextInt(100) < 4 ? -19 : -18) : -17;
        }
        if (!bonus) {
            setPotential1(rank);
            setPotential2(Randomizer.nextInt(10) <= 1 || fullLine ? rank : 0); //1/10 chance of 3 line
            setPotential3(0); //just set it theoretically
        } else {
            setBonusPotential1(rank);
            setBonusPotential2(Randomizer.nextInt(10) <= 1 || fullLine ? rank : 0); //1/10 chance of 3 line
            setBonusPotential3(0); //just set it theoretically
        }
        updateState(bonus);
    }

    public void renewPotential(int rate) {
        renewPotential(rate, -1);
    }

    public void renewPotential(int rate, int type) {
        renewPotential(rate, type, 0);
    }

    public void renewPotential(int rate, int type, int toLock) {
        renewPotential(rate, type, toLock, false);
    }

    public void renewBonusPotential(int rate) {
        renewBonusPotential(rate, -1);
    }

    public void renewBonusPotential(int rate, int type) {
        renewBonusPotential(rate, type, 0);
    }

    public void renewBonusPotential(int rate, int type, int toLock) {
        renewPotential(rate, type, toLock, true);
    }

    public void renewPotential(int rate, int type, int toLock, boolean bonus) { // 0 = normal miracle cube, 1 = premium, 2 = epic pot scroll, 3 = super, 5 = enlightening
        int miracleRate = 1;
        if (EventConstants.DoubleMiracleTime) {
            miracleRate *= 2;
        }

        boolean threeLine = (!bonus && getPotential3() > 0) || (bonus && getBonusPotential3() > 0);

        if (type == -1) {
            type = GameConstants.CubeType.特殊.getValue() | GameConstants.CubeType.稀有.getValue() | GameConstants.CubeType.罕見.getValue() | GameConstants.CubeType.傳說.getValue();
        }
        int rank = Randomizer.nextInt(100) < rate * miracleRate ? 1 : 0;
        if (GameConstants.CubeType.等級下降.check(type)) {
            if (rank == 0) {
                rank = Randomizer.nextInt(100) < (rate + 20) * miracleRate ? -1 : 0;
            }
        }

        if (!bonus) {
            if (getState() + rank < 17 || getState() + rank > (!GameConstants.CubeType.傳說.check(type) ? !GameConstants.CubeType.罕見.check(type) ? !GameConstants.CubeType.稀有.check(type) ? 17 : 18 : 19 : 20)) {
                rank = 0;
            }
        } else {
            if (getBonusState() + rank < 17 || getBonusState() + rank > (!GameConstants.CubeType.傳說.check(type) ? !GameConstants.CubeType.罕見.check(type) ? !GameConstants.CubeType.稀有.check(type) ? 17 : 18 : 19 : 20)) {
                rank = 0;
            }
        }

        if (toLock != 0) {
            if (toLock <= 3) {
                int lockPot;
                if (!bonus) {
                    switch (toLock) {
                        case 1:
                            lockPot = getPotential1();
                            break;
                        case 2:
                            lockPot = getPotential2();
                            break;
                        case 3:
                            lockPot = getPotential3();
                            break;
                        default:
                            lockPot = 0;
                    }
                    setPotential3(-(getState() + rank));
                } else {
                    switch (toLock) {
                        case 1:
                            lockPot = getBonusPotential1();
                            break;
                        case 2:
                            lockPot = getBonusPotential2();
                            break;
                        case 3:
                            lockPot = getBonusPotential3();
                            break;
                        default:
                            lockPot = 0;
                    }
                    setBonusPotential3(-(getBonusState() + rank));
                }
                rank = lockPot + toLock * 100000;
            } else {
                System.err.println("[作弊] 嘗試鎖定的潛能不存在");
            }
        }

        if (GameConstants.CubeType.對等.check(type)) {
            rank += 1000000;
        }

        if (GameConstants.CubeType.去掉無用潛能.check(type)) {
            rank += 10000000;
        }

        if (GameConstants.CubeType.前兩條相同.check(type)) {
            rank += Randomizer.nextInt(10) <= 5 ? 100000000 : 0;
        }

        if (!bonus) {
            if (rank % 1000000 < 100000) {
                rank = -(getState() + rank);
            } else {
                rank = -rank;
            }
            setPotential1(rank);

            if (GameConstants.CubeType.調整潛能條數.check(type)) {
                setPotential2(Randomizer.nextInt(10) <= 2 ? rank : 0);
            } else if (threeLine) {
                setPotential2(rank);
            } else {
                setPotential2(0);
            }

            if (toLock == 0) {
                setPotential3(0);
            }
        } else {
            if (rank % 1000000 < 100000) {
                rank = -(getBonusState() + rank);
            } else {
                rank = -rank;
            }
            setBonusPotential1(rank);

            if (GameConstants.CubeType.調整潛能條數.check(type)) {
                setBonusPotential2(Randomizer.nextInt(10) <= 2 ? rank : 0);
            } else if (threeLine) {
                setBonusPotential2(rank);
            } else {
                setBonusPotential2(0);
            }

            if (toLock == 0) {
                setBonusPotential3(0);
            }
        }

        if (GameConstants.CubeType.洗後無法交易.check(type)) {
            setFlag((short) (getFlag() | ItemFlag.UNTRADABLE.getValue()));
        }

        updateState(bonus);
    }

    public int getIncSkill() {
        return incSkill;
    }

    public void setIncSkill(int inc) {
        incSkill = inc;
    }

    public short getCharmEXP() {
        return charmExp;
    }

    public short getPVPDamage() {
        return pvpDamage;
    }

    public void setCharmEXP(short s) {
        charmExp = s;
    }

    public void setPVPDamage(short p) {
        pvpDamage = p;
    }

    public MapleRing getRing() {
        if (!GameConstants.isEffectRing(getItemId()) || getUniqueId() <= 0) {
            return null;
        }
        if (ring == null) {
            ring = MapleRing.loadFromDb(getUniqueId(), getPosition() < 0);
        }
        return ring;
    }

    public void setRing(MapleRing ring) {
        this.ring = ring;
    }

    public MapleAndroid getAndroid() {
        if (getItemId() / 10000 != 166 || getUniqueId() <= 0) {
            return null;
        }
        if (android == null) {
            android = MapleAndroid.loadFromDb(getItemId(), getUniqueId());
        }
        return android;
    }

    public void setAndroid(MapleAndroid ring) {
        android = ring;
    }

    public short getSocketState() {
        int flag = 0;
        if (socket1 > 0 || socket2 > 0 || socket3 > 0) { // Got empty sockets show msg 
            flag |= SocketFlag.DEFAULT.getValue();
        }
        if (socket1 > 0) {
            flag |= SocketFlag.SOCKET_BOX_1.getValue();
        }
        if (socket1 > 1) {
            flag |= SocketFlag.USED_SOCKET_1.getValue();
        }
        if (socket2 > 0) {
            flag |= SocketFlag.SOCKET_BOX_2.getValue();
        }
        if (socket2 > 1) {
            flag |= SocketFlag.USED_SOCKET_2.getValue();
        }
        if (socket3 > 0) {
            flag |= SocketFlag.SOCKET_BOX_3.getValue();
        }
        if (socket3 > 1) {
            flag |= SocketFlag.USED_SOCKET_3.getValue();
        }
        return (short) flag;
    }

    public int getSocket1() {
        return socket1;
    }

    public void setSocket1(int socket1) {
        this.socket1 = socket1;
    }

    public int getSocket2() {
        return socket2;
    }

    public void setSocket2(int socket2) {
        this.socket2 = socket2;
    }

    public int getSocket3() {
        return socket3;
    }

    public void setSocket3(int socket3) {
        this.socket3 = socket3;
    }

    public List<EquipStat> getStats() {
        return stats;
    }

    public List<EquipSpecialStat> getSpecialStats() {
        return specialStats;
    }

    public Map<EquipStat, Long> getStatsTest() {
        return statsTest;
    }

    public static Equip calculateEquipStatsTest(Equip eq) {
        eq.getStatsTest().clear();
        if (eq.getUpgradeSlots() > 0) {
            eq.getStatsTest().put(EquipStat.SLOTS, Long.valueOf(eq.getUpgradeSlots()));
        }
        if (eq.getLevel() > 0) {
            eq.getStatsTest().put(EquipStat.LEVEL, Long.valueOf(eq.getLevel()));
        }
        if (eq.getStr() > 0) {
            eq.getStatsTest().put(EquipStat.STR, Long.valueOf(eq.getStr()));
        }
        if (eq.getDex() > 0) {
            eq.getStatsTest().put(EquipStat.DEX, Long.valueOf(eq.getDex()));
        }
        if (eq.getInt() > 0) {
            eq.getStatsTest().put(EquipStat.INT, Long.valueOf(eq.getInt()));
        }
        if (eq.getLuk() > 0) {
            eq.getStatsTest().put(EquipStat.LUK, Long.valueOf(eq.getLuk()));
        }
        if (eq.getHp() > 0) {
            eq.getStatsTest().put(EquipStat.MHP, Long.valueOf(eq.getHp()));
        }
        if (eq.getMp() > 0) {
            eq.getStatsTest().put(EquipStat.MMP, Long.valueOf(eq.getMp()));
        }
        if (eq.getWatk() > 0) {
            eq.getStatsTest().put(EquipStat.WATK, Long.valueOf(eq.getWatk()));
        }
        if (eq.getMatk() > 0) {
            eq.getStatsTest().put(EquipStat.MATK, Long.valueOf(eq.getMatk()));
        }
        if (eq.getWdef() > 0) {
            eq.getStatsTest().put(EquipStat.WDEF, Long.valueOf(eq.getWdef()));
        }
        if (eq.getMdef() > 0) {
            eq.getStatsTest().put(EquipStat.MDEF, Long.valueOf(eq.getMdef()));
        }
        if (eq.getAcc() > 0) {
            eq.getStatsTest().put(EquipStat.ACC, Long.valueOf(eq.getAcc()));
        }
        if (eq.getAvoid() > 0) {
            eq.getStatsTest().put(EquipStat.AVOID, Long.valueOf(eq.getAvoid()));
        }
        if (eq.getHands() > 0) {
            eq.getStatsTest().put(EquipStat.HANDS, Long.valueOf(eq.getHands()));
        }
        if (eq.getSpeed() > 0) {
            eq.getStatsTest().put(EquipStat.SPEED, Long.valueOf(eq.getSpeed()));
        }
        if (eq.getJump() > 0) {
            eq.getStatsTest().put(EquipStat.JUMP, Long.valueOf(eq.getJump()));
        }
        if (eq.getFlag() > 0) {
            eq.getStatsTest().put(EquipStat.FLAG, Long.valueOf(eq.getFlag()));
        }
        if (eq.getIncSkill() > 0) {
            eq.getStatsTest().put(EquipStat.INC_SKILL, Long.valueOf(eq.getIncSkill()));
        }
        if (eq.getEquipLevel() > 0) {
            eq.getStatsTest().put(EquipStat.ITEM_LEVEL, Long.valueOf(eq.getEquipLevel()));
        }
        if (eq.getItemEXP() > 0) {
            eq.getStatsTest().put(EquipStat.ITEM_EXP, eq.getItemEXP());
        }
        if (eq.getDurability() > -1) {
            eq.getStatsTest().put(EquipStat.DURABILITY, Long.valueOf(eq.getDurability()));
        }
        if (eq.getViciousHammer() > 0) {
            eq.getStatsTest().put(EquipStat.VICIOUS_HAMMER, Long.valueOf(eq.getViciousHammer()));
        }
        if (eq.getPVPDamage() > 0) {
            eq.getStatsTest().put(EquipStat.PVP_DAMAGE, Long.valueOf(eq.getPVPDamage()));
        }
        if (eq.getEnhanctBuff() > 0) {
            eq.getStatsTest().put(EquipStat.ENHANCT_BUFF, Long.valueOf(eq.getEnhanctBuff()));
        }
        if (eq.getReqLevel() > 0) {
            eq.getStatsTest().put(EquipStat.REQUIRED_LEVEL, Long.valueOf(eq.getReqLevel()));
        }
        if (eq.getYggdrasilWisdom() > 0) {
            eq.getStatsTest().put(EquipStat.YGGDRASIL_WISDOM, Long.valueOf(eq.getYggdrasilWisdom()));
        }
        if (eq.getFinalStrike()) {
            eq.getStatsTest().put(EquipStat.FINAL_STRIKE, Long.valueOf(eq.getFinalStrike() ? 1 : 0));
        }
        if (eq.getBossDamage() > 0) {
            eq.getStatsTest().put(EquipStat.BOSS_DAMAGE, Long.valueOf(eq.getBossDamage()));
        }
        if (eq.getIgnorePDR() > 0) {
            eq.getStatsTest().put(EquipStat.IGNORE_PDR, Long.valueOf(eq.getIgnorePDR()));
        }
        //SPECIAL STATS:
        if (eq.getTotalDamage() > 0) {
            eq.getStatsTest().put(EquipStat.TOTAL_DAMAGE, Long.valueOf(eq.getTotalDamage()));
        }
        if (eq.getAllStat() > 0) {
            eq.getStatsTest().put(EquipStat.ALL_STAT, Long.valueOf(eq.getAllStat()));
        }
        eq.getStatsTest().put(EquipStat.KARMA_COUNT, Long.valueOf(eq.getKarmaCount())); //no count = -1
        //eq.getStatsTest().put(EquipStat.UNK8, Long.valueOf(-1)); // test
        //eq.getStatsTest().put(EquipStat.UNK10, Long.valueOf(0)); // test
        return (Equip) eq.copy();
    }

    public static Equip calculateEquipStats(Equip eq) {
        eq.getStats().clear();
        eq.getSpecialStats().clear();
        if (eq.getUpgradeSlots() > 0) {
            eq.getStats().add(EquipStat.SLOTS);
        }
        if (eq.getLevel() > 0) {
            eq.getStats().add(EquipStat.LEVEL);
        }
        if (eq.getStr() > 0) {
            eq.getStats().add(EquipStat.STR);
        }
        if (eq.getDex() > 0) {
            eq.getStats().add(EquipStat.DEX);
        }
        if (eq.getInt() > 0) {
            eq.getStats().add(EquipStat.INT);
        }
        if (eq.getLuk() > 0) {
            eq.getStats().add(EquipStat.LUK);
        }
        if (eq.getHp() > 0) {
            eq.getStats().add(EquipStat.MHP);
        }
        if (eq.getMp() > 0) {
            eq.getStats().add(EquipStat.MMP);
        }
        if (eq.getWatk() > 0) {
            eq.getStats().add(EquipStat.WATK);
        }
        if (eq.getMatk() > 0) {
            eq.getStats().add(EquipStat.MATK);
        }
        if (eq.getWdef() > 0) {
            eq.getStats().add(EquipStat.WDEF);
        }
        if (eq.getMdef() > 0) {
            eq.getStats().add(EquipStat.MDEF);
        }
        if (eq.getAcc() > 0) {
            eq.getStats().add(EquipStat.ACC);
        }
        if (eq.getAvoid() > 0) {
            eq.getStats().add(EquipStat.AVOID);
        }
        if (eq.getHands() > 0) {
            eq.getStats().add(EquipStat.HANDS);
        }
        if (eq.getSpeed() > 0) {
            eq.getStats().add(EquipStat.SPEED);
        }
        if (eq.getJump() > 0) {
            eq.getStats().add(EquipStat.JUMP);
        }
        if (eq.getFlag() > 0) {
            eq.getStats().add(EquipStat.FLAG);
        }
        if (eq.getIncSkill() > 0) {
            eq.getStats().add(EquipStat.INC_SKILL);
        }
        if (eq.getEquipLevel() > 0) {
            eq.getStats().add(EquipStat.ITEM_LEVEL);
        }
        if (eq.getItemEXP() > 0) {
            eq.getStats().add(EquipStat.ITEM_EXP);
        }
        if (eq.getDurability() > -1) {
            eq.getStats().add(EquipStat.DURABILITY);
        }
        if (eq.getViciousHammer() > 0) {
            eq.getStats().add(EquipStat.VICIOUS_HAMMER);
        }
        if (eq.getPVPDamage() > 0) {
            eq.getStats().add(EquipStat.PVP_DAMAGE);
        }
        if (eq.getEnhanctBuff() > 0) {
            eq.getStats().add(EquipStat.ENHANCT_BUFF);
        }
        if (eq.getReqLevel() > 0) {
            eq.getStats().add(EquipStat.REQUIRED_LEVEL);
        }
        if (eq.getYggdrasilWisdom() > 0) {
            eq.getStats().add(EquipStat.YGGDRASIL_WISDOM);
        }
        if (eq.getFinalStrike()) {
            eq.getStats().add(EquipStat.FINAL_STRIKE);
        }
        if (eq.getBossDamage() > 0) {
            eq.getStats().add(EquipStat.BOSS_DAMAGE);
        }
        if (eq.getIgnorePDR() > 0) {
            eq.getStats().add(EquipStat.IGNORE_PDR);
        }
        //SPECIAL STATS:
        if (eq.getTotalDamage() > 0) {
            eq.getSpecialStats().add(EquipSpecialStat.TOTAL_DAMAGE);
        }
        if (eq.getAllStat() > 0) {
            eq.getSpecialStats().add(EquipSpecialStat.ALL_STAT);
        }
        eq.getSpecialStats().add(EquipSpecialStat.KARMA_COUNT); //no count = -1
        if (eq.getFire() > 0) {
            eq.getSpecialStats().add(EquipSpecialStat.FIRE);
        }
        if (eq.getStarForce() > 0) {
            eq.getSpecialStats().add(EquipSpecialStat.STAR_FORCE);
        }
        return (Equip) eq.copy();
    }

    public Item reset(Equip newEquip) {
        //Equip ret = new Equip(getItemId(), getPosition(), getUniqueId(), getFlag());
        this.str = newEquip.str;
        this.dex = newEquip.dex;
        this._int = newEquip._int;
        this.luk = newEquip.luk;
        this.hp = newEquip.hp;
        this.mp = newEquip.mp;
        this.matk = newEquip.matk;
        this.mdef = newEquip.mdef;
        this.watk = newEquip.watk;
        this.wdef = newEquip.wdef;
        this.acc = newEquip.acc;
        this.avoid = newEquip.avoid;
        this.hands = newEquip.hands;
        this.speed = newEquip.speed;
        this.jump = newEquip.jump;
        this.upgradeSlots = newEquip.upgradeSlots;
        this.level = newEquip.level;
        this.itemEXP = newEquip.itemEXP;
        this.durability = newEquip.durability;
        this.vicioushammer = newEquip.vicioushammer;
        this.enhance = newEquip.enhance;
        this.charmExp = newEquip.charmExp;
        this.pvpDamage = newEquip.pvpDamage;
        this.incSkill = newEquip.incSkill;

        this.enhanctBuff = newEquip.enhanctBuff;
        this.reqLevel = newEquip.reqLevel;
        this.yggdrasilWisdom = newEquip.yggdrasilWisdom;
        this.finalStrike = newEquip.finalStrike;
        this.bossDamage = newEquip.bossDamage;
        this.ignorePDR = newEquip.ignorePDR;
        this.totalDamage = newEquip.totalDamage;
        this.allStat = newEquip.allStat;
        this.karmaCount = newEquip.karmaCount;
        this.soulname = newEquip.soulname;
        this.soulenchanter = newEquip.soulenchanter;
        this.soulpotential = newEquip.soulpotential;
        this.soulskill = newEquip.soulskill;
        this.starforce = newEquip.starforce;
        this.fire = newEquip.fire;
        this.setGiftFrom(getGiftFrom());
        return this;
    }
}
