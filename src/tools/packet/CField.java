package tools.packet;

import client.*;
import client.inventory.*;
import constants.GameConstants;
import constants.QuickMove.QuickMoveNPC;
import constants.ServerConfig;
import constants.ServerConstants;
import handling.SendPacketOpcode;
import handling.channel.handler.PlayerInteractionHandler;
import handling.world.World;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildAlliance;
import java.awt.Point;
import java.util.*;
import server.MaplePackageActions;
import server.MapleTrade;
import server.Randomizer;
import server.events.MapleSnowball;
import server.life.MapleNPC;
import server.maps.*;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import server.shops.MapleShop;
import tools.AttackPair;
import tools.HexTool;
import tools.Pair;
import tools.Triple;
import tools.data.MaplePacketLittleEndianWriter;

public class CField {

    public static int[] SecondaryStatRemote = new int[GameConstants.MAX_BUFFSTAT];

    static {
        //
        SecondaryStatRemote[MapleBuffStat.CHAR_BUFF.getPosition() - 1] |= MapleBuffStat.CHAR_BUFF.getValue();
        SecondaryStatRemote[MapleBuffStat.MOUNT_MORPH.getPosition() - 1] |= MapleBuffStat.MOUNT_MORPH.getValue();
        //
        SecondaryStatRemote[MapleBuffStat.DIVINE_FORCE_AURA.getPosition() - 1] |= MapleBuffStat.DIVINE_FORCE_AURA.getValue();
        SecondaryStatRemote[MapleBuffStat.DIVINE_SPEED_AURA.getPosition() - 1] |= MapleBuffStat.DIVINE_SPEED_AURA.getValue();
        //
        SecondaryStatRemote[MapleBuffStat.ENERGY_CHARGE.getPosition() - 1] |= MapleBuffStat.ENERGY_CHARGE.getValue();
        SecondaryStatRemote[MapleBuffStat.DASH_SPEED.getPosition() - 1] |= MapleBuffStat.DASH_SPEED.getValue();
        SecondaryStatRemote[MapleBuffStat.DASH_JUMP.getPosition() - 1] |= MapleBuffStat.DASH_JUMP.getValue();
        SecondaryStatRemote[MapleBuffStat.MONSTER_RIDING.getPosition() - 1] |= MapleBuffStat.MONSTER_RIDING.getValue();
        SecondaryStatRemote[MapleBuffStat.SPEED_INFUSION.getPosition() - 1] |= MapleBuffStat.SPEED_INFUSION.getValue();
        SecondaryStatRemote[MapleBuffStat.HOMING_BEACON.getPosition() - 1] |= MapleBuffStat.HOMING_BEACON.getValue();
        SecondaryStatRemote[MapleBuffStat.DEFAULTBUFF1.getPosition() - 1] |= MapleBuffStat.DEFAULTBUFF1.getValue();
        SecondaryStatRemote[MapleBuffStat.DEFAULTBUFF2.getPosition() - 1] |= MapleBuffStat.DEFAULTBUFF2.getValue();
    }

    public static byte[] getPacketFromHexString(String hex) {
        return HexTool.getByteArrayFromHexString(hex);
    }

    public static byte[] getServerIP(MapleClient c, int port, int clientId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVER_IP.getValue());
        mplew.write(0);
        mplew.write(0);
        if (c.getTempIP().length() > 0) {
            for (String s : c.getTempIP().split(",")) {
                mplew.write(Integer.parseInt(s));
            }
        } else {
            mplew.write(ServerConstants.getGateway_IP());
        }
        mplew.writeShort(port);
        mplew.writeInt(0);//176+ IDB無調用?
        mplew.writeShort(0);//176+ IDB無調用?
        mplew.writeInt(clientId);
        mplew.write(0);
        mplew.writeInt(0);

//      System.err.println(mplew.toString());
        return mplew.getPacket();
    }

    public static byte[] updatePendantSlot(long time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_PENDANT_SLOT.getValue());
        mplew.writeLong(time);

        return mplew.getPacket();
    }

    public static byte[] exitGame() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.EXIT_GAME.getValue());

        return mplew.getPacket();
    }

    public static byte[] getChannelChange(MapleClient c, int port) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHANGE_CHANNEL.getValue());
        mplew.write(1);
        mplew.write(ServerConstants.getGateway_IP());
        mplew.writeShort(port);

        return mplew.getPacket();
    }

    public static byte[] getPVPType(int type, List<Pair<Integer, String>> players1, int team, boolean enabled, int lvl) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_TYPE.getValue());
        mplew.write(type);
        mplew.write(lvl);
        mplew.write(enabled ? 1 : 0);
        mplew.write(0);
        if (type > 0) {
            mplew.write(team);
            mplew.writeInt(players1.size());
            for (Pair pl : players1) {
                mplew.writeInt(((Integer) pl.left));
                mplew.writeMapleAsciiString((String) pl.right);
                mplew.writeShort(2660);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPTransform(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_TRANSFORM.getValue());
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] getPVPDetails(List<Pair<Integer, Integer>> players) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_DETAILS.getValue());
        mplew.write(1);
        mplew.write(0);
        mplew.writeInt(players.size());
        for (Pair pl : players) {
            mplew.writeInt(((Integer) pl.left));
            mplew.write(((Integer) pl.right));
        }

        return mplew.getPacket();
    }

    public static byte[] enablePVP(boolean enabled) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_ENABLED.getValue());
        mplew.write(enabled ? 1 : 2);

        return mplew.getPacket();
    }

    public static byte[] getPVPScore(int score, boolean kill) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_SCORE.getValue());
        mplew.writeInt(score);
        mplew.write(kill ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] getPVPResult(List<Pair<Integer, MapleCharacter>> flags, int exp, int winningTeam, int playerTeam) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_RESULT.getValue());
        mplew.writeInt(flags.size());
        for (Pair f : flags) {
            mplew.writeInt(((MapleCharacter) f.right).getId());
            mplew.writeMapleAsciiString(((MapleCharacter) f.right).getName());
            mplew.writeInt(((Integer) f.left));
            mplew.write(((MapleCharacter) f.right).getTeam() + 1);
            mplew.write(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(exp);
        mplew.write(0);
        mplew.writeShort(100);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(winningTeam);
        mplew.write(playerTeam);

        return mplew.getPacket();
    }

    public static byte[] getPVPTeam(List<Pair<Integer, String>> players) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_TEAM.getValue());
        mplew.writeInt(players.size());
        for (Pair pl : players) {
            mplew.writeInt(((Integer) pl.left));
            mplew.writeMapleAsciiString((String) pl.right);
            mplew.write(0x0A);
            mplew.write(0x64);
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPScoreboard(List<Pair<Integer, MapleCharacter>> flags, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_SCOREBOARD.getValue());
        mplew.writeShort(flags.size());
        for (Pair f : flags) {
            mplew.writeInt(((MapleCharacter) f.right).getId());
            mplew.writeMapleAsciiString(((MapleCharacter) f.right).getName());
            mplew.writeInt(((Integer) f.left));
            mplew.write(type == 0 ? 0 : ((MapleCharacter) f.right).getTeam() + 1);
            mplew.writeInt(0);
        }
        mplew.writeShort(flags.size());
        for (Pair f : flags) {
            mplew.writeInt(((MapleCharacter) f.right).getId());
            mplew.writeMapleAsciiString(((MapleCharacter) f.right).getName());
            mplew.writeInt(((Integer) f.left));
            mplew.write(type == 0 ? 0 : ((MapleCharacter) f.right).getTeam() + 1);
            mplew.writeInt(0);
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPPoints(int p1, int p2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_POINTS.getValue());
        mplew.writeInt(p1);
        mplew.writeInt(p2);

        return mplew.getPacket();
    }

    public static byte[] getPVPKilled(String lastWords) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_KILLED.getValue());
        mplew.writeMapleAsciiString(lastWords);

        return mplew.getPacket();
    }

    public static byte[] getPVPMode(int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_MODE.getValue());
        mplew.write(mode);

        return mplew.getPacket();
    }

    public static byte[] getPVPIceHPBar(int hp, int maxHp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_ICEKNIGHT.getValue());
        mplew.writeInt(hp);
        mplew.writeInt(maxHp);

        return mplew.getPacket();
    }

    public static byte[] getCaptureFlags(MapleMap map) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CAPTURE_FLAGS.getValue());
        mplew.writeRect(map.getArea(0));
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(0)).left).x);
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(0)).left).y);
        mplew.writeRect(map.getArea(1));
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(1)).left).x);
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(1)).left).y);

        return mplew.getPacket();
    }

    public static byte[] getCapturePosition(MapleMap map) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        Point p1 = map.getPointOfItem(2910000);
        Point p2 = map.getPointOfItem(2910001);
        mplew.writeShort(SendPacketOpcode.CAPTURE_POSITION.getValue());
        mplew.write(p1 == null ? 0 : 1);
        if (p1 != null) {
            mplew.writeInt(p1.x);
            mplew.writeInt(p1.y);
        }
        mplew.write(p2 == null ? 0 : 1);
        if (p2 != null) {
            mplew.writeInt(p2.x);
            mplew.writeInt(p2.y);
        }

        return mplew.getPacket();
    }

    public static byte[] resetCapture() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CAPTURE_RESET.getValue());

        return mplew.getPacket();
    }

    public static byte[] getMacros(SkillMacro[] macros) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SKILL_MACRO.getValue());
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (macros[i] != null) {
                count++;
            }
        }
        mplew.write(count);
        for (int i = 0; i < 5; i++) {
            SkillMacro macro = macros[i];
            if (macro != null) {
                mplew.writeMapleAsciiString(macro.getName());
                mplew.write(macro.getShout());
                mplew.writeInt(macro.getSkill1());
                mplew.writeInt(macro.getSkill2());
                mplew.writeInt(macro.getSkill3());
            }
        }

        return mplew.getPacket();
    }

    public static byte[] gameMsg(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GAME_MSG.getValue());
        mplew.writeAsciiString(msg);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] innerPotentialMsg(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.INNER_ABILITY_MSG.getValue());
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] updateInnerPotential(byte ability, int skill, int level, int rank) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ENABLE_INNER_ABILITY.getValue());
        mplew.write(1); //unlock
        mplew.write(1); //0 = no update
        mplew.writeShort(ability); //1-3
        mplew.writeInt(skill); //skill id (7000000+)
        mplew.writeShort(level); //level, 0 = blank inner ability
        mplew.writeShort(rank); //rank
        mplew.write(1); //0 = no update

        return mplew.getPacket();
    }

    public static byte[] innerPotentialResetMessage() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.INNER_ABILITY_RESET_MSG.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("26 00 49 6E 6E 65 72 20 50 6F 74 65 6E 74 69 61 6C 20 68 61 73 20 62 65 65 6E 20 72 65 63 6F 6E 66 69 67 75 72 65 64 2E 01"));

        return mplew.getPacket();
    }

    public static byte[] updateHonour(int honourLevel, int honourExp, boolean levelup) {
        /*
         * data:
         * 03 00 00 00
         * 69 00 00 00
         * 01
         */
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_HONOUR.getValue());

        mplew.writeInt(honourLevel);
        mplew.writeInt(honourExp);
        mplew.write(levelup ? 1 : 0); //shows level up effect

        return mplew.getPacket();
    }

    public static byte[] getCharInfo(MapleCharacter chr) {
        return setField(chr, true, null, 0);
    }

    public static byte[] getWarpToMap(MapleMap to, int spawnPoint, MapleCharacter chr) {
        return setField(chr, false, to, spawnPoint);
    }

    public static byte[] setField(MapleCharacter chr, boolean CharInfo, MapleMap to, int spawnPoint) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WARP_TO_MAP.getValue());
        mplew.writeInt(chr.getClient().getChannel() - 1);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.write(CharInfo);
        mplew.writeShort(0); // size :: v102
        /*         
         * mplew.writeMapleAsciiString()
         * for (int i = 1; i >= v102; i++) {
         *      mplew.writeMapleAsciiString()
         * }
         */

        if (CharInfo) {
            chr.CRand().connectData(mplew); // [Int][Int][Int] 
            PacketHelper.addCharacterInfo(mplew, chr);
            PacketHelper.UnkFunction(mplew);
        } else {
            mplew.writeBoolean(false);
            mplew.writeInt(to.getId());
            mplew.write(spawnPoint);
            mplew.writeInt(chr.getStat().getHp());
            boolean v12 = false;
            mplew.writeBoolean(v12);
            if (v12) {
                mplew.writeInt(0);
                mplew.writeInt(0);
            }
        }
        mplew.write(0);
        mplew.write(0);
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeInt(100);
        mplew.writeShort(0);//176+
        mplew.write(0);//176+
        mplew.write(1);//176+
        mplew.writeShort(0);//176+
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
//        mplew.write(MapleJob.is管理員(chr.getJob()) ? 0 : (GameConstants.isSeparatedSp(chr.getJob()) ? 0 : 1));

        return mplew.getPacket();
    }

    public static byte[] removeBGLayer(boolean remove, int map, byte layer, int duration) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_BG_LAYER.getValue());
        mplew.write(remove ? 1 : 0); //Boolean show or remove
        mplew.writeInt(map);
        mplew.write(layer); //Layer to show/remove
        mplew.writeInt(duration);

        return mplew.getPacket();
    }

    public static byte[] setMapObjectVisible(List<Pair<String, Byte>> objects) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SET_MAP_OBJECT_VISIBLE.getValue());
        mplew.write(objects.size());
        for (Pair<String, Byte> object : objects) {
            mplew.writeMapleAsciiString(object.getLeft());
            mplew.write(object.getRight());
        }

        return mplew.getPacket();
    }

    public static byte[] spawnFlags(List<Pair<String, Integer>> flags) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHANGE_BACKGROUND.getValue());
        mplew.write(flags == null ? 0 : flags.size());
        if (flags != null) {
            for (Pair f : flags) {
                mplew.writeMapleAsciiString((String) f.left);
                mplew.write(((Integer) f.right));
            }
        }

        return mplew.getPacket();
    }

    public static byte[] serverBlocked(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVER_BLOCKED.getValue());
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] pvpBlocked(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PARTY_BLOCKED.getValue());
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] showEquipEffect() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_EQUIP_EFFECT.getValue());

        return mplew.getPacket();
    }

    public static byte[] showEquipEffect(int team) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_EQUIP_EFFECT.getValue());
        mplew.writeShort(team);

        return mplew.getPacket();
    }

    public static byte[] multiChat(String name, String chattext, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MULTICHAT.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(chattext);

        return mplew.getPacket();
    }

    public static byte[] getFindReplyWithCS(String target, boolean buddy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(2);
        mplew.writeInt(-1);

        return mplew.getPacket();
    }

    public static byte[] getWhisper(String sender, int channel, String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(18);
        mplew.writeMapleAsciiString(sender);
        mplew.writeShort(channel - 1);
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static byte[] getWhisperReply(String target, byte reply) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(10);
        mplew.writeMapleAsciiString(target);
        mplew.write(reply);

        return mplew.getPacket();
    }

    public static byte[] getFindReplyWithMap(String target, int mapid, boolean buddy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(3);//was1
        mplew.writeInt(0);//mapid);
//        mplew.writeZeroBytes(8);

        return mplew.getPacket();
    }

    public static byte[] getFindReply(String target, int channel, boolean buddy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(3);
        mplew.writeInt(channel - 1);

        return mplew.getPacket();
    }

    public static byte[] showForeignDamageSkin(MapleCharacter chr, int skinid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_DAMAGE_SKIN.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(skinid);

        return mplew.getPacket();
    }

    public static byte[] MapEff(String path) {
        return environmentChange(path, 4);//was 3
    }

    public static byte[] MapNameDisplay(int mapid) {
        return environmentChange("maplemap/enter/" + mapid, 4);
    }

    public static byte[] Aran_Start() {
        return environmentChange("Aran/balloon", 4);
    }

    public static byte[] musicChange(String song) {
        return environmentChange(song, 7);//was 6
    }

    public static byte[] showEffect(String effect) {
        return environmentChange(effect, 0x0D);//was 3
    }

    public static byte[] playSound(String sound) {
        return environmentChange(sound, 5);//was 4
    }

    public static byte[] environmentChange(String env, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(env);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] trembleEffect(int type, int delay) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(1);
        mplew.write(type);
        mplew.writeInt(delay);
        mplew.writeShort(30);
        // mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] environmentMove(String env, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_ENV.getValue());
        mplew.writeMapleAsciiString(env);
        mplew.writeInt(mode);

        return mplew.getPacket();
    }

    public static byte[] getUpdateEnvironment(MapleMap map) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_ENV.getValue());
        mplew.writeInt(map.getEnvironment().size());
        for (Map.Entry mp : map.getEnvironment().entrySet()) {
            mplew.writeMapleAsciiString((String) mp.getKey());
            mplew.writeInt(((Integer) mp.getValue()));
        }

        return mplew.getPacket();
    }

    public static byte[] startMapEffect(String msg, int itemid, boolean active) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MAP_EFFECT.getValue());
        mplew.write(active ? 0 : 1);
        mplew.writeInt(itemid);
        if (active) {
            mplew.writeMapleAsciiString(msg);
        }
        return mplew.getPacket();
    }

    public static byte[] removeMapEffect() {
        return startMapEffect(null, 0, false);
    }

    public static byte[] getGMEffect(int value, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GM_EFFECT.getValue());
        mplew.write(value);
        mplew.writeZeroBytes(17);

        return mplew.getPacket();
    }

    public static byte[] showOXQuiz(int questionSet, int questionId, boolean askQuestion) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OX_QUIZ.getValue());
        mplew.write(askQuestion ? 1 : 0);
        mplew.write(questionSet);
        mplew.writeShort(questionId);

        return mplew.getPacket();
    }

    public static byte[] showEventInstructions() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GMEVENT_INSTRUCTIONS.getValue());
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] getPVPClock(int type, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(3);
        mplew.write(type);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] getClock(int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(2);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] getClockTime(int hour, int min, int sec) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(1);
        mplew.write(hour);
        mplew.write(min);
        mplew.write(sec);

        return mplew.getPacket();
    }

    public static byte[] boatPacket(int effect, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOAT_MOVE.getValue());
        mplew.write(effect);
        mplew.write(mode);

        return mplew.getPacket();
    }

    public static byte[] setBoatState(int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOAT_STATE.getValue());
        mplew.write(effect);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] stopClock() {
        return getPacketFromHexString(Integer.toHexString(SendPacketOpcode.STOP_CLOCK.getValue()) + " 00");
    }

    public static byte[] showAriantScoreBoard() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ARIANT_SCOREBOARD.getValue());

        return mplew.getPacket();
    }

    public static byte[] sendPyramidUpdate(int amount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PYRAMID_UPDATE.getValue());
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] sendPyramidResult(byte rank, int amount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PYRAMID_RESULT.getValue());
        mplew.write(rank);
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] quickSlot(String skil) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.QUICK_SLOT.getValue());
        mplew.write(skil == null ? 0 : 1);
        if (skil != null) {
            String[] slots = skil.split(",");
            for (int i = 0; i < 28; i++) {
                mplew.writeInt(Integer.parseInt(slots[i]));
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getMovingPlatforms(MapleMap map) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_PLATFORM.getValue());
        mplew.writeInt(map.getPlatforms().size());
        for (MapleNodes.MaplePlatform mp : map.getPlatforms()) {
            mplew.writeMapleAsciiString(mp.name);
            mplew.writeInt(mp.start);
            mplew.writeInt(mp.SN.size());
            for (Integer SN : mp.SN) {
                mplew.writeInt(SN);
            }
            mplew.writeInt(mp.speed);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.x2);
            mplew.writeInt(mp.y1);
            mplew.writeInt(mp.y2);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.y1);
            mplew.write(mp.r);
            mplew.write(0);
        }

        return mplew.getPacket();
    }

    public static byte[] sendPyramidKills(int amount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PYRAMID_KILL_COUNT.getValue());
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] sendPVPMaps() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_INFO.getValue());
        mplew.write(3); //max amount of players
        for (int i = 0; i < 3; i++) {
            mplew.writeInt(10); //how many peoples in each map
            mplew.writeZeroBytes(120);
        }
        mplew.writeShort(150); // 經驗值加倍活動(1.5倍)
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] gainForce(MapleCharacter chr, int oid, int count, int color) {
        List<Integer> mobid = new ArrayList<>();
        mobid.add(oid);

        List<Pair<Integer, Integer>> forceinfo = new ArrayList<>();
        forceinfo.add(new Pair<>(count, color));
        forceinfo.add(new Pair<>(count, color));
        forceinfo.add(new Pair<>(count, color));
        forceinfo.add(new Pair<>(count, color));

        return gainForce(true, chr, mobid, 0, 0, forceinfo);
    }

    public static byte[] gainForce(boolean isRemote, MapleCharacter chr, List<Integer> oid, int type, int skillid, List<Pair<Integer, Integer>> forceInfo) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());

        mplew.write(isRemote);
        if (isRemote) {
            mplew.writeInt(chr.getId());
        }
        mplew.writeInt(isRemote ? oid.get(0) : chr.getId());
        mplew.writeInt(type); //unk

        if (!(type == 0 || type == 9 || type == 14)) {
            mplew.write(1);
            if (GameConstants.isSpecialForce(type)) {
                mplew.writeInt(oid.size()); // size
                for (int i = 0; i < oid.size(); i++) {
                    mplew.writeInt(oid.get(i));
                }
            } else {
                mplew.writeInt(oid.get(0));
            }
            mplew.writeInt(skillid); //skillid
        }

        for (Pair<Integer, Integer> info : forceInfo) {
            mplew.write(1); // while on/off
            mplew.writeInt(info.left); // count
            mplew.writeInt(info.right); // color
            mplew.writeInt(Randomizer.rand(15, 29));
            mplew.writeInt(Randomizer.rand(5, 6));
            mplew.writeInt(Randomizer.rand(35, 50));
            mplew.writeInt(0); // 0
            mplew.writeInt(0); // 0
            mplew.writeInt(0); // 0
            mplew.writeInt(0); // 0
            mplew.writeInt(0); // 0
        }
        if (type == 11) {
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        if (type == 9 || type == 15) {
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        if (type == 16) {
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        if (type == 17) {
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        if (type == 18) {
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        mplew.write(0); // where read??

        return mplew.getPacket();
    }

    public static byte[] getAndroidTalkStyle(int npc, String talk, int... args) {
        if (ServerConfig.logPackets) {
            System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.write(0); // Boolean
        mplew.write(10);
        mplew.write(0);
        mplew.write(0);
        mplew.writeMapleAsciiString(talk);
        mplew.write(args.length);
        for (int i = 0; i < args.length; i++) {
            mplew.writeInt(args[i]);
        }
        return mplew.getPacket();
    }

    public static byte[] achievementRatio(int amount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ACHIEVEMENT_RATIO.getValue());
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] getQuickMoveInfo(boolean show, List<QuickMoveNPC> qm) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.QUICK_MOVE.getValue());
        mplew.write(qm.size() <= 0 ? 0 : show ? qm.size() : 0);
        if (show && qm.size() > 0) {
            for (QuickMoveNPC qmn : qm) {
                mplew.writeInt(0);
                mplew.writeInt(qmn.getId());
                mplew.writeInt(qmn.getType());
                mplew.writeInt(qmn.getLevel());
                mplew.writeMapleAsciiString(qmn.getDescription());
                mplew.writeLong(PacketHelper.getTime(-2));
                mplew.writeLong(PacketHelper.getTime(-1));
            }
        }

        return mplew.getPacket();
    }

    public static byte[] spawnPlayerMapobject(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_PLAYER.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(chr.getLevel());
        mplew.writeMapleAsciiString(chr.getName());
        MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(111111));
        if ((ultExplorer != null) && (ultExplorer.getCustomData() != null)) {
            mplew.writeMapleAsciiString(ultExplorer.getCustomData());
        } else {
            mplew.writeMapleAsciiString("");
        }
        if (chr.getGuildId() <= 0) {
            mplew.writeZeroBytes(8);
        } else {
            MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
                mplew.writeShort(gs.getLogoBG());
                mplew.write(gs.getLogoBGColor());
                mplew.writeShort(gs.getLogo());
                mplew.write(gs.getLogoColor());
            } else {
                mplew.writeZeroBytes(8);
            }
        }
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);

        final List<Pair<Integer, Integer>> buffvalue = new ArrayList<>();
        int[] mask = new int[GameConstants.MAX_BUFFSTAT];
        mask = SecondaryStatRemote.clone();

        if ((chr.getBuffedValue(MapleBuffStat.DARKSIGHT) != null) || (chr.isHidden())) {
            mask[MapleBuffStat.DARKSIGHT.getPosition(true)] |= MapleBuffStat.DARKSIGHT.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.SOULARROW) != null) {
            mask[MapleBuffStat.SOULARROW.getPosition(true)] |= MapleBuffStat.SOULARROW.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.DAMAGE_ABSORBED) != null) {
            mask[MapleBuffStat.DAMAGE_ABSORBED.getPosition(true)] |= MapleBuffStat.DAMAGE_ABSORBED.getValue();
            buffvalue.add(new Pair(1000, 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.DAMAGE_ABSORBED), 4));
            buffvalue.add(new Pair(9, 0));
        }
        if (chr.getBuffedValue(MapleBuffStat.TEMPEST_BLADES) != null) {
            mask[MapleBuffStat.TEMPEST_BLADES.getPosition(true)] |= MapleBuffStat.TEMPEST_BLADES.getValue();
            buffvalue.add(new Pair(chr.getTotalSkillLevel(chr.getTrueBuffSource(MapleBuffStat.TEMPEST_BLADES)), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.TEMPEST_BLADES), 4));
            buffvalue.add(new Pair(5, 0));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.TEMPEST_BLADES) == 61101002 ? 1 : 2, 4));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.TEMPEST_BLADES) == 61101002 ? 3 : 5, 4));
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.TEMPEST_BLADES), 4));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.TEMPEST_BLADES) == 61101002 ? 3 : 5, 4));
            if (chr.getTrueBuffSource(MapleBuffStat.TEMPEST_BLADES) != 61101002) {
                buffvalue.add(new Pair(8, 0));
            }
        }
        if ((chr.getBuffedValue(MapleBuffStat.COMBO) != null) && (chr.getBuffedValue(MapleBuffStat.TEMPEST_BLADES) == null)) {
            mask[MapleBuffStat.COMBO.getPosition(true)] |= MapleBuffStat.COMBO.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.COMBO), 1));
        }
        if (chr.getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
            mask[MapleBuffStat.WK_CHARGE.getPosition(true)] |= MapleBuffStat.WK_CHARGE.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.WK_CHARGE), 2));
            buffvalue.add(new Pair(chr.getBuffSource(MapleBuffStat.WK_CHARGE), 4));
        }
        if ((chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null) && (chr.getBuffedValue(MapleBuffStat.TEMPEST_BLADES) == null)) {
            mask[MapleBuffStat.SHADOWPARTNER.getPosition(true)] |= MapleBuffStat.SHADOWPARTNER.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER), 2));
            buffvalue.add(new Pair(chr.getBuffSource(MapleBuffStat.SHADOWPARTNER), 4));
        }

        if ((chr.getBuffedValue(MapleBuffStat.MORPH) != null) && (chr.getBuffedValue(MapleBuffStat.TEMPEST_BLADES) == null)) {//TODO
            mask[MapleBuffStat.MORPH.getPosition(true)] |= MapleBuffStat.MORPH.getValue();
            buffvalue.add(new Pair(chr.getStatForBuff(MapleBuffStat.MORPH).getMorph(chr), 2));
            buffvalue.add(new Pair(chr.getBuffSource(MapleBuffStat.MORPH), 4));
        }
        if (chr.getBuffedValue(MapleBuffStat.BERSERK_FURY) != null) {//works
            mask[MapleBuffStat.BERSERK_FURY.getPosition(true)] |= MapleBuffStat.BERSERK_FURY.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.DIVINE_BODY) != null) {
            mask[MapleBuffStat.DIVINE_BODY.getPosition(true)] |= MapleBuffStat.DIVINE_BODY.getValue();
        }

        if (chr.getBuffedValue(MapleBuffStat.WIND_WALK) != null) {//TODO better
            mask[MapleBuffStat.WIND_WALK.getPosition(true)] |= MapleBuffStat.WIND_WALK.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.WIND_WALK), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.WIND_WALK), 4));
        }
        if (chr.getBuffedValue(MapleBuffStat.PYRAMID_PQ) != null) {//TODO
            mask[MapleBuffStat.PYRAMID_PQ.getPosition(true)] |= MapleBuffStat.PYRAMID_PQ.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.PYRAMID_PQ), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.PYRAMID_PQ), 4));
        }
        if (chr.getBuffedValue(MapleBuffStat.SOARING) != null) {//TODO
            mask[MapleBuffStat.SOARING.getPosition(true)] |= MapleBuffStat.SOARING.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.SOARING), 1));
        }
