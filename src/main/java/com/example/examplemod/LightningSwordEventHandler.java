package com.example.examplemod;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "examplemod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LightningSwordEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player)) return;
        Player player = (Player)event.getSource().getEntity();
        Level world = player.level();
        if (world.isClientSide()) return;
        if (!(world instanceof ServerLevel serverLevel)) return;
        if (!(player.getMainHandItem().is(ItemTags.SWORDS))) return;

        LivingEntity target = event.getEntity();

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.spawn(serverLevel, null, null,
                target.blockPosition(), EntitySpawnReason.TRIGGERED, true, true);
    }
}