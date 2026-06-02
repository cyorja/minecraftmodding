package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    private static final Map<UUID, BlockPos> lastPositions = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        Player player = event.player();
        Level level = player.level();
        if (level.isClientSide()) return;

        BlockPos feetPos = player.blockPosition();
        BlockState stateAtFeet = level.getBlockState(feetPos);

        boolean holdingTorch = player.getMainHandItem().is(Items.TORCH)
                || player.getOffhandItem().is(Items.TORCH);

        if (holdingTorch) {
            // If the player has moved to a new block, clean up the light at the old position
            BlockPos lastPos = lastPositions.get(player.getUUID());
            if (lastPos != null && !lastPos.equals(feetPos)) {
                BlockState stateAtLast = level.getBlockState(lastPos);
                if (stateAtLast.is(ModBlocks.FAKE_LIGHT.get())) {
                    level.setBlock(lastPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }

            // Place the light at the current position if it is air or already our light
            if (stateAtFeet.isAir() || stateAtFeet.is(ModBlocks.FAKE_LIGHT.get())) {
                level.setBlock(feetPos, ModBlocks.FAKE_LIGHT.get().defaultBlockState(), 3);
            }

            // Update the stored position
            lastPositions.put(player.getUUID(), feetPos);
        } else {
            // Clean up the light at the current position if the player stopped holding a torch
            if (stateAtFeet.is(ModBlocks.FAKE_LIGHT.get())) {
                level.setBlock(feetPos, Blocks.AIR.defaultBlockState(), 3);
            }
            // Remove the player from the map since they are no longer holding a torch
            lastPositions.remove(player.getUUID());
        }
    }
}
