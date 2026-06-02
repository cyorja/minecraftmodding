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
        BlockPos headPos = feetPos.above();

        boolean holdingTorch = player.getMainHandItem().is(Items.TORCH)
                || player.getOffhandItem().is(Items.TORCH);

        if (holdingTorch) {
            // Prefer head position, fall back to feet if head is blocked
            BlockState stateAtHead = level.getBlockState(headPos);
            boolean headFree = stateAtHead.isAir() || stateAtHead.is(ModBlocks.FAKE_LIGHT.get());
            BlockPos targetPos = headFree ? headPos : feetPos;

            BlockPos lastPos = lastPositions.get(player.getUUID());

            // If the target has changed, clean up the light at the old position
            if (lastPos != null && !lastPos.equals(targetPos)) {
                BlockState stateAtLast = level.getBlockState(lastPos);
                if (stateAtLast.is(ModBlocks.FAKE_LIGHT.get())) {
                    level.setBlock(lastPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }

            // Place the light at the target position
            BlockState stateAtTarget = level.getBlockState(targetPos);
            if (stateAtTarget.isAir() || stateAtTarget.is(ModBlocks.FAKE_LIGHT.get())) {
                level.setBlock(targetPos, ModBlocks.FAKE_LIGHT.get().defaultBlockState(), 3);
            }

            // Store where we actually placed the light
            lastPositions.put(player.getUUID(), targetPos);
        } else {
            // Use the stored position to clean up wherever the light actually is
            BlockPos lastPos = lastPositions.get(player.getUUID());
            if (lastPos != null) {
                BlockState stateAtLast = level.getBlockState(lastPos);
                if (stateAtLast.is(ModBlocks.FAKE_LIGHT.get())) {
                    level.setBlock(lastPos, Blocks.AIR.defaultBlockState(), 3);
                }
                lastPositions.remove(player.getUUID());
            }
        }
    }
}