//        if (chr.getBuffedValue(MapleBuffStat.OWL_SPIRIT) != null) {//TODO
//            mask[MapleBuffStat.OWL_SPIRIT.getPosition(true)] |= MapleBuffStat.OWL_SPIRIT.getValue();
//            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.OWL_SPIRIT).intValue()), Integer.valueOf(2)));
//            buffvalue.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.OWL_SPIRIT)), Integer.valueOf(3)));
//        }
        if (chr.getBuffedValue(MapleBuffStat.FINAL_CUT) != null) {
            mask[MapleBuffStat.FINAL_CUT.getPosition(true)] |= MapleBuffStat.FINAL_CUT.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.FINAL_CUT), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.FINAL_CUT), 4));
        }

        if (chr.getBuffedValue(MapleBuffStat.TORNADO) != null) {
            mask[MapleBuffStat.TORNADO.getPosition(true)] |= MapleBuffStat.TORNADO.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.TORNADO), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.TORNADO), 4));
        }
        if (chr.getBuffedValue(MapleBuffStat.INFILTRATE) != null) {
            mask[MapleBuffStat.INFILTRATE.getPosition(true)] |= MapleBuffStat.INFILTRATE.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.MECH_CHANGE) != null) {
            mask[MapleBuffStat.MECH_CHANGE.getPosition(true)] |= MapleBuffStat.MECH_CHANGE.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.MECH_CHANGE), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.MECH_CHANGE), 4));
        }
        if (chr.getBuffedValue(MapleBuffStat.DARK_AURA) != null) {
            mask[MapleBuffStat.DARK_AURA.getPosition(true)] |= MapleBuffStat.DARK_AURA.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.DARK_AURA), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.DARK_AURA), 4));
        }
        if (chr.getBuffedValue(MapleBuffStat.BLUE_AURA) != null) {
            mask[MapleBuffStat.BLUE_AURA.getPosition(true)] |= MapleBuffStat.BLUE_AURA.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.BLUE_AURA), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.BLUE_AURA), 4));
        }
        if (chr.getBuffedValue(MapleBuffStat.YELLOW_AURA) != null) {
            mask[MapleBuffStat.YELLOW_AURA.getPosition(true)] |= MapleBuffStat.YELLOW_AURA.getValue();
            buffvalue.add(new Pair(chr.getBuffedValue(MapleBuffStat.YELLOW_AURA), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.YELLOW_AURA), 4));
        }
        if ((chr.getBuffedValue(MapleBuffStat.WATER_SHIELD) != null) && (chr.getBuffedValue(MapleBuffStat.TEMPEST_BLADES) == null)) {
            mask[MapleBuffStat.WATER_SHIELD.getPosition(true)] |= MapleBuffStat.WATER_SHIELD.getValue();
            buffvalue.add(new Pair(chr.getTotalSkillLevel(chr.getTrueBuffSource(MapleBuffStat.WATER_SHIELD)), 2));
            buffvalue.add(new Pair(chr.getTrueBuffSource(MapleBuffStat.WATER_SHIELD), 4));
            buffvalue.add(new Pair(9, 0));
        }

        for (int i = 0; i < mask.length; i++) {
            mplew.writeInt(mask[i]);
        }
        for (Pair i : buffvalue) {
            switch ((int) i.right) {
                case 0:
                    mplew.writeZeroBytes(((Integer) i.left));
                    break;
                case 1:
                    mplew.write(((Integer) i.left).byteValue());
                    break;
                case 2:
                    mplew.writeShort(((Integer) i.left).shortValue());
                    break;
                case 4:
                    mplew.writeInt(((Integer) i.left));
                    break;
            }
        }

        mplew.writeInt(-1);
        mplew.write(0);

        int CHAR_MAGIC_SPAWN = Randomizer.nextInt();

        // DIVINE_SPEED_AURA
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeZeroBytes(5);

        // ENERGY_CHARGE
        mplew.writeZeroBytes(5);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);//1

        mplew.writeZeroBytes(8);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);//2

        mplew.writeZeroBytes(10);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);//3

        mplew.writeShort(0);
        int buffSrc = chr.getBuffSource(MapleBuffStat.MONSTER_RIDING);
        if (buffSrc > 0) {
            Item c_mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -118);
            Item mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18);
            if ((GameConstants.getMountItem(buffSrc, chr) == 0) && (c_mount != null) && (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -119) != null)) {
                mplew.writeInt(c_mount.getItemId());
            } else if ((GameConstants.getMountItem(buffSrc, chr) == 0) && (mount != null) && (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -19) != null)) {
                mplew.writeInt(mount.getItemId());
            } else {
                mplew.writeInt(GameConstants.getMountItem(buffSrc, chr));
            }
            mplew.writeInt(buffSrc);
        } else {
            mplew.writeLong(0L);
        }
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);//4
        mplew.writeLong(0L);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);//5
        mplew.write(1);//177Change
        mplew.writeInt(Randomizer.nextInt());
        mplew.writeZeroBytes(10);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);//6
        mplew.writeZeroBytes(16);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);//7
        mplew.writeZeroBytes(10);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);//8
        mplew.writeShort(chr.getJob());
        mplew.writeShort(chr.getSubcategory());
        mplew.writeInt(0);//176+?
        PacketHelper.addCharLook(mplew, chr, true, false);
        if (MapleJob.is神之子(chr.getJob())) {
            PacketHelper.addCharLook(mplew, chr, true, false);
        }

        mplew.writeInt(0);
        mplew.writeInt(0);

        if ((chr.getBuffedValue(MapleBuffStat.飛行坐騎) != null) && (buffSrc > 0)) {//妮娜的魔法阵 1C 7B 1D 00 //5C 58 8A 00
            addMountId(mplew, chr, buffSrc);
            mplew.writeInt(chr.getId());
        } else {
            mplew.writeLong(0L);
        }
        mplew.writeInt(0);

        mplew.writeInt(Math.min(250, chr.getInventory(MapleInventoryType.CASH).countById(5110000))); //Valentine Effect
        mplew.writeInt(chr.getItemEffect());
        mplew.writeInt(0);
        mplew.writeInt(chr.getTitleEffect());
        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(chr.getTitleEffect()/*124000*/));
        mplew.writeInt(stat != null && stat.getCustomData() != null ? Integer.parseInt(stat.getCustomData()) : 0); //title
        mplew.writeInt(chr.getItemEffect());//AD 72 4C 00
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeShort(-1);
        mplew.writeInt(0);
        mplew.writeInt(-1);
        mplew.write(0);
        mplew.writeInt(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);
        mplew.writeInt(0);
        mplew.writeInt(0); //new v143
        mplew.writePos(chr.getTruePosition());
        mplew.write(chr.getStance());
        mplew.writeShort(chr.getFH());

        for (MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                PetPacket.addPetInfo(mplew, chr, pet, true);
            }
        }
        mplew.write(0);
        if (chr.getHaku() != null && MapleJob.is陰陽師(chr.getJob())) {
            MapleHaku haku = chr.getHaku();
            mplew.write(0);
            mplew.writeInt(haku.getObjectId());
            mplew.writeInt(40020109);
            mplew.write(1);
            mplew.writePos(haku.getPosition());
            mplew.write(0);
            mplew.writeShort(haku.getStance());
        }
        mplew.write(0);
        mplew.writeInt(chr.getMount() != null ? chr.getMount().getLevel() : 1); // 骑宠等级 默认是1级
        mplew.writeInt(chr.getMount() != null ? chr.getMount().getExp() : 0);
        mplew.writeInt(chr.getMount() != null ? chr.getMount().getFatigue() : 0);
        //mplew.writeInt(chr.getMount().getLevel());
        //mplew.writeInt(chr.getMount().getExp());
        //mplew.writeInt(chr.getMount().getFatigue());

        PacketHelper.addAnnounceBox(mplew, chr);
        mplew.write((chr.getChalkboard() != null) && (chr.getChalkboard().length() > 0) ? 1 : 0);
        if ((chr.getChalkboard() != null) && (chr.getChalkboard().length() > 0)) {
            mplew.writeMapleAsciiString(chr.getChalkboard());
        }

        Triple rings = chr.getRings(false);
        addRingInfo(mplew, (List) rings.getLeft());
        addRingInfo(mplew, (List) rings.getMid());
        addMRingInfo(mplew, (List) rings.getRight(), chr);
        mplew.write(0);
        mplew.write(chr.getStat().Berserk ? 1 : 0); //mask
        mplew.writeInt(chr.getMount().getItemId());//骑宠id

        if (MapleJob.is凱撒(chr.getJob())) {
            String x = chr.getOneInfo(12860, "extern");
            mplew.writeInt(x == null ? 0 : Integer.parseInt(x));
            x = chr.getOneInfo(12860, "inner");
            mplew.writeInt(x == null ? 0 : Integer.parseInt(x));
            x = chr.getOneInfo(12860, "premium");
            mplew.write(x == null ? 0 : Integer.parseInt(x));
        }

        mplew.writeZeroBytes(5); //new v142->v143

        //PacketHelper.addFarmInfo(mplew, chr.getClient(), 0);
        for (int i = 0; i < 5; i++) {
            mplew.write(-1);
        }
        mplew.writeInt(0);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.writeLong(0); //v145
        mplew.writeZeroBytes(9);//176+

        return mplew.getPacket();
    }

    public static byte[] removePlayerFromMap(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_PLAYER_FROM_MAP.getValue());
        mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] getChatText(int cidfrom, String text, boolean whiteBG, int show) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHATTEXT.getValue());
        mplew.writeInt(cidfrom);
        mplew.write(whiteBG ? 1 : 0);
        mplew.writeMapleAsciiString(text);
        mplew.write(show);
        mplew.write(0);
        mplew.write(-1);

        return mplew.getPacket();
    }

    public static byte[] getScrollEffect(int chr, int scroll, int toScroll) {
        return getScrollEffect(chr, Equip.ScrollResult.SUCCESS, false, false, scroll, toScroll);
    }

    public static byte[] getScrollEffect(int chr, Equip.ScrollResult scrollSuccess, boolean legendarySpirit, boolean whiteScroll, int scroll, int toScroll) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_SCROLL_EFFECT.getValue());
        mplew.writeInt(chr);
        mplew.write(scrollSuccess == Equip.ScrollResult.SUCCESS ? 1 : scrollSuccess == Equip.ScrollResult.CURSE ? 2 : 0);
        mplew.write(legendarySpirit ? 1 : 0);
        mplew.writeInt(scroll);
        mplew.writeInt(toScroll);
        mplew.write(whiteScroll ? 1 : 0);
        mplew.write(0);//?

        return mplew.getPacket();
    }

    public static byte[] showEnchanterEffect(int cid, byte result) {
        tools.data.MaplePacketLittleEndianWriter mplew = new tools.data.MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ENCHANTER_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(result);

        return mplew.getPacket();
    }

    public static byte[] showSoulScrollEffect(int cid, byte result, boolean destroyed) {
        tools.data.MaplePacketLittleEndianWriter mplew = new tools.data.MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_SOULSCROLL_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(result);
        mplew.write(destroyed ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] showMagnifyingEffect(int chr, short pos, boolean bonusPot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_MAGNIFYING_EFFECT.getValue());
        mplew.writeInt(chr);
        mplew.writeShort(pos);
        mplew.write(bonusPot ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] showPotentialReset(int chrId, boolean success, int itemid, boolean bonus) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(bonus ? SendPacketOpcode.SHOW_BONUS_POTENTIAL_RESET.getValue() : SendPacketOpcode.SHOW_POTENTIAL_RESET.getValue());
        mplew.writeInt(chrId);
        mplew.write(success ? 1 : 0);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showPotentialEx(int chrId, boolean success, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_POTENTIAL_EXPANSION.getValue());
        mplew.writeInt(chrId);
        mplew.write(success);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showBonusPotentialEx(int chrId, boolean success, int itemid, boolean broken) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FIREWORKS_EFFECT.getValue());
        mplew.writeInt(chrId);
        mplew.write(success);
        mplew.writeInt(itemid);
        mplew.write(broken);

        return mplew.getPacket();
    }

    public static byte[] showMapleCubeCost(int value, long cost) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FLASH_CUBE_RESPONSE.getValue());
        mplew.writeInt(-2064580167);
        mplew.write(3);
        mplew.writeInt(value);//0 - 更新價格; 1 - 詢問是否洗道具; 2 - 重新加載道具潛能 ; 大於3 - 取下道具
        mplew.writeLong(cost);

        return mplew.getPacket();
    }

    public static byte[] showFlashCubeEquip(int value, Item item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FLASH_CUBE_RESPONSE.getValue());
        mplew.writeInt(245186978);
        mplew.write(3);
        mplew.writeInt(value);
        PacketHelper.addItemInfo(mplew, item);

        return mplew.getPacket();
    }

    public static byte[] getFlashCubeRespons(int type, int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FLASH_CUBE_RESPONSE.getValue());
        mplew.writeInt(245186978);
        mplew.write(type);
        mplew.writeInt(value);

        return mplew.getPacket();
    }

    public static byte[] getShimmerCubeRespons(int type, int value) {
        return getShimmerCubeRespons(type, value, 0, new ArrayList());
    }

    public static byte[] getShimmerCubeRespons(int type, int value, int line, ArrayList<Integer> selects) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHIMMER_CUBE_RESPONSE.getValue());
        mplew.writeInt(-441292612);
        mplew.write(type);
        mplew.writeInt(value);
        if (line > 0) {
            mplew.writeInt(line);
            mplew.writeInt(selects.size());
            for (int i : selects) {
                mplew.writeInt(i);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] showNebuliteEffect(int chr, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_NEBULITE_EFFECT.getValue());
        mplew.writeInt(chr);
        mplew.write(success ? 1 : 0);
        mplew.writeMapleAsciiString(success ? "Successfully mounted Nebulite." : "Failed to mount Nebulite.");

        return mplew.getPacket();
    }

    public static byte[] useNebuliteFusion(int cid, int itemId, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FUSION_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(success ? 1 : 0);
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] sendKaiserQuickKey(int skill1, int skill2, int skill3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.KAISER_QUICK_KEY.getValue());
        if (skill1 != 0) {
            mplew.write(true);
            mplew.write(0);
            mplew.writeInt(skill1);
        }
        if (skill2 != 0) {
            mplew.write(true);
            mplew.write(1);
            mplew.writeInt(skill2);
        }
        if (skill3 != 0) {
            mplew.write(true);
            mplew.write(2);
            mplew.writeInt(skill3);
        }

        return mplew.getPacket();
    }

    public static byte[] pvpAttack(int cid, int playerLevel, int skill, int skillLevel, int speed, int mastery, int projectile, int attackCount, int chargeTime, int stance, int direction, int range, int linkSkill, int linkSkillLevel, boolean movementSkill, boolean pushTarget, boolean pullTarget, List<AttackPair> attack) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.write(playerLevel);
        mplew.writeInt(skill);
        mplew.write(skillLevel);
        mplew.writeInt(linkSkill != skill ? linkSkill : 0);
        mplew.write(linkSkillLevel != skillLevel ? linkSkillLevel : 0);
        mplew.write(direction);
        mplew.write(movementSkill ? 1 : 0);
        mplew.write(pushTarget ? 1 : 0);
        mplew.write(pullTarget ? 1 : 0);
        mplew.write(0);
        mplew.writeShort(stance);
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(projectile);
        mplew.writeInt(chargeTime);
        mplew.writeInt(range);
        mplew.write(attack.size());
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(attackCount);
        mplew.write(0);
        mplew.write(0);
        for (AttackPair p : attack) {
            mplew.writeInt(p.objectid);
            mplew.writeInt(0);
            mplew.writePos(p.point);
            mplew.write(0);
            mplew.writeInt(0);
            for (Pair atk : p.attack) {
                mplew.writeInt(((Integer) atk.left));
                mplew.writeInt(0);
                mplew.write(((Boolean) atk.right) ? 1 : 0);
                mplew.writeShort(0);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPMist(int cid, int mistSkill, int mistLevel, int damage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_MIST.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(mistSkill);
        mplew.write(mistLevel);
        mplew.writeInt(damage);
        mplew.write(8);
        mplew.writeInt(1000);

        return mplew.getPacket();
    }

    public static byte[] pvpCool(int cid, List<Integer> attack) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_COOL.getValue());
        mplew.writeInt(cid);
        mplew.write(attack.size());
        for (Iterator i$ = attack.iterator(); i$.hasNext();) {
            int b = ((Integer) i$.next());
            mplew.writeInt(b);
        }

        return mplew.getPacket();
    }

    public static byte[] teslaTriangle(int cid, int sum1, int sum2, int sum3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TESLA_TRIANGLE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(sum1);
        mplew.writeInt(sum2);
        mplew.writeInt(sum3);

        return mplew.getPacket();
    }

    public static byte[] followEffect(int initiator, int replier, Point toMap) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FOLLOW_EFFECT.getValue());
        mplew.writeInt(initiator);
        mplew.writeInt(replier);
        mplew.writeLong(0);
        if (replier == 0) {
            mplew.write(toMap == null ? 0 : 1);
            if (toMap != null) {
                mplew.writeInt(toMap.x);
                mplew.writeInt(toMap.y);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] showPQReward(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_PQ_REWARD.getValue());
        mplew.writeInt(cid);
        for (int i = 0; i < 6; i++) {
            mplew.write(0);
        }

        return mplew.getPacket();
    }

    public static byte[] craftMake(int cid, int something, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CRAFT_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(something);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] craftFinished(int cid, int craftID, int ranking, int itemId, int quantity, int exp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CRAFT_COMPLETE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(craftID);
        mplew.writeInt(ranking);
        mplew.writeInt(itemId);
        mplew.writeInt(quantity);
        mplew.writeInt(exp);

        return mplew.getPacket();
    }

    public static byte[] harvestResult(int cid, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HARVESTED.getValue());
        mplew.writeInt(cid);
        mplew.write(success ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] playerDamaged(int cid, int dmg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_DAMAGED.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(dmg);

        return mplew.getPacket();
    }

    public static byte[] showPyramidEffect(int chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.NETT_PYRAMID.getValue());
        mplew.writeInt(chr);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] pamsSongEffect(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PAMS_SONG.getValue());
        mplew.writeInt(cid);
        return mplew.getPacket();
    }

    public static byte[] spawnHaku_change0(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HAKU_CHANGE_0.getValue());
        mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] spawnHaku_change1(MapleHaku d) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HAKU_CHANGE_1.getValue());
        mplew.writeInt(d.getOwner());
        mplew.writePos(d.getPosition());
        mplew.write(d.getStance());
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] spawnHaku_bianshen(int cid, int oid, boolean change) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HAKU_CHANGE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(oid);
        mplew.write(change ? 2 : 1);

        return mplew.getPacket();
    }

    public static byte[] hakuUnk(int cid, int oid, boolean change) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HAKU_CHANGE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.write(0);
        mplew.writeMapleAsciiString("lol");

        return mplew.getPacket();
    }

    public static byte[] spawnHaku(MapleHaku d) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_HAKU.getValue());
        mplew.writeInt(d.getOwner());
        mplew.writeInt(d.getObjectId());
        mplew.writeInt(40020109);
        mplew.write(1);
        mplew.writePos(d.getPosition());
        mplew.write(0);
        mplew.writeShort(d.getStance());

        return mplew.getPacket();
    }

    public static byte[] moveHaku(int cid, int oid, Point pos, List<LifeMovementFragment> res) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.HAKU_MOVE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(oid);
        mplew.writeInt(0);
        mplew.writePos(pos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, res);
        return mplew.getPacket();
    }

    public static byte[] spawnDragon(MapleDragon d) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DRAGON_SPAWN.getValue());
        mplew.writeInt(d.getOwner());
        mplew.writeInt(d.getPosition().x);
        mplew.writeInt(d.getPosition().y);
        mplew.write(d.getStance());
        mplew.writeShort(0);
        mplew.writeShort(d.getJobId());

        return mplew.getPacket();
    }

    public static byte[] removeDragon(int chrid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DRAGON_REMOVE.getValue());
        mplew.writeInt(chrid);

        return mplew.getPacket();
    }

    public static byte[] moveDragon(MapleDragon d, Point startPos, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DRAGON_MOVE.getValue());
        mplew.writeInt(d.getOwner());
        mplew.writeInt(0);
        mplew.writePos(startPos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] spawnAndroid(MapleCharacter cid, MapleAndroid android) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANDROID_SPAWN.getValue());
        mplew.writeInt(cid.getId());
        mplew.write(android.getType());
        mplew.writePos(android.getPos());
        mplew.write(android.getStance());
        mplew.writeShort(0);
        mplew.writeShort(android.getSkin() >= 2000 ? android.getSkin() - 2000 : android.getSkin());
        mplew.writeShort(android.getHair() - 30000);
        mplew.writeShort(android.getFace() - 20000);
        mplew.writeMapleAsciiString(android.getName());
        for (short i = -1200; i > -1207; i = (short) (i - 1)) {
            Item item = cid.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
            mplew.writeInt(item != null ? item.getItemId() : 0);
            mplew.writeInt(0);
        }

        return mplew.getPacket();
    }

    public static byte[] moveAndroid(int cid, Point pos, List<LifeMovementFragment> res) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ANDROID_MOVE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(0);
        mplew.writePos(pos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, res);

        return mplew.getPacket();
    }

    public static byte[] showAndroidEmotion(int cid, byte emo1, byte emo2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // Packet: 97 DB 00 00 04 E7 FD C4 FF 05 00 00 03 00 3A 1C 52 04 07 00 41 6E 64 72 6F 69 64 85 4D 0F 00 00 00 00 00 00 00 00 00 BF 09 10
        // and more 63 zero bytes
        mplew.writeShort(SendPacketOpcode.ANDROID_EMOTION.getValue());
        mplew.writeInt(cid);
        mplew.write(emo1);
        mplew.write(emo2);

        return mplew.getPacket();
    }

    public static byte[] updateAndroidLook(boolean itemOnly, MapleCharacter cid, MapleAndroid android) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANDROID_UPDATE.getValue());
        mplew.writeInt(cid.getId());
        mplew.write(itemOnly ? 1 : 0);
        if (itemOnly) {
            for (short i = -1200; i > -1207; i = (short) (i - 1)) {
                Item item = cid.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
                mplew.writeInt(item != null ? item.getItemId() : 0);
                mplew.writeInt(0);
            }
        } else {
            mplew.writeShort(android.getSkin() >= 2000 ? android.getSkin() - 2000 : android.getSkin());
            mplew.writeShort(android.getHair() - 30000);
            mplew.writeShort(android.getFace() - 20000);
            mplew.writeMapleAsciiString(android.getName());
        }

        return mplew.getPacket();
    }

    public static byte[] deactivateAndroid(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANDROID_DEACTIVATED.getValue());
        mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] removeAndroidHeart() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(0x14);

        return mplew.getPacket();
    }

    public static byte[] removeFamiliar(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.writeShort(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] spawnFamiliar(MonsterFamiliar mf, boolean spawn, boolean respawn) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(respawn ? SendPacketOpcode.SPAWN_FAMILIAR_2.getValue() : SendPacketOpcode.SPAWN_FAMILIAR.getValue());
        mplew.writeInt(mf.getCharacterId());
        mplew.write(spawn ? 1 : 0);
        mplew.write(respawn ? 1 : 0);
        mplew.write(0);
        if (spawn) {
            mplew.writeInt(mf.getFamiliar());
            mplew.writeInt(mf.getFatigue());
            mplew.writeInt(mf.getVitality() * 300); //max fatigue
            mplew.writeMapleAsciiString(mf.getName());
            mplew.writePos(mf.getTruePosition());
            mplew.write(mf.getStance());
            mplew.writeShort(mf.getFh());
        }

        return mplew.getPacket();
    }

    public static byte[] moveFamiliar(int cid, Point startPos, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.write(0);
        mplew.writePos(startPos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] touchFamiliar(int cid, byte unk, int objectid, int type, int delay, int damage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TOUCH_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.write(0);
        mplew.write(unk);
        mplew.writeInt(objectid);
        mplew.writeInt(type);
        mplew.writeInt(delay);
        mplew.writeInt(damage);

        return mplew.getPacket();
    }

    public static byte[] familiarAttack(int cid, byte unk, List<Triple<Integer, Integer, List<Integer>>> attackPair) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ATTACK_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.write(0);// familiar id?
        mplew.write(unk);
        mplew.write(attackPair.size());
        for (Triple<Integer, Integer, List<Integer>> s : attackPair) {
            mplew.writeInt(s.left);
            mplew.write(s.mid);
            mplew.write(s.right.size());
            for (int damage : s.right) {
                mplew.writeInt(damage);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] renameFamiliar(MonsterFamiliar mf) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.RENAME_FAMILIAR.getValue());
        mplew.writeInt(mf.getCharacterId());
        mplew.write(0);
        mplew.writeInt(mf.getFamiliar());
        mplew.writeMapleAsciiString(mf.getName());

        return mplew.getPacket();
    }

    public static byte[] updateFamiliar(MonsterFamiliar mf) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_FAMILIAR.getValue());
        mplew.writeInt(mf.getCharacterId());
        mplew.writeInt(mf.getFamiliar());
        mplew.writeInt(mf.getFatigue());
        mplew.writeLong(PacketHelper.getTime(mf.getVitality() >= 3 ? System.currentTimeMillis() : -2L));

        return mplew.getPacket();
    }

    public static byte[] movePlayer(int cid, List<LifeMovementFragment> moves, Point startPos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_PLAYER.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(0);
        mplew.writePos(startPos);
        mplew.writeShort(0);
        mplew.writeShort(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] closeRangeAttack(int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackPair> damage, boolean energy, int lvl, byte mastery, byte unk, int charge) {
        return addAttackInfo(energy ? 4 : 0, cid, tbyte, skill, level, display, speed, damage, lvl, mastery, unk, 0, null, 0, false);
    }

    public static byte[] rangedAttack(int cid, byte tbyte, int skill, int level, int display, byte speed, int itemid, List<AttackPair> damage, Point pos, int lvl, byte mastery, byte unk) {
        return addAttackInfo(1, cid, tbyte, skill, level, display, speed, damage, lvl, mastery, unk, itemid, pos, 0, true);
    }

    public static byte[] strafeAttack(int cid, byte tbyte, int skill, int level, int display, byte speed, int itemid, List<AttackPair> damage, Point pos, int lvl, byte mastery, byte unk, int ultLevel) {
        return addAttackInfo(2, cid, tbyte, skill, level, display, speed, damage, lvl, mastery, unk, itemid, pos, ultLevel, true);
    }

    public static byte[] magicAttack(int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackPair> damage, int charge, int lvl, byte unk) {
        return addAttackInfo(3, cid, tbyte, skill, level, display, speed, damage, lvl, (byte) 0, unk, charge, null, 0, false);
    }

    public static byte[] addAttackInfo(int type, int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackPair> damage, int lvl, byte mastery, byte unk, int charge, Point pos, int ultLevel, boolean RangedAttack) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (type == 0) {
            mplew.writeShort(SendPacketOpcode.CLOSE_RANGE_ATTACK.getValue());
        } else if (type == 1 || type == 2) {
            mplew.writeShort(SendPacketOpcode.RANGED_ATTACK.getValue());
        } else if (type == 3) {
            mplew.writeShort(SendPacketOpcode.MAGIC_ATTACK.getValue());
        } else {
            mplew.writeShort(SendPacketOpcode.ENERGY_ATTACK.getValue());
        }

        addAttackBody(mplew, type, cid, tbyte, skill, level, display, speed, damage, lvl, mastery, unk, charge, pos, ultLevel, RangedAttack);
        /* mplew.writeInt(cid);
         mplew.write(RangedAttack ? 1 : 0);
         mplew.write(tbyte);
         //        System.out.println(tbyte + " - tbyte");
         mplew.write(lvl);
         if ((skill > 0) || (type == 3)) {
         mplew.write(level);
         if (level > 0) {
         mplew.writeInt(skill);
         }
         } else if (type != 2 && type != 3) {
         mplew.write(0);
         }

         if (GameConstants.isZero(skill / 10000) && skill != 100001283) {
         short zero1 = 0;
         short zero2 = 0;
         mplew.write(zero1 > 0 || zero2 > 0); //boolean
         if (zero1 > 0 || zero2 > 0) {
         mplew.writeShort(zero1);
         mplew.writeShort(zero2);
         //there is a full handler so better not write zero
         }
         }

         if (type == 2) {
         mplew.write(ultLevel);
         if (ultLevel > 0) {
         mplew.writeInt(3220010);
         }
         }
         if (skill == 40021185 || skill == 42001006) {
         mplew.write(0); //boolean if true then int
         }
         if (type == 0 || type == 1) {
         mplew.write(0);
         }
         mplew.write(unk);//always 0?
         if ((unk & 2) != 0) {
         mplew.writeInt(0);
         mplew.writeInt(0);
         }
         mplew.writeShort(display);
         mplew.write(speed);
         mplew.write(mastery);
         mplew.writeInt(charge);
         for (AttackPair oned : damage) {
         if (oned.attack != null) {
         mplew.writeInt(oned.objectid);
         mplew.write(7);
         mplew.write(0);
         mplew.write(0);
         if (skill == 42111002) {
         mplew.write(oned.attack.size());
         for (Pair eachd : oned.attack) {
         mplew.writeInt(((Integer) eachd.left));
         }
         } else {
         for (Pair eachd : oned.attack) {
         mplew.write(((Boolean) eachd.right) ? 1 : 0);
         mplew.writeInt(((Integer) eachd.left));
         }
         }
         }
         }
         if (skill == 2321001 || skill == 2221052 || skill == 11121052) {
         mplew.writeInt(0);
         } else if (skill == 65121052 || skill == 101000202 || skill == 101000102) {
         mplew.writeInt(0);
         mplew.writeInt(0);
         }
         if (skill == 42100007) {
         mplew.writeShort(0);
         mplew.write(0);
         }
         if (type == 1 || type == 2) {
         mplew.writePos(pos);
         } else if (type == 3 && charge > 0) {
         mplew.writeInt(charge);
         }
         if (skill == 5321000
         || skill == 5311001
         || skill == 5321001
         || skill == 5011002
         || skill == 5311002
         || skill == 5221013
         || skill == 5221017
         || skill == 3120019
         || skill == 3121015
         || skill == 4121017) {
         mplew.writePos(pos);
         }
         mplew.writeZeroBytes(30);//test
         */
        return mplew.getPacket();
    }

    //int type, int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackPair> damage, int lvl, byte mastery, byte unk, int charge, Point pos, int ultLevel, boolean RangedAttack
    public static void addAttackBody(MaplePacketLittleEndianWriter mplew, int type, int cid, int tbyte, int skill, int level, int display, byte speed, List<AttackPair> damage, int lvl, byte mastery, byte unk, int charge, Point pos, int ultLevel, boolean RangedAttack) {
        mplew.writeInt(cid);
        mplew.write(RangedAttack ? 1 : 0);
        mplew.write(tbyte);
        mplew.write(lvl);
        if (skill > 0) {
            mplew.write(level);
            mplew.writeInt(skill);
        } else {
            mplew.write(0);
        }

        if (MapleJob.is神之子(skill / 10000) && skill != 100001283) {
            short zero1 = 0;
            short zero2 = 0;
            mplew.write(zero1 > 0 || zero2 > 0); //boolean
            if (zero1 > 0 || zero2 > 0) {
                mplew.writeShort(zero1);
                mplew.writeShort(zero2);
                //there is a full handler so better not write zero
            }
        }

        if (RangedAttack) {
            if (skill != 13121052) {
                mplew.write(0);
            }
            mplew.writeInt(8);
        } else {
            mplew.writeInt(GameConstants.Attacktype(skill));
        }

        mplew.write(0);
        mplew.write(unk);//always 0?
        if ((unk & 2) != 0) {
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        mplew.writeShort(display);
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(charge);
        for (AttackPair oned : damage) {
            if (oned.attack != null) {
                mplew.writeInt(oned.objectid);
                mplew.write(7);
                mplew.writeShort(0);
                if (skill == 42111002) {
                    mplew.write(oned.attack.size());
                    for (Pair eachd : oned.attack) {
                        mplew.writeInt(((Integer) eachd.left));
                    }
                } else {
                    for (Pair eachd : oned.attack) {
                        if (((Boolean) eachd.right)) {
                            mplew.writeInt(((Integer) eachd.left) + -2147483648);
                        } else {
                            mplew.writeInt(((Integer) eachd.left));
                        }
                    }
                }
            }
        }
    }

    public static byte[] skillEffect(MapleCharacter from, int skillId, byte level, short display, byte unk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SKILL_EFFECT.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        mplew.write(level);
        mplew.writeShort(display);
        mplew.write(unk);
        if (skillId == 13111020 || skillId == 112111016) {
            mplew.writePos(from.getPosition()); // Position
        }

        return mplew.getPacket();
    }

    public static byte[] skillCancel(MapleCharacter from, int skillId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_SKILL_EFFECT.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);

        return mplew.getPacket();
    }

    public static byte[] damagePlayer(int cid, int type, int damage, int monsteridfrom, byte direction, int skillid, int pDMG, boolean pPhysical, int pID, byte pType, Point pPos, byte offset, int offset_d, int fake) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_PLAYER.getValue());
        mplew.writeInt(cid);
        mplew.write(type);
        mplew.writeInt(damage);
        mplew.write(0);
        if (type >= -1) {
            mplew.writeInt(monsteridfrom);
            mplew.write(direction);
            mplew.writeInt(skillid);
        } else {
            if (type == -8) {
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
            }
        }
        mplew.writeInt(0);
        mplew.writeInt(pDMG);
        mplew.write(0);
        if (pDMG > 0) {
            mplew.write(pPhysical ? 1 : 0);
            mplew.writeInt(pID);
            mplew.write(pType);
            mplew.writePos(pPos);
        }
        mplew.write(offset);
        if ((offset & 1) != 0) {
            mplew.writeInt(offset_d);
        }
        mplew.writeInt(damage);
        if (damage == -1) {
            mplew.writeInt(fake);
        }

        return mplew.getPacket();
    }

    public static byte[] facialExpression(MapleCharacter from, int expression) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FACIAL_EXPRESSION.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(expression);
        mplew.writeInt(-1);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] itemEffect(int characterid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        MapleCharacter chr;
        mplew.writeShort(SendPacketOpcode.SHOW_EFFECT.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);
        mplew.writeInt(-1); // not sure, added in v146.1
        mplew.write(0);
        System.out.println("Item Effect:\r\nCharacter ID: " + characterid + "\r\nItem ID: " + itemid);
        return mplew.getPacket();
    }

    public static byte[] showTitle(int characterid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_TITLE.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showAngelicBuster(int characterid, int tempid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANGELIC_CHANGE.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(tempid);

        return mplew.getPacket();
    }

    public static byte[] showChair(int characterid, int itemid, String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_CHAIR.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);
        mplew.writeInt(text.length() > 0 ? 1 : 0);
        if (text.length() != 0 && (itemid / 1000 == 3014)) {
            mplew.writeMapleAsciiString(text);
        }
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] updateCharLook(MapleCharacter chr, boolean second) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_LOOK.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        PacketHelper.addCharLook(mplew, chr, false, second);
        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        addRingInfo(mplew, rings.getLeft());
        addRingInfo(mplew, rings.getMid());
        addMRingInfo(mplew, rings.getRight(), chr);
        mplew.writeInt(0); // -> charid to follow (4)
        mplew.writeInt(0x0F);

        return mplew.getPacket();
    }

    public static byte[] updatePartyMemberHP(int cid, int curhp, int maxhp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_PARTYMEMBER_HP.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(curhp);
        mplew.writeInt(maxhp);

        return mplew.getPacket();
    }

    public static byte[] loadGuildName(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOAD_GUILD_NAME.getValue());
        mplew.writeInt(chr.getId());
        if (chr.getGuildId() <= 0) {
            mplew.writeShort(0);
        } else {
            MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
            } else {
                mplew.writeShort(0);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] loadGuildIcon(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOAD_GUILD_ICON.getValue());
        mplew.writeInt(chr.getId());
        if (chr.getGuildId() <= 0) {
            mplew.writeZeroBytes(6);
        } else {
            MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeShort(gs.getLogoBG());
                mplew.write(gs.getLogoBGColor());
                mplew.writeShort(gs.getLogo());
                mplew.write(gs.getLogoColor());
            } else {
                mplew.writeZeroBytes(6);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] changeTeam(int cid, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOAD_TEAM.getValue());
        mplew.writeInt(cid);
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] showHarvesting(int cid, int tool) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_HARVEST.getValue());
        mplew.writeInt(cid);
        if (tool > 0) {
            mplew.write(1);
            mplew.write(0);
            mplew.writeShort(0);
            mplew.writeInt(tool);
            mplew.writeZeroBytes(30);
        } else {
            mplew.write(0);
            mplew.writeZeroBytes(33);
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPHPBar(int cid, int hp, int maxHp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_HP.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(hp);
        mplew.writeInt(maxHp);

        return mplew.getPacket();
    }

    public static byte[] cancelChair(int id, int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_CHAIR.getValue());
        if (id == -1) {
            mplew.writeInt(cid);
            mplew.write(0);
        } else {
            mplew.writeInt(cid);
            mplew.write(1);
            mplew.writeShort(id);
        }

        return mplew.getPacket();
    }

    public static byte[] instantMapWarp(byte portal) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CURRENT_MAP_WARP.getValue());
        mplew.write(0);
        mplew.write(portal);

        return mplew.getPacket();
    }

    public static byte[] updateQuestInfo(MapleCharacter c, int quest, int npc, byte progress) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_QUEST_INFO.getValue());
        mplew.write(11);
        mplew.writeShort(quest);
        mplew.writeInt(npc);
        mplew.writeShort(0);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] updateQuestFinish(int quest, int npc, int nextquest) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_QUEST_INFO.getValue());
        mplew.write(11);
        mplew.writeShort(quest);
        mplew.writeInt(npc);
        mplew.writeShort(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] sendHint(String hint, int width, int height) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_HINT.getValue());
        mplew.writeMapleAsciiString(hint);
        mplew.writeShort(width < 1 ? Math.max(hint.length() * 10, 40) : width);
        mplew.writeShort(Math.max(height, 5));
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] updateCombo(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ARAN_COMBO.getValue());
        mplew.writeInt(value);

        return mplew.getPacket();
    }

    public static byte[] rechargeCombo(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ARAN_COMBO_RECHARGE.getValue());
        mplew.writeInt(value);

        return mplew.getPacket();
    }

    public static byte[] getFollowMessage(String msg) {
        return getGameMessage(msg, (short) 11);
    }

    public static byte[] getGameMessage(String msg, short colour) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GAME_MESSAGE.getValue());
        mplew.writeShort(colour);
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] getBuffZoneEffect(int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BUFF_ZONE_EFFECT.getValue());
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] getTimeBombAttack() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TIME_BOMB_ATTACK.getValue());
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(10);
        mplew.writeInt(6);

        return mplew.getPacket();
    }

    public static byte[] moveFollow(Point otherStart, Point myStart, Point otherEnd, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FOLLOW_MOVE.getValue());
        mplew.writeInt(0);
        mplew.writePos(otherStart);
        mplew.writePos(myStart);
        PacketHelper.serializeMovementList(mplew, moves);
        mplew.write(17);
        for (int i = 0; i < 8; i++) {
            mplew.write(0);
        }
        mplew.write(0);
        mplew.writePos(otherEnd);
        mplew.writePos(otherStart);
        mplew.writeZeroBytes(100);

        return mplew.getPacket();
    }

    public static byte[] getFollowMsg(int opcode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FOLLOW_MSG.getValue());
        mplew.writeLong(opcode);

        return mplew.getPacket();
    }

    public static byte[] registerFamiliar(MonsterFamiliar mf) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REGISTER_FAMILIAR.getValue());
        mplew.writeLong(mf.getId());
        mf.writeRegisterPacket(mplew, false);
        mplew.writeShort(mf.getVitality() >= 3 ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] createUltimate(int amount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CREATE_ULTIMATE.getValue());
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] harvestMessage(int oid, int msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HARVEST_MESSAGE.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(msg);

        return mplew.getPacket();
    }

    public static byte[] openBag(int index, int itemId, boolean firstTime) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OPEN_BAG.getValue());
        mplew.writeInt(index);
        mplew.writeInt(itemId);
        mplew.writeShort(firstTime ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] dragonBlink(int portalId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DRAGON_BLINK.getValue());
        mplew.write(portalId);

        return mplew.getPacket();
    }

    public static byte[] getPVPIceGage(int score) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_ICEGAGE.getValue());
        mplew.writeInt(score);

        return mplew.getPacket();
    }

    public static byte[] skillCooldown(int sid, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.COOLDOWN.getValue());
        mplew.writeInt(1);
        mplew.writeInt(sid);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] showFusionAnvil(int itemId, int giveItemId, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FUSION_ANVIL.getValue());
        mplew.write(success ? 1 : 0);
        mplew.writeInt(itemId);
        mplew.writeInt(giveItemId);

        return mplew.getPacket();
    }

    public static byte[] dropItemFromMapObject(MapleMapItem drop, Point dropfrom, Point dropto, byte mod) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DROP_ITEM_FROM_MAPOBJECT.getValue());
        mplew.write(0);
        mplew.write(mod);
        mplew.writeInt(drop.getObjectId());
        mplew.write(drop.getMeso() > 0 ? 1 : 0);
        mplew.writeInt(0);
        mplew.writeLong(0/*Randomizer.nextInt(255)*/);
        mplew.writeInt(drop.getItemId());
        mplew.writeInt(drop.getOwner());
        mplew.write(drop.getDropType());
        mplew.writePos(dropto);
        mplew.writeInt(0);
        mplew.writeInt(0);//v175+
        if (mod != 2) {
            mplew.writePos(dropfrom);
            mplew.writeShort(0);
            mplew.writeShort(0);
        }
        mplew.write(0);
        if (drop.getMeso() == 0) {
            PacketHelper.addExpirationTime(mplew, drop.getItem().getExpiration());
        }
        mplew.writeShort(drop.isPlayerDrop() ? 0 : 1);
        mplew.writeInt(0);
        mplew.writeInt(0);//new v148
        mplew.write(drop.getState());//潛能等級特效

        return mplew.getPacket();
    }

    public static byte[] explodeDrop(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_ITEM_FROM_MAP.getValue());
        mplew.write(4);
        mplew.writeInt(oid);
        mplew.writeShort(655);

        return mplew.getPacket();
    }

    public static byte[] removeItemFromMap(int oid, int animation, int cid) {
        return removeItemFromMap(oid, animation, cid, 0);
    }

    public static byte[] removeItemFromMap(int oid, int animation, int cid, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_ITEM_FROM_MAP.getValue());
        mplew.write(animation);
        mplew.writeInt(oid);
        if (animation >= 2) {
            mplew.writeInt(cid);
            if (animation == 5) {
                mplew.writeInt(slot);
            }
        }
        return mplew.getPacket();
    }

    public static byte[] spawnMist(MapleMist mist) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MIST.getValue());
        mplew.writeInt(mist.getObjectId());

        mplew.write(mist.isMobMist() ? 0 : mist.isPoisonMist());
        //mplew.write(0);
        mplew.writeInt(mist.getOwnerId());
        if (mist.getMobSkill() == null) {
            mplew.writeInt(mist.getSourceSkill().getId());
        } else {
            mplew.writeInt(mist.getMobSkill().getSkillId());
        }
        mplew.write(mist.getSkillLevel());
        mplew.writeShort(mist.getSkillDelay());
        mplew.writeRect(mist.getBox());
        mplew.writeInt(mist.isShelter() ? 1 : 0);
        //mplew.writeInt(0);
        mplew.writePos(mist.getPosition());
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        if (mist.getSourceSkill().getId() == 33121012 || mist.getSourceSkill().getId() == 35121052) {
            mplew.write(0);
        }

        return mplew.getPacket();
    }

    public static byte[] unkMist(int oid, List<Integer> unk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MIST_UNK.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(0);
        mplew.writeInt(unk.size());
        for (int mm : unk) {
            mplew.writeInt(mm);
        }

        return mplew.getPacket();
    }

    public static byte[] removeMist(int oid, boolean eruption) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_MIST.getValue());
        mplew.writeInt(oid);
        mplew.write(eruption ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] spawnDoor(int oid, int skillId, Point pos, boolean animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_DOOR.getValue());
        mplew.write(animation ? 0 : 1);
        mplew.writeInt(oid);
        mplew.writeInt(skillId);
        mplew.writePos(pos);

        return mplew.getPacket();
    }

    public static byte[] removeDoor(int oid, boolean animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REMOVE_DOOR.getValue());
        mplew.write(animation ? 0 : 1);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static byte[] spawnKiteError() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_KITE_ERROR.getValue());

        return mplew.getPacket();
    }

    public static byte[] spawnKite(int oid, int id, Point pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_KITE.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(0);
        mplew.writeMapleAsciiString("");
        mplew.writeMapleAsciiString("");
        mplew.writePos(pos);

        return mplew.getPacket();
    }

    public static byte[] destroyKite(int oid, int id, boolean animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.DESTROY_KITE.getValue());
        mplew.write(animation ? 0 : 1);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static byte[] spawnMechDoor(MechDoor md, boolean animated) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MECH_DOOR_SPAWN.getValue());
        mplew.write(animated ? 0 : 1);
        mplew.writeInt(md.getOwnerId());
        mplew.writePos(md.getTruePosition());
        mplew.write(md.getId());
        mplew.writeInt(md.getPartyId());
        return mplew.getPacket();
    }

    public static byte[] removeMechDoor(MechDoor md, boolean animated) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MECH_DOOR_REMOVE.getValue());
        mplew.write(animated ? 0 : 1);
        mplew.writeInt(md.getOwnerId());
        mplew.write(md.getId());

        return mplew.getPacket();
    }

    //[8A 16 25 00] [03] [9D 07 7F 01] [06 01 00 06]
    public static byte[] triggerReactor(MapleReactor reactor, int stance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REACTOR_HIT.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getTruePosition());
        mplew.writeInt(stance);
        //mplew.write(0);
        //mplew.write(4);
        return mplew.getPacket();
    }

    public static byte[] spawnReactor(MapleReactor reactor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REACTOR_SPAWN.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.writeInt(reactor.getReactorId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getTruePosition());
        mplew.write(reactor.getFacingDirection());
        mplew.writeMapleAsciiString(reactor.getName());

        return mplew.getPacket();
    }

    public static byte[] destroyReactor(MapleReactor reactor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REACTOR_DESTROY.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getPosition());

        return mplew.getPacket();
    }

    public static byte[] makeExtractor(int cid, String cname, Point pos, int timeLeft, int itemId, int fee) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_EXTRACTOR.getValue());
        mplew.writeInt(cid);
        mplew.writeMapleAsciiString(cname);
        mplew.writeInt(pos.x);
        mplew.writeInt(pos.y);
        mplew.writeShort(timeLeft);
        mplew.writeInt(itemId);
        mplew.writeInt(fee);

        return mplew.getPacket();
    }

    public static byte[] removeExtractor(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_EXTRACTOR.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(1);

        return mplew.getPacket();
    }

    public static byte[] rollSnowball(int type, MapleSnowball.MapleSnowballs ball1, MapleSnowball.MapleSnowballs ball2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ROLL_SNOWBALL.getValue());
        mplew.write(type);
        mplew.writeInt(ball1 == null ? 0 : ball1.getSnowmanHP() / 75);
        mplew.writeInt(ball2 == null ? 0 : ball2.getSnowmanHP() / 75);
        mplew.writeShort(ball1 == null ? 0 : ball1.getPosition());
        mplew.write(0);
        mplew.writeShort(ball2 == null ? 0 : ball2.getPosition());
        mplew.writeZeroBytes(11);

        return mplew.getPacket();
    }

    public static byte[] enterSnowBall() {
        return rollSnowball(0, null, null);
    }

    public static byte[] hitSnowBall(int team, int damage, int distance, int delay) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HIT_SNOWBALL.getValue());
        mplew.write(team);
        mplew.writeShort(damage);
        mplew.write(distance);
        mplew.write(delay);

        return mplew.getPacket();
    }

    public static byte[] snowballMessage(int team, int message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SNOWBALL_MESSAGE.getValue());
        mplew.write(team);
        mplew.writeInt(message);

        return mplew.getPacket();
    }

    public static byte[] leftKnockBack() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LEFT_KNOCK_BACK.getValue());

        return mplew.getPacket();
    }

    public static byte[] hitCoconut(boolean spawn, int id, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HIT_COCONUT.getValue());
        mplew.writeShort(spawn ? 0x8000 : id);
        mplew.writeShort(0); // 延遲時間
        mplew.write(spawn ? 0 : type);

        return mplew.getPacket();
    }

    public static byte[] coconutScore(int[] coconutscore) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.COCONUT_SCORE.getValue());
        mplew.writeShort(coconutscore[0]);
        mplew.writeShort(coconutscore[1]);

        return mplew.getPacket();
    }

    public static byte[] updateAriantScore(List<MapleCharacter> players) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ARIANT_SCORE_UPDATE.getValue());
        mplew.write(players.size());
        for (MapleCharacter i : players) {
            mplew.writeMapleAsciiString(i.getName());
            mplew.writeInt(0);
        }

        return mplew.getPacket();
    }

    public static byte[] sheepRanchInfo(byte wolf, byte sheep) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHEEP_RANCH_INFO.getValue());
        mplew.write(wolf);
        mplew.write(sheep);

        return mplew.getPacket();
    }

    public static byte[] sheepRanchClothes(int cid, byte clothes) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHEEP_RANCH_CLOTHES.getValue());
        mplew.writeInt(cid);
        mplew.write(clothes);

        return mplew.getPacket();
    }

    public static byte[] updateWitchTowerKeys(int keys) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WITCH_TOWER.getValue());
        mplew.write(keys);

        return mplew.getPacket();
    }

    public static byte[] showChaosZakumShrine(boolean spawned, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHAOS_ZAKUM_SHRINE.getValue());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] showChaosHorntailShrine(boolean spawned, int time) {
        return showHorntailShrine(spawned, time);
    }

    public static byte[] showHorntailShrine(boolean spawned, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HORNTAIL_SHRINE.getValue());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] getRPSMode(byte mode, int mesos, int selection, int answer) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.RPS_GAME.getValue());
        mplew.write(mode);
        switch (mode) {
            case 6:
                if (mesos == -1) {
                    break;
                }
                mplew.writeInt(mesos);
                break;
            case 8:
                mplew.writeInt(9000019);
                break;
            case 11:
                mplew.write(selection);
                mplew.write(answer);
        }

        return mplew.getPacket();
    }

    public static byte[] messengerInvite(String from, int messengerid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(3);
        mplew.writeMapleAsciiString(from);
        mplew.write(1);//channel?
        mplew.writeInt(messengerid);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] addMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0);
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true, false);
        mplew.writeMapleAsciiString(from);
        mplew.write(channel);
        mplew.write(1); // v140
        mplew.writeInt(chr.getJob());

        return mplew.getPacket();
    }

    public static byte[] removeMessengerPlayer(int position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(2);
        mplew.write(position);

        return mplew.getPacket();
    }

    public static byte[] updateMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0); // v140.
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true, false);
        mplew.writeMapleAsciiString(from);
        mplew.write(channel);
        mplew.write(0); // v140.
        mplew.writeInt(chr.getJob()); // doubt it's the job, lol. v140.

        return mplew.getPacket();
    }

    public static byte[] joinMessenger(int position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(1);
        mplew.write(position);

        return mplew.getPacket();
    }

    public static byte[] messengerChat(String charname, String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(6);
        mplew.writeMapleAsciiString(charname);
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static byte[] messengerNote(String text, int mode, int mode2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(text);
        mplew.write(mode2);

        return mplew.getPacket();
    }

    public static byte[] messengerOpen(byte type, List<MapleCharacter> chars) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER_OPEN.getValue());
        mplew.write(type); //7 in messenger open ui 8 new ui
        if (chars.isEmpty()) {
            mplew.writeShort(0);
        }
        for (MapleCharacter chr : chars) {
            mplew.write(1);
            mplew.writeInt(chr.getId());
            mplew.writeInt(0); //likes
            mplew.writeLong(0); //some time
            mplew.writeMapleAsciiString(chr.getName());
            PacketHelper.addCharLook(mplew, chr, true, false);
        }

        return mplew.getPacket();
    }

    public static byte[] messengerCharInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0x0B);
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeInt(chr.getJob());
        mplew.writeInt(chr.getFame());
        mplew.writeInt(0); //likes
        MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
        mplew.writeMapleAsciiString(gs != null ? gs.getName() : "-");
        MapleGuildAlliance alliance = World.Alliance.getAlliance(gs.getAllianceId());
        mplew.writeMapleAsciiString(alliance != null ? alliance.getName() : "");
        mplew.write(2);

        return mplew.getPacket();
    }

    public static byte[] removeFromPackageList(boolean remove, int Package) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PACKAGE_OPERATION.getValue());
        mplew.write(24);
        mplew.writeInt(Package);
        mplew.write(remove ? 3 : 4);

        return mplew.getPacket();
    }

    public static byte[] sendPackageMSG(byte operation, List<MaplePackageActions> packages) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PACKAGE_OPERATION.getValue());
        mplew.write(operation);

        switch (operation) {
            case 9:
                mplew.write(1);
                break;
            case 10:
                mplew.write(0);
                mplew.write(packages.size());

                for (MaplePackageActions dp : packages) {
                    mplew.writeInt(dp.getPackageId());
                    mplew.writeAsciiString(dp.getSender(), 13);
                    mplew.writeInt(dp.getMesos());
                    mplew.writeLong(PacketHelper.getTime(dp.getSentTime()));
                    mplew.writeZeroBytes(205);

                    if (dp.getItem() != null) {
                        mplew.write(1);
                        PacketHelper.addItemInfo(mplew, dp.getItem());
                    } else {
                        mplew.write(0);
                    }
                }
                mplew.write(0);
        }

        return mplew.getPacket();
    }

    public static byte[] getKeymap(MapleKeyLayout layout, MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.KEYMAP.getValue());
        layout.writeData(mplew, chr);

        return mplew.getPacket();
    }

    public static byte[] petAutoHP(int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PET_AUTO_HP.getValue());
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] petAutoMP(int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PET_AUTO_MP.getValue());
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] petAutoCure(int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PET_AUTO_CURE.getValue());
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] petAutoBuff(int skillId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        //mplew.writeShort(SendPacketOpcode.PET_AUTO_BUFF.getValue());
        mplew.writeInt(skillId);

        return mplew.getPacket();
    }

    public static void addRingInfo(MaplePacketLittleEndianWriter mplew, List<MapleRing> rings) {
        mplew.write(rings.size());
        for (MapleRing ring : rings) {
            mplew.writeInt(1);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
            mplew.writeInt(ring.getItemId());
        }
    }

    public static void addMRingInfo(MaplePacketLittleEndianWriter mplew, List<MapleRing> rings, MapleCharacter chr) {
        mplew.write(rings.size());
        for (MapleRing ring : rings) {
            mplew.writeInt(1);
            mplew.writeInt(chr.getId());
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeInt(ring.getItemId());
        }
    }

    public static byte[] getBuffBar(long millis) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BUFF_BAR.getValue());
        mplew.writeLong(millis);

        return mplew.getPacket();
    }

    public static byte[] getBoosterFamiliar(int cid, int familiar, int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOOSTER_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(familiar);
        mplew.writeLong(id);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] viewSkills(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TARGET_SKILL.getValue());
        List skillz = new ArrayList();
        for (Skill sk : chr.getSkills().keySet()) {
            if ((sk.canBeLearnedBy(chr.getJob())) && (GameConstants.canSteal(sk)) && (!skillz.contains(sk.getId()))) {
                skillz.add(sk.getId());
            }
        }
        mplew.write(1);
        mplew.writeInt(chr.getId());
        mplew.writeInt(skillz.isEmpty() ? 2 : 4);
        mplew.writeInt(chr.getJob());
        mplew.writeInt(skillz.size());
        for (Iterator i$ = skillz.iterator(); i$.hasNext();) {
            int i = ((Integer) i$.next());
            mplew.writeInt(i);
        }

        return mplew.getPacket();
    }

    public static byte[] spawnArrowBlaster(MapleCharacter chr, int x, int y, int a) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_ARROW_BLASTER.getValue());
        mplew.writeInt(1);
        mplew.writeInt(1);
        mplew.writeInt(chr.getId());
        mplew.writeInt(0);
        mplew.writeInt((int) chr.getPosition().getX());
        mplew.writeInt((int) chr.getPosition().getY());
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] controlArrowBlaster(int a) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ARROW_BLASTER_CONTROL.getValue());
        mplew.writeInt(a);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] cancelArrowBlaster(int b) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_ARROW_BLASTER.getValue());
        mplew.writeInt(1);
        mplew.writeInt(b);

        return mplew.getPacket();
    }

    public static class InteractionPacket {

        public static byte[] getTradeInvite(MapleCharacter c) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(PlayerInteractionHandler.Interaction.INVITE_TRADE.action);
            mplew.write(4);//was 3
            mplew.writeMapleAsciiString(c.getName());
//            mplew.writeInt(c.getLevel());
            mplew.writeInt(c.getJob());
            return mplew.getPacket();
        }

        public static byte[] getTradeMesoSet(byte number, long meso) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(PlayerInteractionHandler.Interaction.UPDATE_MESO.action);
            mplew.write(number);
            mplew.writeLong(meso);
            return mplew.getPacket();
        }

        public static byte[] getTradeItemAdd(byte number, Item item) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(PlayerInteractionHandler.Interaction.SET_ITEMS.action);
            mplew.write(number);
            mplew.write(item.getPosition());
            PacketHelper.addItemInfo(mplew, item);

            return mplew.getPacket();
        }

        public static byte[] getTradeStart(MapleClient c, MapleTrade trade, byte number) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
