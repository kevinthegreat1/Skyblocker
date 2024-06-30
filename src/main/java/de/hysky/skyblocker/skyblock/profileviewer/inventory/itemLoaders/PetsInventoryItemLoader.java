package de.hysky.skyblocker.skyblock.profileviewer.inventory.itemLoaders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import de.hysky.skyblocker.skyblock.PetCache;
import de.hysky.skyblocker.skyblock.profileviewer.ProfileViewerScreen;
import de.hysky.skyblocker.skyblock.profileviewer.inventory.Pet;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PetsInventoryItemLoader extends ItemLoader {
    private static final List<String> TIER_ORDER = List.of("MYTHIC", "LEGENDARY", "EPIC", "RARE", "UNCOMMON", "COMMON");

    @Override
    public List<ItemStack> loadItems(JsonObject data) {
        List<Pet> petList = new ArrayList<>();
        try {
            JsonObject petsData = data.getAsJsonObject("pets_data");
            if (petsData != null && petsData.has("pets")) {
                for (var petElement : petsData.get("pets").getAsJsonArray()) {
                    PetCache.PetInfo petInfo = PetCache.PetInfo.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(petElement.toString())).getOrThrow();
                    petList.add(new Pet(petInfo));
                }
            }
        } catch (Exception e) {
            ProfileViewerScreen.LOGGER.error("[Skyblocker Profile Viewer] Failed to load pets", e);
        }

        // Sort pets by tier (in reverse order) and level (in reverse order)
        petList.sort(Comparator.comparingInt((Pet pet) -> TIER_ORDER.indexOf(pet.getTierAsString())).reversed().thenComparingInt(Pet::getLevel).reversed());

        List<ItemStack> itemList = new ArrayList<>();
        for (Pet pet : petList) {
            itemList.add(pet.getIcon());
        }
        return itemList;
    }
}
