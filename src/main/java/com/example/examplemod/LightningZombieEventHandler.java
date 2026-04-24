package com.example.examplemod;

import net.minecraft.world.damagesource.DamageTypes; // Needed to prevent recursion
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "examplemod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LightningZombieEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 1. Get the world and ensure we are on the server
        Level world = event.getEntity().level();
        if (world.isClientSide() || !(world instanceof ServerLevel serverLevel)) return;

        // 2. SAFETY: If the damage is already lightning, stop here
        // This prevents the lightning from triggering more lightning
        if (event.getSource().is(DamageTypes.LIGHTNING_BOLT)) return;

        // 3. Check if the attacker is a Zombie
        if (event.getSource().getEntity() instanceof Zombie) {
            LivingEntity target = event.getEntity();

            // 4. Spawn the lightning bolt at the target
            EntityType.LIGHTNING_BOLT.spawn(serverLevel,
                    null,
                    null,
                    target.blockPosition(),
                    EntitySpawnReason.TRIGGERED,
                    true,
                    true
            );
        }
    }
}
