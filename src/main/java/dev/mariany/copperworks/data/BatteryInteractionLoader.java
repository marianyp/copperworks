package dev.mariany.copperworks.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import dev.mariany.copperworks.api.interaction.BatteryInteractionType;
import dev.mariany.copperworks.api.interaction.InteractionSound;
import dev.mariany.copperworks.api.registry.BatteryInteractionRegistry;
import dev.mariany.copperworks.api.registry.BatteryInteractionTypeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BatteryInteractionLoader implements SimpleSynchronousResourceReloadListener {
    private static final String RESOURCE_ID = "battery_interactions";

    @Override
    public Identifier getFabricId() {
        return Copperworks.id(RESOURCE_ID);
    }

    @Override
    public void reload(ResourceManager manager) {
        BatteryInteractionRegistry.clearInteractions();
        Map<RegistryKey<Block>, AbstractBatteryInteraction> replaceInteractions = new HashMap<>();

        Set<Identifier> resourceFiles = findResourceFiles(manager);

        for (Identifier id : resourceFiles) {
            try {
                Optional<Resource> optionalResource = manager.getResource(id);
                if (optionalResource.isPresent()) {
                    JsonObject json = parseJsonConfig(optionalResource.get());
                    if (json != null) {
                        processInteraction(id, json, replaceInteractions);
                    }
                }
            } catch (Exception e) {
                Copperworks.LOGGER.error("Error loading interaction from {}", id.toString(), e);
            }
        }
        applyReplaceInteractions(replaceInteractions);
    }

    private Set<Identifier> findResourceFiles(ResourceManager manager) {
        return manager.findResources(RESOURCE_ID, path -> path.getPath().endsWith(".json")).keySet();
    }

    private JsonObject parseJsonConfig(Resource resource) throws IOException {
        return JsonParser.parseReader(new InputStreamReader(resource.getInputStream())).getAsJsonObject();
    }

    private void processInteraction(Identifier id, JsonObject json,
                                    Map<RegistryKey<Block>, AbstractBatteryInteraction> replaceInteractions) {
        boolean replace = json.has("replace") && json.get("replace").getAsBoolean();
        String type = extractType(json, id);

        if (type == null) {
            return;
        }

        InteractionSound interactionSound = extractSound(json);

        Identifier interactionTypeId = Identifier.tryParse(type);
        BatteryInteractionType interactionType = BatteryInteractionTypeRegistry.getInteractionType(interactionTypeId);
        if (interactionType == null) {
            Copperworks.LOGGER.warn("Unknown interaction type: {}", interactionTypeId);
            return;
        }

        Optional<RegistryKey<Block>> optionalBlockRegistryKey = getBlockRegistryKey(id);

        if (optionalBlockRegistryKey.isEmpty()) {
            return;
        }

        RegistryKey<Block> blockRegistryKey = optionalBlockRegistryKey.get();

        AbstractBatteryInteraction interaction = interactionType.createInteraction(
                Registries.BLOCK.get(blockRegistryKey), interactionSound, json);

        BatteryInteractionRegistry.registerInteraction(blockRegistryKey, interaction);
        if (replace) {
            replaceInteractions.put(blockRegistryKey, interaction);
        }
    }

    private String extractType(JsonObject json, Identifier id) {
        if (!json.has("type")) {
            Copperworks.LOGGER.warn("Interaction type must be specified for interaction {}", id);
            return null;
        }
        String type = json.get("type").getAsString();
        return type.contains(":") ? type : Copperworks.MOD_ID + ":" + type;
    }

    private InteractionSound extractSound(JsonObject json) {
        String sound = "";
        float volume = 0.5F;
        float pitch = 1F;

        if (json.has("sound") && json.get("sound").isJsonObject()) {
            JsonObject soundObject = json.getAsJsonObject("sound");

            if (soundObject.has("id")) {
                sound = soundObject.get("id").getAsString();
            }
            if (soundObject.has("volume")) {
                volume = soundObject.get("volume").getAsFloat();
            }
            if (soundObject.has("pitch")) {
                pitch = soundObject.get("pitch").getAsFloat();
            }
        }

        if (!sound.isBlank() && !sound.contains(":")) {
            sound = Copperworks.MOD_ID + ":" + sound;
        }

        Identifier soundId = Identifier.tryParse(sound);
        Optional<SoundEvent> soundEvent = Registries.SOUND_EVENT.getOrEmpty(soundId);

        return new InteractionSound(soundEvent, volume, pitch);
    }

    private Optional<RegistryKey<Block>> getBlockRegistryKey(Identifier id) {
        Identifier blockId = Identifier.of(id.getNamespace(),
                id.getPath().replace(RESOURCE_ID + "/", "").replace(".json", ""));
        Optional<Block> blockOptional = Registries.BLOCK.getOrEmpty(blockId);
        if (blockOptional.isEmpty()) {
            Copperworks.LOGGER.warn("Block with id ({}) not found for interaction", blockId);
            return Optional.empty();
        }
        return Registries.BLOCK.getKey(blockOptional.get());
    }

    private void applyReplaceInteractions(Map<RegistryKey<Block>, AbstractBatteryInteraction> replaceInteractions) {
        for (Map.Entry<RegistryKey<Block>, AbstractBatteryInteraction> entry : replaceInteractions.entrySet()) {
            BatteryInteractionRegistry.registerInteraction(entry.getKey(), entry.getValue());
        }
    }

    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new BatteryInteractionLoader());
    }
}