package dev.mariany.copperworks.sound;

import dev.mariany.copperworks.Copperworks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSoundEvents {
    public static final SoundEvent DRILL = register("drill");
    public static final SoundEvent CHARGE = register("charge");
    public static final SoundEvent DONE_CHARGING = register("done_charging");
    public static final SoundEvent OUT_OF_CHARGE = register("out_of_charge");
    public static final SoundEvent RADIO_USED = register("radio_used");
    public static final SoundEvent RADIO_FAIL = register("radio_fail");

    private static SoundEvent register(String name) {
        Identifier id = Copperworks.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerModSoundEvents() {
        Copperworks.LOGGER.info("Registering Mod Sound Events for " + Copperworks.MOD_ID);
    }
}
