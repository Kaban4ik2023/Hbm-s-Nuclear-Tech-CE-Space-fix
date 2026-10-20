package com.hbmspace;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbmspace.capability.HbmLivingCapabilitySpace;
import com.hbmspace.dim.BiomeGenBaseCelestial;
import com.hbmspace.dim.CelestialBody;
import com.hbmspace.util.TransporterInventoryTransfer;
import com.hbmspace.tileentity.machine.TileEntityMachineDischarger;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.items.ItemStackHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FixRegressionTest {
    @BeforeAll
    static void bootstrapMinecraft() {
        Bootstrap.register();
    }

    @Test
    void dischargerStopsAdvertisingItselfWhenUnloaded() {
        TileEntityMachineDischarger discharger = new TileEntityMachineDischarger();
        assertTrue(discharger.isLoaded());
        discharger.onChunkUnload();
        assertFalse(discharger.isLoaded());
        discharger.onLoad();
        assertTrue(discharger.isLoaded());
    }

    @Test
    void transportedItemsKeepTheirNbtWhenStacking() {
        ItemStack tagged = new ItemStack(Items.DIAMOND, 3);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("payload", "keep-me");
        tagged.setTagCompound(tag);
        ItemStackHandler source = new ItemStackHandler(2);
        ItemStackHandler target = new ItemStackHandler(1);
        source.setStackInSlot(1, tagged);
        ItemStack existing = tagged.copy();
        existing.setCount(1);
        target.setStackInSlot(0, existing);
        assertTrue(TransporterInventoryTransfer.pushOne(source, 1, 2, target));
        assertEquals(2, source.getStackInSlot(1).getCount());
        assertEquals(2, target.getStackInSlot(0).getCount());
        assertEquals("keep-me", target.getStackInSlot(0).getTagCompound().getString("payload"));
    }

    @Test
    void rejectedInsertionDoesNotConsumeCargo() {
        ItemStackHandler source = new ItemStackHandler(1);
        ItemStackHandler target = new ItemStackHandler(1) {
            @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }
        };
        source.setStackInSlot(0, new ItemStack(Items.DIAMOND, 3));
        assertFalse(TransporterInventoryTransfer.pushOne(source, 0, 1, target));
        assertEquals(3, source.getStackInSlot(0).getCount());
        assertTrue(target.getStackInSlot(0).isEmpty());
    }

    @Test
    void pullOnlyFillsInputSlots() {
        ItemStackHandler source = new ItemStackHandler(1);
        source.setStackInSlot(0, new ItemStack(Items.DIAMOND, 3));
        ItemStackHandler target = new ItemStackHandler(2);
        target.setStackInSlot(0, new ItemStack(Items.APPLE, 64));
        assertFalse(TransporterInventoryTransfer.pullOne(source, target, 1));
        assertEquals(3, source.getStackInSlot(0).getCount());
        assertTrue(target.getStackInSlot(1).isEmpty());
        target.setStackInSlot(0, ItemStack.EMPTY);
        assertTrue(TransporterInventoryTransfer.pullOne(source, target, 1));
        assertEquals(2, source.getStackInSlot(0).getCount());
        assertEquals(1, target.getStackInSlot(0).getCount());
    }

    @Test
    void spawnRegistrationPersistsAcrossLookups() {
        Biome biome = new BiomeGenBaseCelestial(new Biome.BiomeProperties("fix-test")) {};
        List<Biome.SpawnListEntry> spawns = biome.getSpawnableList(EnumCreatureType.CREATURE);
        assertTrue(spawns.isEmpty(), "Space biomes must not inherit Earth animals");
        Biome.SpawnListEntry spawn = new Biome.SpawnListEntry(EntityPig.class, 1, 1, 1);
        spawns.add(spawn);
        assertTrue(biome.getSpawnableList(EnumCreatureType.CREATURE).contains(spawn));
        biome.getSpawnableList(EnumCreatureType.CREATURE).remove(spawn);
        assertTrue(biome.getSpawnableList(EnumCreatureType.CREATURE).isEmpty());
    }

    @Test
    void firstJoinFlagSurvivesSerialization() {
        HbmLivingCapabilitySpace.EntityHbmProps original = new HbmLivingCapabilitySpace.EntityHbmProps();
        original.setWarped(true);
        NBTTagCompound tag = new NBTTagCompound();
        original.saveNBTData(tag);
        HbmLivingCapabilitySpace.EntityHbmProps restored = new HbmLivingCapabilitySpace.EntityHbmProps();
        restored.loadNBTData(tag);
        assertTrue(restored.hasWarped());
    }

    @Test
    void sunsetTextureIsARealTextureResource() {
        CelestialBody body = new CelestialBody("fix-test-texture").withBlockTextures(
                "hbm:blocks/minmus_stone", "hbm:blocks/minmus_regolith");
        assertEquals("hbm:textures/blocks/minmus_regolith.png", body.surfaceTexture.toString());
        assertNotNull(getClass().getResource("/assets/hbm/" + body.surfaceTexture.getPath()));
    }

    @Test
    void earlyMixinPackageDoesNotOwnLateAccessors() throws Exception {
        JsonObject early = readConfig("hbmspace.default.mixin.json");
        JsonObject late = readConfig("hbmspace.mod.mixin.json");
        String earlyPackage = early.get("package").getAsString();
        String latePackage = late.get("package").getAsString();
        assertFalse(latePackage.startsWith(earlyPackage + "."));
        assertFalse(earlyPackage.startsWith(latePackage + "."));
        assertTrue(late.getAsJsonArray("mixins").toString().contains("hbm.MixinItemRTGPelletAccessor"));
        verifyListedClasses(early);
        verifyListedClasses(late);
    }

    private JsonObject readConfig(String name) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/" + name)) {
            assertNotNull(stream, name);
            return new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    private void verifyListedClasses(JsonObject config) {
        String prefix = "/" + config.get("package").getAsString().replace('.', '/') + "/";
        for (String side : new String[] {"mixins", "client", "server"}) {
            JsonArray entries = config.getAsJsonArray(side);
            entries.forEach(entry -> assertNotNull(getClass().getResource(
                    prefix + entry.getAsString().replace('.', '/') + ".class"), entry.getAsString()));
        }
    }
}
