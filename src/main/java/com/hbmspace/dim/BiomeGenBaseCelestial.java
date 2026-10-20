package com.hbmspace.dim;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BiomeGenBaseCelestial extends Biome {

    protected ArrayList<SpawnListEntry> creatures = new ArrayList<>();
    protected ArrayList<SpawnListEntry> monsters = new ArrayList<>();
    protected ArrayList<SpawnListEntry> waterCreatures = new ArrayList<>();
    protected ArrayList<SpawnListEntry> caveCreatures = new ArrayList<>();
    private final Map<EnumCreatureType, List<SpawnListEntry>> additionalCreatures = new HashMap<>();

    public BiomeGenBaseCelestial(BiomeProperties properties) {
        super(properties);
    }

    // Forge and spawn registration mods expect mutable, persistent lists,
    // including for creature types added with EnumHelper (e.g. Multi Mob).
    @Override
    public @NotNull List<SpawnListEntry> getSpawnableList(EnumCreatureType type) {
        return switch (type) {
            case MONSTER -> monsters;
            case CREATURE -> creatures;
            case WATER_CREATURE -> waterCreatures;
            case AMBIENT -> caveCreatures;
            default -> additionalCreatures.computeIfAbsent(type, key -> new ArrayList<>());
        };
    }
    
}
