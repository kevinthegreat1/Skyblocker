package me.xmrvizzy.skyblocker.skyblock.tabhud.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.xmrvizzy.skyblocker.mixin.PlayerListHudAccessor;

import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * This class may be used to get data from the player list.
 * It doesn't get its data every frame, instead, a scheduler is used to
 * update the data this class is holding periodically.
 * The list is sorted like in the vanilla game.
 */
public class PlayerListMgr {

    public static final Logger LOGGER = LoggerFactory.getLogger("Skyblocker Regex");

    private static List<PlayerListEntry> playerList;

    public static void updateList() {

        if (!Utils.isOnSkyblock()) {
            return;
        }

        ClientPlayNetworkHandler cpnwh = MinecraftClient.getInstance().getNetworkHandler();

        // check is needed, else game crash on server leave
        if (cpnwh != null) {
            playerList = cpnwh.getPlayerList()
                    .stream()
                    .sorted(PlayerListHudAccessor.getOrdering())
                    .toList();
        }
    }

    /**
     * Get the display name at some index of the player list and apply a pattern to
     * it
     * 
     * @return the matcher if p fully matches, else null
     */
    public static Matcher regexAt(int idx, Pattern p) {

        String str = PlayerListMgr.strAt(idx);

        if (str == null) {
            return null;
        }

        Matcher m = p.matcher(str);
        if (!m.matches()) {
            LOGGER.error("no match: \"{}\" against \"{}\"", str, p);
            return null;
        } else {
            return m;
        }
    }

    /**
     * Get the display name at some index of the player list as string
     * 
     * @return the string or null, if the display name is null, empty or whitespace
     *         only
     */
    public static String strAt(int idx) {

        if (playerList == null) {
            return null;
        }

        if (playerList.size() <= idx) {
            return null;
        }

        Text txt = playerList.get(idx).getDisplayName();
        if (txt == null) {
            return null;
        }
        String str = txt.getString().trim();
        if (str.length() == 0) {
            return null;
        }
        return str;
    }
    
    /**
     * Gets the display name at some index of the player list
     * 
     * @return the text or null, if the display name is null
     * 
     * @implNote currently designed specifically for crimson isles faction quests widget, might not work correctly without modification
     * for other stuff. you've been warned!
     */
    public static Text textAt(int idx) {
    	
    	if(playerList == null) {
    		return null;
    	}
    	
    	if(playerList.size() <= idx) {
    		return null;
    	}
    	
    	Text txt = playerList.get(idx).getDisplayName();
    	if(txt == null) {
    		return null;
    	}
    	
    	//Rebuild the text object to remove beginning space thats in all faction quest stuff
    	MutableText newTxt = Text.empty();
    	
    	for(int i = 0; i < txt.getSiblings().size(); i++) {
    		Text current = txt.getSiblings().get(i);
    		String textToAppend = current.getString();
    		newTxt.append(Text.literal((i == 0) ? StringUtils.removeStart(textToAppend, " ") : textToAppend ).setStyle(current.getStyle()));
    	}
    	
    	return newTxt;
    }

    /**
     * Get the display name at some index of the player list as Text as seen in the
     * game
     * 
     * @return the PlayerListEntry at that index
     */
    public static PlayerListEntry getRaw(int idx) {
        return playerList.get(idx);
    }

    public static int getSize() {
        return playerList.size();
    }

}