//            mplew.write(PlayerInteractionHandler.Interaction.START_TRADE.action);
//            if (number != 0){//13 a0
////                mplew.write(HexTool.getByteArrayFromHexString("13 01 01 03 FE 53 00 00 40 08 00 00 00 E2 7B 00 00 01 E9 50 0F 00 03 62 98 0F 00 04 56 BF 0F 00 05 2A E7 0F 00 07 B7 5B 10 00 08 3D 83 10 00 09 D3 D1 10 00 0B 13 01 16 00 11 8C 1F 11 00 12 BF 05 1D 00 13 CB 2C 1D 00 31 40 6F 11 00 32 6B 46 11 00 35 32 5C 19 00 37 20 E2 11 00 FF 03 B6 98 0F 00 05 AE 0A 10 00 09 CC D0 10 00 FF FF 00 00 00 00 13 01 16 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 4D 6F 6D 6F 6C 6F 76 65 73 4B 48 40 08"));
//                mplew.write(19);
//                mplew.write(1);
//                PacketHelper.addCharLook(mplew, trade.getPartner().getChr(), false);
//                mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
//                mplew.writeShort(trade.getPartner().getChr().getJob());
//            }else{
            mplew.write(20);
            mplew.write(4);
            mplew.write(2);
            mplew.write(number);

            if (number == 1) {
                mplew.write(0);
                PacketHelper.addCharLook(mplew, trade.getPartner().getChr(), false, false);
                mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
                mplew.writeShort(trade.getPartner().getChr().getJob());
            }
            mplew.write(number);
            PacketHelper.addCharLook(mplew, c.getPlayer(), false, false);
            mplew.writeMapleAsciiString(c.getPlayer().getName());
            mplew.writeShort(c.getPlayer().getJob());
            mplew.write(255);
