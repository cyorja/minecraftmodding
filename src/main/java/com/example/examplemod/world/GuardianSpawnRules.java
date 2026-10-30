package com.example.examplemod.world;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

/**
 * Replacement spawn rule for regular guardians (elder guardians keep vanilla's rule).
 * Vanilla only lets guardians spawn where the sky is hidden (plus a 1-in-20 exception);
 * this flips that so they only spawn in open water under the sky, and also thins them out.
 * <p>
 * Registered with {@code Operation.REPLACE}, which discards any AND predicates, so the
 * spawn chance and crowd cap live here rather than in a separate check.
 */
public final class GuardianSpawnRules {
    // 1-in-SPAWN_CHANCE rolls succeed, and no more than MAX_NEARBY guardians within NEARBY_RADIUS blocks
    private static final int SPAWN_CHANCE = 2;
    private static final int MAX_NEARBY = 4;
    private static final double NEARBY_RADIUS = 24.0;

    private GuardianSpawnRules() {}

    public static boolean check(EntityType<Guardian> type, LevelAccessor level,
                                EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        if (level.getDifficulty() == Difficulty.PEACEFUL || !level.getFluidState(pos.below()).is(FluidTags.WATER)) return false;
        if (EntitySpawnReason.isSpawner(reason)) return true;

        // Exact type match so elder guardians don't count toward the cap
        return level.getFluidState(pos).is(FluidTags.WATER)
                && level.canSeeSkyFromBelowWater(pos)
                && random.nextInt(SPAWN_CHANCE) == 0
                && level.getEntities(type, new AABB(pos).inflate(NEARBY_RADIUS), e -> true).size() < MAX_NEARBY;
    }
}
