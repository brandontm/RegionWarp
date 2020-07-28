package com.brandontm.regionwarp.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bukkit.inventory.ItemStack;

import dev.dbassett.skullcreator.SkullCreator;

public class SkullUtil {
    private static final String DEFAULT_REGION_SKULL_URL = "b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d";
    private static Map<String, String> alphabetSkulls;

    /**
     * Get item from texture key for Minecraft URL
     * 
     * @param textureKey Texture key
     * @return
     */
    public static ItemStack itemFromUrl(String textureKey) {
        return SkullCreator.itemFromUrl("http://textures.minecraft.net/texture/" + textureKey.trim());
    }

    /**
     * Get Minecraft URL for character head
     * 
     * @param character Character to get
     * @return Minecraft URL for character head or default if character URL is not
     *         found. Can return null if alphabet skulls map was not loaded.
     */
    public static String getCharacterUrl(char character) {
        if (alphabetSkulls == null)
            return null;

        if (alphabetSkulls.get(String.valueOf(character)) == null) {
            return DEFAULT_REGION_SKULL_URL;
        } else {
            return alphabetSkulls.get(String.valueOf(character));
        }
    }

    /**
     * Loads alphabet skulls from "alphabet.json" resource
     */
    public static void loadAlphabetSkulls() throws FileNotFoundException, IOException {
        InputStream file = SkullUtil.class.getResourceAsStream("/alphabet.json");

        final InputStreamReader reader = new InputStreamReader(file);
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();

        Gson gson = new Gson();
        alphabetSkulls = gson.fromJson(reader, type);

        reader.close();
    }
}