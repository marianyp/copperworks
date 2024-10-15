package dev.mariany.copperworks.api.interaction;

import net.minecraft.sound.SoundEvent;

import java.util.Optional;

public record InteractionSound(Optional<SoundEvent> sound, float volume, float pitch) {
}
