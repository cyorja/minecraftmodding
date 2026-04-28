package com.example.examplemod;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "examplemod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GrowthEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 1. Safety check: Run only on server and ensure there is an attacker
        Level world = event.getEntity().level();
        if (world.isClientSide()) return;
        if (event.getSource().getEntity() == null) return;

        // 2. Check if the attacker is a Player
        if (event.getSource().getEntity() instanceof Player) {
            LivingEntity target = event.getEntity();

            // 3. Access the SCALE attribute (Requires Minecraft 1.20.5+)
            AttributeInstance scaleAttribute = target.getAttribute(Attributes.SCALE);

            if (scaleAttribute != null) {
                double currentScale = scaleAttribute.getBaseValue();
                double maxScale = 5.0;
                double growthAmount = 0.2;

                // 4. Apply growth if under the cap
                if (currentScale < maxScale) {
                    double nextScale = Math.min(currentScale + growthAmount, maxScale);
                    scaleAttribute.setBaseValue(nextScale);

                    // Optional: Make the mob heal as it grows so it doesn't die from the hits
                    target.setHealth(target.getMaxHealth());
                }
            }
        }
    }
}