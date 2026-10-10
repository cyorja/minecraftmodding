package com.example.examplemod;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModGameRules {
    public static final DeferredRegister<GameRule<?>> GAME_RULES =
            DeferredRegister.create(Registries.GAME_RULE, ExampleMod.MODID);

    public static final RegistryObject<GameRule<Boolean>> LIGHTNING_SWORD = registerToggle("lightning_sword");
    public static final RegistryObject<GameRule<Boolean>> LIGHTNING_ZOMBIE = registerToggle("lightning_zombie");
    public static final RegistryObject<GameRule<Boolean>> GROWTH_ON_HIT = registerToggle("growth_on_hit");

    private static RegistryObject<GameRule<Boolean>> registerToggle(String id) {
        return GAME_RULES.register(id, () -> new GameRule<Boolean>(
                GameRuleCategory.MOBS,
                GameRuleType.BOOL,
                BoolArgumentType.bool(),
                GameRuleTypeVisitor::visitBoolean,
                Codec.BOOL,
                enabled -> enabled ? 1 : 0,
                false,
                FeatureFlagSet.of()));
    }

    public static void init(BusGroup modBus) {
        GAME_RULES.register(modBus);
    }
}
