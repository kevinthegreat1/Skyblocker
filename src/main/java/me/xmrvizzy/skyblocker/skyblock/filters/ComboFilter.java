package me.xmrvizzy.skyblocker.skyblock.filters;

import me.xmrvizzy.skyblocker.config.SkyblockerConfigManager;
import me.xmrvizzy.skyblocker.utils.chat.ChatFilterResult;

public class ComboFilter extends SimpleChatFilter {
    public ComboFilter() {
        super("^(\\+\\d+ Kill Combo \\+\\d+(% ✯ Magic Find| coins per kill|% Combat Exp)" +
                "|Your Kill Combo has expired! You reached a \\d+ Kill Combo!)$");
    }

    @Override
    public ChatFilterResult state() {
        return SkyblockerConfigManager.get().messages.hideCombo;
    }
}