//            }
            return mplew.getPacket();
        }

        public static byte[] getTradeConfirmation() {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(PlayerInteractionHandler.Interaction.CONFIRM_TRADE.action);

            return mplew.getPacket();
        }

        public static byte[] TradeMessage(byte UserSlot, byte message) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(PlayerInteractionHandler.Interaction.EXIT.action);
//            mplew.write(25);//new v141
            mplew.write(UserSlot);
            mplew.write(message);

            return mplew.getPacket();
        }

        public static byte[] getTradeCancel(byte UserSlot, int unsuccessful) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(PlayerInteractionHandler.Interaction.EXIT.action);
            mplew.write(UserSlot);
            mplew.write(7);//was2

            return mplew.getPacket();
        }
    }

    public static class NPCPacket {

        public static byte[] spawnNPC(MapleNPC life, boolean show) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SPAWN_NPC.getValue());
            mplew.writeInt(life.getObjectId());
            mplew.writeInt(life.getId());
            mplew.writeShort(life.getPosition().x);
            mplew.writeShort(life.getCy());
            mplew.write(life.getF() == 1 ? 0 : 1);
            mplew.writeShort(life.getFh());
            mplew.writeShort(life.getRx0());
            mplew.writeShort(life.getRx1());
            mplew.write(show ? 1 : 0);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeInt(-1);
            mplew.writeLong(0);
            mplew.writeZeroBytes(3);//176+

            return mplew.getPacket();
        }

        public static byte[] removeNPC(int objectid) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.REMOVE_NPC.getValue());
            mplew.writeInt(objectid);

            return mplew.getPacket();
        }

        public static byte[] removeNPCController(int objectid) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
            mplew.write(0);
            mplew.writeInt(objectid);

            return mplew.getPacket();
        }

        public static byte[] spawnNPCRequestController(int npc) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
            mplew.write(0);
            mplew.writeInt(npc);

            return mplew.getPacket();
        }

        public static byte[] spawnNPCRequestController(MapleNPC life, boolean MiniMap) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
            mplew.write(1);
            mplew.writeInt(life.getObjectId());
            mplew.writeInt(life.getId());
            mplew.writeShort(life.getPosition().x);
            mplew.writeShort(life.getCy());
            mplew.write(life.getF() == 1 ? 0 : 1);
            mplew.writeShort(life.getFh());
            mplew.writeShort(life.getRx0());
            mplew.writeShort(life.getRx1());
            mplew.write(MiniMap ? 1 : 0);
            mplew.writeInt(0);//new 143
            mplew.write(0);
            mplew.writeInt(-1);
            mplew.writeLong(0);
            mplew.write(0);
            mplew.write(0);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] toggleNPCShow(int oid, boolean hide) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.NPC_TOGGLE_VISIBLE.getValue());
            mplew.writeInt(oid);
            mplew.write(hide ? 0 : 1);
            return mplew.getPacket();
        }

        public static byte[] setNPCSpecialAction(int oid, String action) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.NPC_SET_SPECIAL_ACTION.getValue());
            mplew.writeInt(oid);
            mplew.writeMapleAsciiString(action);
            mplew.writeInt(0); //unknown yet
            mplew.write(0); //unknown yet
            return mplew.getPacket();
        }

        public static byte[] NPCSpecialAction(int oid, int value, int x, int y) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.NPC_UPDATE_LIMITED_INFO.getValue());
            mplew.writeInt(oid);
            mplew.writeInt(value);
            mplew.writeInt(x);
            mplew.writeInt(y);

            return mplew.getPacket();
        }

        public static byte[] setNPCScriptable() {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.NPC_SCRIPTABLE.getValue());
            List<Pair<Integer, String>> npcs = new LinkedList();
            npcs.add(new Pair<>(9070006, "Why...why has this happened to me? My knightly honor... My knightly pride..."));
            npcs.add(new Pair<>(9000021, "Are you enjoying the event?"));
            mplew.write(npcs.size());
            for (Pair<Integer, String> s : npcs) {
                mplew.writeInt(s.getLeft());
                mplew.writeMapleAsciiString(s.getRight());
                mplew.writeInt(0);
                mplew.writeInt(Integer.MAX_VALUE);
            }
            return mplew.getPacket();
        }

        public static byte[] getNPCTalk(int npc, byte msgType, String talk, String endBytes, byte type) {
            return getNPCTalk(npc, msgType, talk, endBytes, type, npc);
        }

        public static byte[] getNPCTalk(int npc, byte msgType, String talk, String endBytes, byte type, int diffNPC) {
            return getNPCTalk(npc, msgType, talk, endBytes, type, (byte) 0, diffNPC);
        }

        public static byte[] getNPCTalk(int npc, byte msgType, String talk, String endBytes, byte type, byte type2, int diffNPC) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(0); // Boolean
            mplew.write(msgType);
            mplew.write(type);
            mplew.write(type2);
            if ((type & 0x4) != 0) {
                mplew.writeInt(diffNPC);
            }
            mplew.writeMapleAsciiString(talk);
            mplew.write(HexTool.getByteArrayFromHexString(endBytes));

            return mplew.getPacket();
        }

        public static byte[] getZeroNPCTalk(int npc, byte msgType, String talk, String endBytes, byte type, int diffNPC) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(3);
            mplew.writeInt(0);
            mplew.write(1); // Boolean
            mplew.writeInt(npc);
            mplew.write(msgType);
            mplew.write(type);
            mplew.write(0);
            if ((type & 0x4) != 0) {
                mplew.writeInt(diffNPC);
            }
            mplew.writeMapleAsciiString(talk);
            mplew.write(HexTool.getByteArrayFromHexString(endBytes));

            return mplew.getPacket();
        }

        public static byte[] getSengokuNPCTalk(boolean unknown, int npc, byte msgType, byte type, int diffNPC, String talk, boolean next, boolean prev, boolean pic) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(unknown ? 4 : 3);
            if (!unknown) {
                mplew.writeInt(0);
            }
            mplew.write(unknown); // Boolean
            if (unknown) {
                mplew.writeInt(npc);
            }
            mplew.write(msgType);
            mplew.write(type);
            mplew.write(0);//?
            if ((type & 0x4) != 0) {
                mplew.writeInt(diffNPC);
            }
            mplew.writeMapleAsciiString(talk);
            mplew.write(next);
            mplew.write(prev);
            mplew.writeInt(diffNPC);
            mplew.write(pic);
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        public static byte[] getEnglishQuiz(int npc, byte type, int diffNPC, String talk, String endBytes) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(0); // Boolean
            mplew.write(10); //not sure
            mplew.write(type);
            mplew.write(0);
            if ((type & 0x4) != 0) {
                mplew.writeInt(diffNPC);
            }
            mplew.writeMapleAsciiString(talk);
            mplew.write(HexTool.getByteArrayFromHexString(endBytes));

            return mplew.getPacket();
        }

        public static byte[] getAdviceTalk(String[] wzinfo) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(8);
            mplew.writeInt(0);
            mplew.write(0); // Boolean
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);
            mplew.write(wzinfo.length);
            for (String data : wzinfo) {
                mplew.writeMapleAsciiString(data);
            }
            return mplew.getPacket();
        }

        public static byte[] getSlideMenu(int npcid, int type, int lasticon, String sel) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            //Types: 0 - map selection 1 - neo city map selection 2 - korean map selection 3 - tele rock map selection 4 - dojo buff selection
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4); // slide menu
            mplew.writeInt(npcid);
            mplew.write(0); // Boolean
            mplew.write(0x11);
            mplew.write(0);
            mplew.write(0); // 175+
            mplew.writeInt(type); // 選單類型
            mplew.writeInt(type == 0 ? lasticon : 0); // last icon on menu
            mplew.writeMapleAsciiString(sel);

            return mplew.getPacket();
        }

        public static byte[] getNPCTalkStyle(int npc, String talk, int[] args, boolean second) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(0); // Boolean
            mplew.write(9);
            mplew.write(0);
            mplew.write(0);
            mplew.write(second ? 1 : 0);
            mplew.write(0);
            mplew.writeMapleAsciiString(talk);
            mplew.write(args.length);
            for (int i = 0; i < args.length; i++) {
                mplew.writeInt(args[i]);
            }
            return mplew.getPacket();
        }

        public static byte[] getNPCTalkNum(int npc, String talk, int def, int min, int max) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(0); // Boolean
            mplew.write(4);
            mplew.write(0);
            mplew.write(0);
            mplew.writeMapleAsciiString(talk);
            mplew.writeInt(def);
            mplew.writeInt(min);
            mplew.writeInt(max);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] getNPCTalkText(int npc, String talk) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(0); // Boolean
            mplew.write(3); // 3 regular 6 quiz
            mplew.write(0);
            mplew.write(0);
            mplew.writeMapleAsciiString(talk);
            mplew.writeMapleAsciiString("");
            mplew.writeShort(0);
            mplew.writeShort(0);

            return mplew.getPacket();
        }

        public static byte[] getNPCTalkQuiz(int npc, String caption, String talk, int time) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(0); // Boolean
            mplew.write(6);
            mplew.write(0);
            mplew.write(0);
            mplew.write(0); // Boolean
            mplew.writeMapleAsciiString(caption);
            mplew.writeMapleAsciiString(talk);
            mplew.writeMapleAsciiString("");
            mplew.writeInt(0);
            mplew.writeInt(0xF); //no idea
            mplew.writeInt(time); //seconds

            return mplew.getPacket();
        }

        public static byte[] getSelfTalkText(String text) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(3);
            mplew.writeInt(0);
            mplew.write(1); // Boolean
            mplew.writeInt(0);
            mplew.write(0);
            mplew.write(0x11);
            mplew.write(0); // 173+
            mplew.writeMapleAsciiString(text);
            mplew.write(0);
            mplew.write(1);

            return mplew.getPacket();
        }

        public static byte[] getNPCTutoEffect(String effect) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(3);
            mplew.writeInt(0);
            mplew.write(0); // Boolean
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);
            mplew.write(1);
            mplew.writeMapleAsciiString(effect);

            return mplew.getPacket();
        }

        public static byte[] getDemonSelection() {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(3);
            mplew.writeInt(0);
            mplew.write(1); // Boolean
            mplew.writeInt(2159311); // npcID
            mplew.write(0x17);
            mplew.write(1);
            mplew.write(0);
            mplew.writeShort(1);
            mplew.writeZeroBytes(8);

            return mplew.getPacket();
        }

        public static byte[] getLuminousSelection() {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(3);
            mplew.writeInt(0);
            mplew.write(1);
            mplew.writeInt(2159311); //npc
            mplew.write(0x17);
            mplew.write(1);
            mplew.write(0);
            mplew.writeShort(0);
            mplew.writeZeroBytes(8);
            return mplew.getPacket();
        }

        public static byte[] getAngelicBusterAvatarSelect(int npc) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(0); // Boolean
            mplew.write(0x18);
            mplew.write(0);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] getEvanTutorial(String data) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(8);
            mplew.writeInt(0);
            mplew.write(0); // Boolean
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);
            mplew.write(1);
            mplew.writeMapleAsciiString(data);

            return mplew.getPacket();
        }

        public static byte[] getNPCShop(int sid, MapleShop shop, MapleClient c) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_NPC_SHOP.getValue());
            mplew.write(0); // Boolean [true => + [Int]]
            mplew.writeInt(0);
            mplew.writeInt(sid);
            PacketHelper.addShopInfo(mplew, shop, c);

            return mplew.getPacket();
        }

        public static byte[] confirmShopTransaction(byte code, MapleShop shop, MapleClient c, int indexBought) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.CONFIRM_SHOP_TRANSACTION.getValue());
            mplew.write(code);
            if (code == 8) {
                mplew.writeInt(0);
                mplew.writeInt(shop.getNpcId());
                PacketHelper.addShopInfo(mplew, shop, c);
            } else {
                mplew.write(indexBought >= 0 ? 1 : 0);
                if (indexBought >= 0) {
                    mplew.writeInt(indexBought);
                } else {
                    mplew.write(0);
                }
                mplew.write(0);
                mplew.write(0);
            }

            return mplew.getPacket();
        }

        public static byte[] getStorage(int npcId, byte slots, Collection<Item> items, long meso) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(22);
            mplew.writeInt(npcId);
            mplew.write(slots);
            mplew.writeShort(126);
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.writeLong(meso);
            mplew.writeShort(0);
            mplew.write((byte) items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(mplew, item);
            }
            mplew.writeZeroBytes(2);//4

            return mplew.getPacket();
        }

        public static byte[] getStorageFull() {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(17);

            return mplew.getPacket();
        }

        public static byte[] mesoStorage(byte slots, long meso) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(19);
            mplew.write(slots);
            mplew.writeShort(2);
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.writeLong(meso);

            return mplew.getPacket();
        }

        public static byte[] arrangeStorage(byte slots, Collection<Item> items, boolean changed) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(15);
            mplew.write(slots);
            mplew.write(124);
            mplew.writeZeroBytes(10);
            mplew.write(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(mplew, item);
            }
            mplew.write(0);
            return mplew.getPacket();
        }

        public static byte[] storeStorage(byte slots, MapleInventoryType type, Collection<Item> items) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(13);
            mplew.write(slots);
            mplew.writeShort(type.getBitfieldEncoding());
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.write(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(mplew, item);
            }
            return mplew.getPacket();
        }

        public static byte[] takeOutStorage(byte slots, MapleInventoryType type, Collection<Item> items) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(9);
            mplew.write(slots);
            mplew.writeShort(type.getBitfieldEncoding());
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.write(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(mplew, item);
            }
            return mplew.getPacket();
        }
    }

    public static class SummonPacket {

        public static byte[] spawnSummon(MapleSummon summon, boolean animated) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SPAWN_SUMMON.getValue());
            mplew.writeInt(summon.getOwnerId());
            mplew.writeInt(summon.getObjectId());
            mplew.writeInt(summon.getSkill());
            mplew.write(summon.getOwnerLevel() - 1);
            mplew.write(summon.getSkillLevel());
            mplew.writePos(summon.getPosition());
            mplew.write((summon.getSkill() == 32111006) || (summon.getSkill() == 33101005) ? 5 : 4);// Summon Reaper Buff - Call of the Wild
            if ((summon.getSkill() == 35121003) && (summon.getOwner().getMap() != null)) {//Giant Robot SG-88
                mplew.writeShort(summon.getOwner().getMap().getFootholds().findBelow(summon.getPosition()).getId());
            } else {
                mplew.writeShort(0);
            }
            mplew.write(summon.getMovementType().getValue());
            mplew.write(summon.getSummonType());
            mplew.write(animated ? 1 : 0);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.write(1);
            mplew.writeInt(0);
            mplew.writeInt(0);

            MapleCharacter chr = summon.getOwner();
            boolean sendAddCharLook = ((summon.getSkill() == 4341006) && (chr != null));
            mplew.write(sendAddCharLook ? 1 : 0); // Mirrored Target
            if (sendAddCharLook) { // Mirrored Target
                PacketHelper.addCharLook(mplew, chr, true, false);
            }
            if (summon.getSkill() == 35111002) {// Rock 'n Shock
                boolean v8 = false;
                mplew.write(v8);
                if (v8) {
                    int v33 = 0;
                    do {
                        mplew.writeShort(0);
                        mplew.writeShort(0);
                        v33++;
                    } while (v33 < 3);
                }
            }
            if (summon.getSkill() == 42111003) {
                mplew.writeShort(0);
                mplew.writeShort(0);
                mplew.writeShort(0);
                mplew.writeShort(0);
            }

            return mplew.getPacket();
        }

        public static byte[] removeSummon(int ownerId, int objId) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.REMOVE_SUMMON.getValue());
            mplew.writeInt(ownerId);
            mplew.writeInt(objId);
            mplew.write(10);

            return mplew.getPacket();
        }

        public static byte[] removeSummon(MapleSummon summon, boolean animated) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.REMOVE_SUMMON.getValue());
            mplew.writeInt(summon.getOwnerId());
            mplew.writeInt(summon.getObjectId());
            if (animated) {
                switch (summon.getSkill()) {
                    case 35121003:
                        mplew.write(10);
                        break;
                    case 33101008:
                    case 35111001:
                    case 35111002:
                    case 35111005:
                    case 35111009:
                    case 35111010:
                    case 35111011:
                    case 35121009:
                    case 35121010:
                    case 35121011:
                        mplew.write(5);
                        break;
                    default:
                        mplew.write(4);
                        break;
                }
            } else {
                mplew.write(1);
            }

            return mplew.getPacket();
        }

        public static byte[] moveSummon(int cid, int oid, Point startPos, List<LifeMovementFragment> moves) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.MOVE_SUMMON.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(oid);
            mplew.writeInt(0);
            mplew.writePos(startPos);
            mplew.writeInt(0);
            PacketHelper.serializeMovementList(mplew, moves);

            return mplew.getPacket();
        }

        public static byte[] summonAttack(int cid, int summonSkillId, byte animation, List<Pair<Integer, Integer>> allDamage, int level, boolean darkFlare) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SUMMON_ATTACK.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(summonSkillId);
            mplew.write(level - 1);
            mplew.write(animation);
            mplew.write(allDamage.size());
            for (Pair attackEntry : allDamage) {
                mplew.writeInt(((Integer) attackEntry.left));
                mplew.write(7);
                mplew.writeInt(((Integer) attackEntry.right));
            }
            mplew.write(darkFlare ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] pvpSummonAttack(int cid, int playerLevel, int oid, int animation, Point pos, List<AttackPair> attack) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PVP_SUMMON.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(oid);
            mplew.write(playerLevel);
            mplew.write(animation);
            mplew.writePos(pos);
            mplew.writeInt(0);
            mplew.write(attack.size());
            for (AttackPair p : attack) {
                mplew.writeInt(p.objectid);
                mplew.writePos(p.point);
                mplew.write(p.attack.size());
                mplew.write(0);
                for (Pair atk : p.attack) {
                    mplew.writeInt(((Integer) atk.left));
                }
            }

            return mplew.getPacket();
        }

        public static byte[] summonSkill(int cid, int summonSkillId, int newStance) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SUMMON_SKILL.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(summonSkillId);
            mplew.write(newStance);

            return mplew.getPacket();
        }

        public static byte[] damageSummon(int cid, int summonSkillId, int damage, int unkByte, int monsterIdFrom) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.DAMAGE_SUMMON.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(summonSkillId);
            mplew.write(unkByte);
            mplew.writeInt(damage);
            mplew.writeInt(monsterIdFrom);
            mplew.write(0);

            return mplew.getPacket();
        }
    }

    public static class UIPacket {

        public static byte[] getDirectionStatus(boolean enable) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.DIRECTION_STATUS.getValue());
            mplew.write(enable ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] openUI(int type) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);

            mplew.writeShort(SendPacketOpcode.OPEN_UI.getValue());
            mplew.writeInt(type);

            return mplew.getPacket();
        }

        public static byte[] sendRepairWindow(int npc) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(10);

            mplew.writeShort(SendPacketOpcode.OPEN_UI_OPTION.getValue());
            mplew.writeInt(33);
            mplew.writeInt(npc);
            mplew.writeInt(0);//new143

            return mplew.getPacket();
        }

        public static byte[] sendJewelCraftWindow(int npc) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(10);

            mplew.writeShort(SendPacketOpcode.OPEN_UI_OPTION.getValue());
            mplew.writeInt(104);
            mplew.writeInt(npc);
            mplew.writeInt(0);//new143

            return mplew.getPacket();
        }

        public static byte[] startAzwan(int npc) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(10);
            mplew.writeShort(SendPacketOpcode.OPEN_UI_OPTION.getValue());
            mplew.writeInt(70);
            mplew.writeInt(npc);
            mplew.writeInt(0);//new143
            return mplew.getPacket();
        }

        public static byte[] openUIOption(int type, int npc) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(10);
            mplew.writeShort(SendPacketOpcode.OPEN_UI_OPTION.getValue());
            mplew.writeInt(type);
            mplew.writeInt(npc);
            return mplew.getPacket();
        }

        public static byte[] sendAttackOnTitanScore(int type, int npc) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(10);
            mplew.writeShort(SendPacketOpcode.OPEN_UI_OPTION.getValue());
            mplew.writeInt(type);
            mplew.writeInt(npc);
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        public static byte[] sendDojoResult(int points) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_UI_OPTION.getValue());
            mplew.writeInt(0x48);
            mplew.writeInt(points);

            return mplew.getPacket();
        }

        public static byte[] sendAzwanResult() {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_UI_OPTION.getValue());
            mplew.writeInt(0x45);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] DublStart(boolean dark) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            mplew.write(0x34);
            mplew.write(dark ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] DublStartAutoMove() {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.MOVE_SCREEN.getValue());
            mplew.write(3);
            mplew.writeInt(2);

            return mplew.getPacket();
        }

        public static byte[] IntroLock(boolean enable) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.INTRO_LOCK.getValue());
            mplew.write(enable ? 1 : 0);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        // 1 Enable 0: Disable 
        public static byte[] IntroEnableUI(int enable) {
            return IntroEnableUI(enable, enable);
        }

        public static byte[] IntroEnableUI(int enable, int enable2) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.INTRO_ENABLE_UI.getValue());
            mplew.write(enable > 0 ? 1 : 0);
            if (enable > 0) {
                mplew.writeShort(enable2);
                mplew.write(0);
            } else {
                mplew.write(enable < 0 ? 1 : 0);
            }

            return mplew.getPacket();
        }

        // 1 Enable 0: Disable 
        public static byte[] IntroDisableUI(boolean enable) {
            return IntroDisableUI(enable, enable ? 1 : 0);
        }

        public static byte[] IntroDisableUI(boolean enable, int enable2) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.INTRO_DISABLE_UI.getValue());
            mplew.write(enable ? 1 : 0);
            if (enable) {
                mplew.writeShort(enable2);
                mplew.write(0);
            } else {
                mplew.write(!enable ? 1 : 0);
            }

            return mplew.getPacket();
        }

        public static byte[] summonHelper(boolean summon) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SUMMON_HINT.getValue());
            mplew.write(summon ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] summonMessage(int type) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SUMMON_HINT_MSG.getValue());
            mplew.write(1);
            mplew.writeInt(type);
            mplew.writeInt(7000);

            return mplew.getPacket();
        }

        public static byte[] summonMessage(String message) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SUMMON_HINT_MSG.getValue());
            mplew.write(0);
            mplew.writeMapleAsciiString(message);
            mplew.writeInt(200);
            mplew.writeShort(0);
            mplew.writeInt(10000);

            return mplew.getPacket();
        }

        public static byte[] getDirectionInfo(int type, int value) {
            return getDirectionInfo(type, value, 0);
        }

        public static byte[] getDirectionInfo(int type, int value, int value2) {
            return getDirectionEffect(type, "", value, value2, 0, 0, 0, 0, 0, 0);
        }

        public static byte[] getDirectionInfo(String data, int value, int x, int y, int a, int b) {
            return getDirectionEffect(2, data, value, x, y, a, b, 0, 0, 0);
        }

        public static byte[] getDirectionEffect(String data, int value, int x, int y) {
            return getDirectionEffect(data, value, x, y, 0);
        }

        public static byte[] getDirectionEffect(String data, int value, int x, int y, int npc) {
            //[02]  [02 00 31 31] [84 03 00 00] [00 00 00 00] [88 FF FF FF] [01] [00 00 00 00] [01] [29 C2 1D 00] [00] [00]
            //[mod] [data       ] [value      ] [value2     ] [value3     ] [a1] [a3         ] [a2] [npc        ] [  ] [a4]
            return getDirectionEffect(2, data, value, x, y, 1, 1, 0, 0, npc);
        }

        public static byte[] getDirectionInfoNew(byte x, int value) {
            return getDirectionInfoNew(x, value, 0, 0);
        }

        public static byte[] getDirectionInfoNew(byte x, int value, int a, int b) {
            //[mod] [data] [value] [value2] [value3] [a1] ....
            return getDirectionEffect(5, "", x, value, a, b, 0, 0, 0, 0);
        }

        public static byte[] getDirectionEffect(int mod, String data, int value, int value2, int value3, int a1, int a2, int a3, int a4, int npc) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.DIRECTION_INFO.getValue());
            mplew.write(mod);
            switch (mod) {
                case 0:
                    mplew.writeInt(value);
                    if (value <= 0x455) {
                        mplew.writeInt(value2);
                    }
                    break;
                case 1:
                    mplew.writeInt(value);
                    break;
                case 2:
                    mplew.writeMapleAsciiString(data);
                    mplew.writeInt(value);
                    mplew.writeInt(value2);
                    mplew.writeInt(value3);
                    mplew.write(a1);
                    if (a1 > 0) {
                        mplew.writeInt(a3);
                    }
                    mplew.write(a2);
                    if (a2 > 0) {
                        mplew.writeInt(npc);
                        mplew.write(npc > 0 ? 0 : 1); // 暫時解決
                        mplew.write(a4);
                    }
                    break;
                case 3:
                    mplew.writeInt(value);
                    break;
                case 4:
                    mplew.writeMapleAsciiString(data);
                    mplew.writeInt(value);
                    mplew.writeInt(value2);
                    mplew.writeInt(value3);
                    break;
                case 5:
                    mplew.write(value);
                    mplew.writeInt(value2);
                    if (value2 > 0) {
                        if (value == 0) {
                            mplew.writeInt(value3);
                            mplew.writeInt(a1);
                        }
                    }
                    break;
                case 6:
                    mplew.write(value);
                    break;
                case 7:
                    mplew.writeInt(value);
                    mplew.writeInt(value2);
                    mplew.writeInt(value3);
                    mplew.writeInt(a1);
                    mplew.writeInt(a2);
                    break;
                case 8:
                    // CCameraWork::ReleaseCameraFromUserPoint
                    break;
                case 9:
                    mplew.write(value);
                    break;
                case 10:
                    mplew.writeInt(value);
                    break;
                case 11:
                    mplew.writeMapleAsciiString(data);
                    mplew.write(value);
                    break;
                case 12:
                    mplew.writeMapleAsciiString(data);
                    mplew.write(value);
                    mplew.writeShort(value2);
                    mplew.writeInt(value3);
                    mplew.writeInt(a1);
                    break;
                case 13:
                    mplew.write(value);
                    for (int i = 0; i >= value; i++) {
                        mplew.writeInt(value2); // 要重寫
                    }
                    break;
                case 14:
                    break;
                case 15:
                    mplew.writeInt(value);
                    mplew.writeInt(value2);
                    break;
                case 16:
                    mplew.write(value);
                    break;
                case 17:
                    mplew.write(value);
                    break;
                default:
                    System.out.println("CField.getDirectionInfo() is Unknow mod :: [" + mod + "]");
                    break;
            }

            return mplew.getPacket();
        }

        public static byte[] getDirectionFacialExpression(int expression, int duration) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.DIRECTION_FACIAL_EXPRESSION.getValue());
            mplew.writeInt(expression);
            mplew.writeInt(duration);
            mplew.write(0);

            /* Facial Expressions:
             * 0 - Normal 
             * 1 - F1
             * 2 - F2
             * 3 - F3
             * 4 - F4
             * 5 - F5
             * 6 - F6
             * 7 - F7
             * 8 - Vomit
             * 9 - Panic
             * 10 - Sweetness
             * 11 - Kiss
             * 12 - Wink
             * 13 - Ouch!
             * 14 - Goo goo eyes
             * 15 - Blaze
             * 16 - Star
             * 17 - Love
             * 18 - Ghost
             * 19 - Constant Sigh
             * 20 - Sleepy
             * 21 - Flaming hot
             * 22 - Bleh
             * 23 - No Face
             */
            return mplew.getPacket();
        }

        public static byte[] moveScreen(int x) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.MOVE_SCREEN_X.getValue());
            mplew.writeInt(x);
            mplew.writeInt(0);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] screenDown() {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.MOVE_SCREEN_DOWN.getValue());

            return mplew.getPacket();
        }

        public static byte[] resetScreen() {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.RESET_SCREEN.getValue());

            return mplew.getPacket();
        }

        public static byte[] reissueMedal(int itemId, int type) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.REISSUE_MEDAL.getValue());
            mplew.write(type);
            mplew.writeInt(itemId);

            return mplew.getPacket();
        }

        public static byte[] playMovie(String data, boolean show) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAY_MOVIE.getValue());
            mplew.writeMapleAsciiString(data);
            mplew.write(show ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] setRedLeafStatus(int joejoe, int hermoninny, int littledragon, int ika) {
            //packet made to set status
            //should remove it and make a handler for it, it's a recv opcode
            /*
             * slea:
             * E2 9F 72 00
             * 5D 0A 73 01
             * E2 9F 72 00
             * 04 00 00 00
             * 00 00 00 00
             * 75 96 8F 00
             * 55 01 00 00
             * 76 96 8F 00
             * 00 00 00 00
             * 77 96 8F 00
             * 00 00 00 00
             * 78 96 8F 00
             * 00 00 00 00
             */
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            //mplew.writeShort();
            mplew.writeInt(7512034); //no idea
            mplew.writeInt(24316509); //no idea
            mplew.writeInt(7512034); //no idea
            mplew.writeInt(4); //no idea
            mplew.writeInt(0); //no idea
            mplew.writeInt(9410165); //joe joe
            mplew.writeInt(joejoe); //amount points added
            mplew.writeInt(9410166); //hermoninny
            mplew.writeInt(hermoninny); //amount points added
            mplew.writeInt(9410167); //little dragon
            mplew.writeInt(littledragon); //amount points added
            mplew.writeInt(9410168); //ika
            mplew.writeInt(ika); //amount points added

            return mplew.getPacket();
        }

        public static byte[] sendRedLeaf(int points, boolean viewonly) {
            /*
             * slea:
             * 73 00 00 00
             * 0A 00 00 00
             * 01
             */
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(10);

            mplew.writeShort(SendPacketOpcode.OPEN_UI_OPTION.getValue());
            mplew.writeInt(0x73);
            mplew.writeInt(points);
            mplew.write(viewonly ? 1 : 0); //if view only, then complete button is disabled

            return mplew.getPacket();
        }
    }

    public static class EffectPacket {

        public static byte[] showForeignEffect(int effect) {
            return showForeignEffect(-1, effect);
        }

        public static byte[] showForeignEffect(int cid, int effect) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(effect);

            return mplew.getPacket();
        }

        public static byte[] showItemLevelupEffect() {
            return showForeignEffect(20);
        }

        public static byte[] showForeignItemLevelupEffect(int cid) {
            return showForeignEffect(cid, 20);
        }

        public static byte[] showOwnDiceEffect(int skillid, int effectid, int effectid2, int level) {
            return showDiceEffect(-1, skillid, effectid, effectid2, level);
        }

        public static byte[] showDiceEffect(int cid, int skillid, int effectid, int effectid2, int level) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(4);
            mplew.writeInt(effectid);
            mplew.writeInt(effectid2);
            mplew.writeInt(skillid);
            mplew.write(level);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] useCharm(byte charmsleft, byte daysleft, boolean safetyCharm) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            mplew.write(0xA);
            mplew.write(safetyCharm ? 1 : 0);
            mplew.write(charmsleft);
            mplew.write(daysleft);
            if (!safetyCharm) {
                mplew.writeInt(0);
            }

            return mplew.getPacket();
        }

        public static byte[] Mulung_DojoUp2() {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            mplew.write(0xC);

            return mplew.getPacket();
        }

        public static byte[] showOwnHpHealed(int amount) {
            return showHpHealed(-1, amount);
        }

        public static byte[] showHpHealed(int cid, int amount) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(0x20);
            mplew.writeInt(amount);

            return mplew.getPacket();
        }

        public static byte[] showRewardItemAnimation(int itemId, String effect) {
            return showRewardItemAnimation(itemId, effect, -1);
        }

        public static byte[] showRewardItemAnimation(int itemId, String effect, int from_playerid) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (from_playerid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(from_playerid);
            }
            mplew.write(0x13);
            mplew.writeInt(itemId);
            mplew.write((effect != null) && (effect.length() > 0) ? 1 : 0);
            if ((effect != null) && (effect.length() > 0)) {
                mplew.writeMapleAsciiString(effect);
            }

            return mplew.getPacket();
        }

        public static byte[] showCashItemEffect(int itemId) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            mplew.write(0x19);
            mplew.writeInt(itemId);

            return mplew.getPacket();
        }

        public static byte[] ItemMaker_Success() {
            return ItemMaker_Success_3rdParty(-1);
        }

        public static byte[] ItemMaker_Success_3rdParty(int from_playerid) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (from_playerid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(from_playerid);
            }
            mplew.write(0x15);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] useWheel(byte charmsleft) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            mplew.write(0x1A);
            mplew.write(charmsleft);

            return mplew.getPacket();
        }

        public static byte[] showOwnBuffEffect(int skillid, int effectid, int playerLevel, int skillLevel) {
            return showBuffeffect(-1, skillid, effectid, playerLevel, skillLevel, (byte) 3);
        }

        public static byte[] showOwnBuffEffect(int skillid, int effectid, int playerLevel, int skillLevel, byte direction) {
            return showBuffeffect(-1, skillid, effectid, playerLevel, skillLevel, direction);
        }

        public static byte[] showBuffeffect(int cid, int skillid, int effectid, int playerLevel, int skillLevel) {
            return showBuffeffect(cid, skillid, effectid, playerLevel, skillLevel, (byte) 3);
        }

        public static byte[] showBuffeffect(int cid, int skillid, int effectid, int playerLevel, int skillLevel, byte direction) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(effectid);
            mplew.writeInt(skillid);
            mplew.write(playerLevel);
            mplew.write(skillLevel);
            if (direction != 3) {
                mplew.write(direction);
            }
            /*if ((effectid == 2) && (skillid == 31111003)) {
             mplew.writeInt(0);
             }
             mplew.write(skillLevel);
             if ((direction != 3) || (skillid == 1320006) || (skillid == 30001062) || (skillid == 30001061)) {
             mplew.write(direction);
             }

             if (skillid == 30001062) {
             mplew.writeInt(0);
             }
             if (cid == -1) {
             mplew.writeZeroBytes(10);
             }
             mplew.writeZeroBytes(20);
             */
            return mplew.getPacket();
        }

        public static byte[] showWZEffect(String data) {
            return EffectPacket.showWZEffect(0x17, data);//0x15+2 173ok
        }

        public static byte[] showWZEffectNew(String data) {
            return EffectPacket.showWZEffect(0x1A, data);//173ok
        }

        public static byte[] showWZEffect(int value, String data) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            mplew.write(value);
            if (value == 0x17) {
                mplew.writeZeroBytes(9);
            }
            mplew.writeMapleAsciiString(data);

            return mplew.getPacket();
        }

        public static byte[] showOwnCraftingEffect(String effect, byte direction, int time, int mode) {
            return showCraftingEffect(-1, effect, direction, time, mode);
        }

        public static byte[] showCraftingEffect(int cid, String effect, byte direction, int time, int mode) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(0x2B);
            mplew.writeMapleAsciiString(effect);
            mplew.write(direction);
            mplew.writeInt(time);
            mplew.writeInt(mode);
            if (mode == 2) {
                mplew.writeInt(0);
            }

            return mplew.getPacket();
        }

        public static byte[] TutInstructionalBalloon(String data) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            mplew.write(0x1A);//0x19+1 173
            mplew.writeMapleAsciiString(data);
            mplew.writeInt(1);

            return mplew.getPacket();
        }

        public static byte[] showOwnPetLevelUp(byte index) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            mplew.write(6);
            mplew.write(0);
            mplew.writeInt(index);

            return mplew.getPacket();
        }

        public static byte[] showOwnChampionEffect() {
            return showChampionEffect(-1);
        }

        public static byte[] showChampionEffect(int from_playerid) {
            if (ServerConfig.logPackets) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (from_playerid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(from_playerid);
            }
            mplew.write(0x22);
            mplew.writeInt(30000);

            return mplew.getPacket();
        }

        public static byte[] updateDeathCount(int deathCount) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.DEATH_COUNT.getValue());
            mplew.writeInt(deathCount);

            return mplew.getPacket();
        }
    }

    public static byte[] showWeirdEffect(String effect, int itemId) {
        if (ServerConfig.logPackets) {
            System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        final tools.data.output.MaplePacketLittleEndianWriter mplew = new tools.data.output.MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
        mplew.write(0x2B);
        mplew.writeMapleAsciiString(effect);
        mplew.write(1);
        mplew.writeInt(0);//weird high number is it will keep showing it lol
        mplew.writeInt(2);
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] showWeirdEffect(int chrId, String effect, int itemId) {
        final tools.data.output.MaplePacketLittleEndianWriter mplew = new tools.data.output.MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(chrId);
        mplew.write(0x2B);
        mplew.writeMapleAsciiString(effect);
        mplew.write(1);
        mplew.writeInt(0);//weird high number is it will keep showing it lol
        mplew.writeInt(2);//this makes it read the itemId
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] enchantResult(int result, int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.STRENGTHEN_UI.getValue());
        mplew.writeInt(result);//0=fail/1=sucess/2=idk/3=shows stats
        mplew.writeInt(itemId);
        return mplew.getPacket();
    }

    public static byte[] sendSealedBox(short slot, int itemId, List<Integer> items) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SEALED_BOX.getValue());
        mplew.writeShort(slot);
        mplew.writeInt(itemId);
        mplew.writeInt(items.size());
        for (int item : items) {
            mplew.writeInt(item);
        }

        return mplew.getPacket();
    }

    public static byte[] unsealBox(int reward) {
        if (ServerConfig.logPackets) {
            System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_SPECIAL_EFFECT.getValue());
        mplew.write(0x33);
        mplew.write(1);
        mplew.writeInt(reward);
        mplew.writeInt(1);

        return mplew.getPacket();
    }

    public static byte[] getRandomResponse(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.RANDOM_RESPONSE.getValue());
        mplew.write(12);
        mplew.writeShort(1);
        mplew.writeLong(1);
        mplew.writeInt(100);
        mplew.writeInt(GameConstants.getCurrentDate());

        return mplew.getPacket();
    }

    public static byte[] getCassandrasCollection() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CASSANDRAS_COLLECTION.getValue());
        mplew.write(6);

        return mplew.getPacket();
    }

    public static byte[] getLuckyLuckyMonstory() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LUCKY_LUCKY_MONSTORY.getValue());
        mplew.writeShort(1);
        mplew.write(30);

        return mplew.getPacket();
    }

    public static void addMountId(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, int buffSrc) {
        Item c_mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -123);
        Item mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18);
        int mountId = GameConstants.getMountItem(buffSrc, chr);
        if ((mountId == 0) && (c_mount != null) && (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -124) != null)) {
            mplew.writeInt(c_mount.getItemId());
        } else if ((mountId == 0) && (mount != null) && (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -19) != null)) {
            mplew.writeInt(mount.getItemId());
        } else {
            mplew.writeInt(mountId);
        }
    }

    public static byte[] harvestResultEffect(int cid, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HARVESTED_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(0);
        mplew.write(0);

        return mplew.getPacket();
    }
}
