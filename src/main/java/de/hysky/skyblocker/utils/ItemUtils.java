package de.hysky.skyblocker.utils;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.hysky.skyblocker.SkyblockerMod;
import de.hysky.skyblocker.config.SkyblockerConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ItemUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemUtils.class);
    private static final Pattern WHITESPACES = Pattern.compile("^\\s*$");
    public static final String EXTRA_ATTRIBUTES = "ExtraAttributes";
    public static final String ID = "id";
    public static final String UUID = "uuid";

    public static void init() {
        if (SkyblockerConfigManager.get().general.debug) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal(SkyblockerMod.NAMESPACE)
                    .then(literal("debug")
                            .then(literal("dumpHeldItemNbt").executes(context -> {
                                LOGGER.info("[Skyblocker Debug] Held Item Nbt: {}", context.getSource().getPlayer().getMainHandStack().writeNbt(new NbtCompound()));
                                return Command.SINGLE_SUCCESS;
                            }))
                    )
            ));
        }
    }

    public static List<Text> getTooltip(ItemStack item) {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.player == null || item == null ? Collections.emptyList() : item.getTooltip(client.player, TooltipContext.Default.BASIC);
    }

    public static List<String> getTooltipStrings(ItemStack item) {
        List<Text> lines = getTooltip(item);
        List<String> list = new ArrayList<>();

        for (Text line : lines) {
            String string = line.getString();
            if (!WHITESPACES.matcher(string).matches())
                list.add(string);
        }

        return list;
    }

    /**
     * Gets the {@code ExtraAttributes} NBT tag from the item stack.
     *
     * @param stack the item stack to get the {@code ExtraAttributes} NBT tag from
     * @return an optional containing the {@code ExtraAttributes} NBT tag of the item stack
     */
    public static Optional<NbtCompound> getExtraAttributesOptional(@NotNull ItemStack stack) {
        return Optional.ofNullable(stack.getSubNbt(EXTRA_ATTRIBUTES));
    }

    /**
     * Gets the {@code ExtraAttributes} NBT tag from the item stack.
     *
     * @param stack the item stack to get the {@code ExtraAttributes} NBT tag from
     * @return the {@code ExtraAttributes} NBT tag of the item stack, or null if the item stack is null or does not have an {@code ExtraAttributes} NBT tag
     */
    @Nullable
    public static NbtCompound getExtraAttributes(@NotNull ItemStack stack) {
        return stack.getSubNbt(EXTRA_ATTRIBUTES);
    }

    /**
     * Gets the internal name of the item stack from the {@code ExtraAttributes} NBT tag.
     *
     * @param stack the item stack to get the internal name from
     * @return an optional containing the internal name of the item stack
     */
    public static Optional<String> getItemIdOptional(@NotNull ItemStack stack) {
        return getExtraAttributesOptional(stack).map(extraAttributes -> extraAttributes.getString(ID));
    }

    /**
     * Gets the internal name of the item stack from the {@code ExtraAttributes} NBT tag.
     *
     * @param stack the item stack to get the internal name from
     * @return the internal name of the item stack, or an empty string if the item stack is null or does not have an internal name
     */
    public static String getItemId(@NotNull ItemStack stack) {
        NbtCompound extraAttributes = getExtraAttributes(stack);
        return extraAttributes != null ? extraAttributes.getString(ID) : "";
    }

    /**
     * Gets the UUID of the item stack from the {@code ExtraAttributes} NBT tag.
     *
     * @param stack the item stack to get the UUID from
     * @return an optional containing the UUID of the item stack
     */
    public static Optional<String> getItemUuidOptional(@NotNull ItemStack stack) {
        return getExtraAttributesOptional(stack).map(extraAttributes -> extraAttributes.getString(UUID));
    }

    /**
     * Gets the UUID of the item stack from the {@code ExtraAttributes} NBT tag.
     *
     * @param stack the item stack to get the UUID from
     * @return the UUID of the item stack, or null if the item stack is null or does not have a UUID
     */
    public static String getItemUuid(@NotNull ItemStack stack) {
        NbtCompound extraAttributes = getExtraAttributes(stack);
        return extraAttributes != null ? extraAttributes.getString(UUID) : "";
    }

    @Nullable
    public static Durability getDurability(@NotNull ItemStack stack) {
        if (!Utils.isOnSkyblock() || !SkyblockerConfigManager.get().locations.dwarvenMines.enableDrillFuel || stack.isEmpty()) {
            return null;
        }

        if (getExtraAttributesOptional(stack).filter(extraAttributes -> extraAttributes.contains("drill_fuel") || extraAttributes.getString(ItemUtils.ID).equals("PICKONIMBUS")).isEmpty()) {
            return null;
        }

        int current = 0;
        int max = 0;
        String clearFormatting;

        for (String line : ItemUtils.getTooltipStrings(stack)) {
            clearFormatting = Formatting.strip(line);
            if (line.contains("Fuel: ")) {
                if (clearFormatting != null) {
                    String clear = Pattern.compile("[^0-9 /]").matcher(clearFormatting).replaceAll("").trim();
                    String[] split = clear.split("/");
                    current = Integer.parseInt(split[0]);
                    max = Integer.parseInt(split[1]) * 1000;
                    return new Durability(current, max);
                }
            } else if (line.contains("uses.")) {
                if (clearFormatting != null) {
                    int startIndex = clearFormatting.lastIndexOf("after") + 6;
                    int endIndex = clearFormatting.indexOf("uses", startIndex);
                    if (startIndex >= 0 && endIndex > startIndex) {
                        String usesString = clearFormatting.substring(startIndex, endIndex).trim();
                        current = Integer.parseInt(usesString);
                        max = 5000;
                    }
                    return new Durability(current, max);
                }
            }
        }

        return null;
    }

    public static ItemStack getSkyblockerStack() {
        try {
            return ItemStack.fromNbt(StringNbtReader.parse("{id:\"minecraft:player_head\",Count:1,tag:{SkullOwner:{Id:[I;-300151517,-631415889,-1193921967,-1821784279],Properties:{textures:[{Value:\"e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdjYzY2ODc0MjNkMDU3MGQ1NTZhYzUzZTA2NzZjYjU2M2JiZGQ5NzE3Y2Q4MjY5YmRlYmVkNmY2ZDRlN2JmOCJ9fX0=\"}]}}}}"));
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public record Durability(int current, int max) {
    }
}
