# Piranha Mob — Implementation Plan

Handoff doc for continuing this work in a new Claude Code session. Read this in full before writing code.

## Goal

Add a **piranha** mob to the existing `examplemod`. Attack behavior modeled after axolotls (hunts other fish), with the **addition that it also targets players**.

## Status

**No code written yet.** The plan was agreed on but the first `Write` attempt was interrupted before any file landed. The existing project is unchanged. Start fresh from the design below.

## Environment (from CLAUDE.md)

- Minecraft 1.26.1
- Forge 63.0.0 (`net.minecraftforge:forge:26.1.1-63.0.0`)
- Java 25
- Modid: `examplemod`, package `com.example.examplemod`

**Before writing code, use Context7** to verify Forge 63 / MC 1.26 APIs for:
- `EntityType.Builder.build(...)` signature (string vs ResourceKey)
- `DeferredSpawnEggItem` vs `ForgeSpawnEggItem` (the modern name in Forge 63)
- `EntityAttributeCreationEvent` access pattern via the new `BusGroup` API
- `EntityRenderersEvent.RegisterRenderers` registration in Forge 63
- Whether `Item.Properties().setId(...)` is still required (this project uses it — confirm)

The prior session did **not** have Context7 available and worked from 1.20–1.21 knowledge. Verify before writing.

## Design Decisions

### Base class: extend `Salmon`, NOT `Axolotl`

Initial draft extended `Axolotl`. User correctly pointed out **piranhas are fish, not amphibians**. Extending `Salmon` (→ `AbstractSchoolingFish` → `AbstractFish` → `WaterAnimal`) gives:

- Water-only navigation (`WaterBoundPathNavigation`) — can't chase player onto land. Realistic.
- Suffocates out of water (inherited from `AbstractFish`).
- Schooling behavior — fits real piranha shoaling.
- Salmon body shape, so we can reuse `SalmonRenderer` for v1.

We lose Axolotl's hunt-fish AI by switching base classes, so we re-add it explicitly via target goals.

### AI goals

`Salmon.registerGoals()` gives swimming, panic, flock-following. We add on top:

- `MeleeAttackGoal(this, 1.4D, true)` — actually bites the target. Damage comes from `ATTACK_DAMAGE` attribute. The `true` flag means it keeps chasing even without line of sight (zombie-style aggression).
- `NearestAttackableTargetGoal<Player>` at priority 1 — players are valid targets.
- `NearestAttackableTargetGoal<AbstractFish>` at priority 2, excluding other piranhas — replicates the axolotl-hunts-fish behavior.

**Why explicit `MeleeAttackGoal`:** Axolotl damages prey via hardcoded touch-collision inside its hunt logic, not a generic melee goal. Since we're not extending Axolotl, we need a real goal that calls `mob.doHurtTarget(target)`.

### Attributes

```
MAX_HEALTH:     8.0   (same as cod/salmon — small fish)
ATTACK_DAMAGE:  3.0   (less than zombie's 3.0 base — tune up if too weak)
MOVEMENT_SPEED: 1.2
```

### Renderer

Reuse `SalmonRenderer::new` for v1. Piranha renders as a salmon visually. Custom texture/model is a follow-up — the user has not asked for one yet. Spawn-egg color suggestion: primary `0x8B0000` (dark red), secondary `0xC0C0C0` (silver).

### Spawning

**Spawn egg only for v1.** No natural biome spawning. The user can `/summon examplemod:piranha` or use the egg from the existing `EXAMPLE_TAB` creative tab. Add natural spawn placement in a follow-up if requested.

## Files to Create

### `src/main/java/com/example/examplemod/entity/PiranhaEntity.java`

```java
package com.example.examplemod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PiranhaEntity extends Salmon {
    public PiranhaEntity(EntityType<? extends Salmon> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MOVEMENT_SPEED, 1.2);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.4D, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this, AbstractFish.class, 10, true, false,
                e -> !(e instanceof PiranhaEntity)));
    }
}
```

### `src/main/java/com/example/examplemod/ModEntities.java`

`DeferredRegister<EntityType<?>>` keyed to `ForgeRegistries.ENTITY_TYPES`, registers `piranha` with `MobCategory.WATER_CREATURE`, sized roughly `0.7f x 0.4f`, `clientTrackingRange(10)`. Build with `EntityType.Builder.of(PiranhaEntity::new, ...).build(...)` — confirm via Context7 whether `build` takes a String id or a `ResourceKey` in Forge 63.

## Files to Modify

### `src/main/java/com/example/examplemod/ExampleMod.java`

1. Register `ModEntities.ENTITIES` against the mod bus group (alongside the existing `BLOCKS`, `ITEMS`, `CREATIVE_MODE_TABS` registrations).
2. Wire `EntityAttributeCreationEvent` via the new bus group API:
   ```
   EntityAttributeCreationEvent.getBus(modBusGroup).addListener(event ->
       event.put(ModEntities.PIRANHA.get(), PiranhaEntity.createAttributes().build()));
   ```
3. Add a spawn egg item to the existing `ITEMS` register (use `DeferredSpawnEggItem` — confirm name in Forge 63):
   ```
   public static final RegistryObject<Item> PIRANHA_SPAWN_EGG = ITEMS.register("piranha_spawn_egg",
       () -> new DeferredSpawnEggItem(ModEntities.PIRANHA, 0x8B0000, 0xC0C0C0,
           new Item.Properties().setId(ITEMS.key("piranha_spawn_egg"))));
   ```
4. Add `PIRANHA_SPAWN_EGG` to `EXAMPLE_TAB.displayItems(...)` so it's findable in creative.
5. In `ClientModEvents.onClientSetup` (or via `EntityRenderersEvent.RegisterRenderers` on the mod bus group — preferred), register the renderer:
   ```
   event.registerEntityRenderer(ModEntities.PIRANHA.get(), SalmonRenderer::new);
   ```

## Project conventions to respect

From `CLAUDE.md` and observed in existing code:
- Use `DeferredRegister`, never `GameRegistry.register*` or direct `ForgeRegistries.X.register()`.
- `@Mod.EventBusSubscriber` must specify `bus = Bus.MOD` or `bus = Bus.FORGE` explicitly (see `ModEvents.java` for the pattern).
- Items in 1.26 require `Item.Properties().setId(ITEMS.key("..."))` — this project does it everywhere; match the pattern.
- Use `MapColor` / `BlockBehaviour.Properties` style — no `Material.*`.
- Forge 63 event bus is the new `BusGroup`-based API. Existing code uses `EVENT.getBus(modBusGroup).addListener(...)`. Follow that pattern; do not use `MinecraftForge.EVENT_BUS` for mod-lifecycle events.

## Verification steps after writing

1. `./gradlew build` — fix any API mismatches from compiler output.
2. `./gradlew runClient` — spawn a piranha via the creative tab egg in a body of water, verify:
   - It swims around.
   - It attacks a nearby player who enters the water.
   - It attacks a nearby salmon/cod, ignores other piranhas.
   - It suffocates / flops on land (not amphibian).

## Follow-ups (not in v1, mention to user)

- Custom texture + model (currently looks like a salmon).
- Natural spawning in jungle/swamp river biomes via `SpawnPlacementRegisterEvent`.
- Damage tuning based on playtesting.
- Sound events (attack, hurt, ambient).
- Maybe a "frenzy" mechanic: attack damage scales with nearby piranhas.

## Open question for the user

None blocking — design was agreed. Just confirm at start of next session: "ready to proceed with the plan in PLAN.md?" and then build it.
