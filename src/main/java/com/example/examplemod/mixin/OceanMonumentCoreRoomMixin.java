package com.example.examplemod.mixin;

import com.example.examplemod.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Extends StructurePiece (an ancestor of the target) so the protected getWorldPos helper is callable
@Mixin(OceanMonumentPieces.OceanMonumentCoreRoom.class)
public abstract class OceanMonumentCoreRoomMixin extends StructurePiece {

    private OceanMonumentCoreRoomMixin(StructurePieceType type, int genDepth, BoundingBox boundingBox) {
        super(type, genDepth, boundingBox);
    }

    @Inject(method = "postProcess", at = @At("TAIL"))
    private void addBrineSpawner(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                                 RandomSource random, BoundingBox chunkBB, ChunkPos chunkPos, BlockPos referencePos,
                                 CallbackInfo ci) {
        // The gold vault (local 6..9, 3..6, 6..9) sits on pillars; hang the spawner in the gap right beneath it.
        // The room is built one chunk at a time, so only the chunk containing the spot places it (same as the fortress blaze spawner).
        BlockPos pos = this.getWorldPos(7, 2, 7);
        if (chunkBB.isInside(pos)) {
            level.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 2);
            if (level.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner) {
                spawner.setEntityId(ModEntities.BRINE.get(), random);
            }
        }
    }
}
