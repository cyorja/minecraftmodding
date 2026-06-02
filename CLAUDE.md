# Minecraft Modding Project

## Environment

- **Minecraft:** 1.26.1
- **Forge:** 63.0.0 (`net.minecraftforge:forge:26.1.1-63.0.0`)
- **ForgeGradle:** 7.0.17+
- **Java:** 25

## Before Writing Any Code

Use Context7 to look up the current Forge/Minecraft documentation before writing or modifying mod code:

```
use context7
```

Look up the relevant API in the official Forge docs for Forge 63 / Minecraft 1.26. Do not rely on training data for API details — always verify against current docs.

## Mandatory Patterns (Forge 63 / 1.26)

- Use `DeferredRegister` with `RegisterEvent` or the new holder-based registration APIs.
- Use `IEventBus` obtained from `FMLJavaModLoadingContext` (or mod constructor injection) for registering to the mod event bus.
- Use `@Mod` and constructor-based event bus subscription.
- Use data-driven features (loot tables, recipes, tags, advancements) via datagen where possible.

## Forbidden Patterns (Pre-1.20 / Deprecated)

Do NOT use any of the following deprecated or removed patterns:

- `GameRegistry.register*` — removed; use `DeferredRegister`
- `@ObjectHolder` for registration — deprecated; use `DeferredRegister` holders
- `ForgeRegistries.*` direct `.register()` calls outside `DeferredRegister`
- `FMLCommonSetupEvent` for registry work — registries must be populated before setup
- `IForgeRegistry.register()` called directly from event handlers
- `MinecraftForge.EVENT_BUS` for mod lifecycle events — those belong on the mod event bus
- `@SubscribeEvent` on static methods in non-`@EventBusSubscriber` classes without explicit bus registration
- `@Mod.EventBusSubscriber` without specifying `bus = Bus.MOD` or `bus = Bus.FORGE` explicitly
- Old `IRecipe`/`IRecipeType` interfaces replaced in 1.20+
- `Block.Properties.of(Material.*)` — `Material` was removed; use `BlockBehaviour.Properties` with `MapColor` or the new property builders
- `AbstractBlock.Properties` created without the modern factory methods
- Any renderer or model registration via old `ModelLoader` APIs removed in 1.20+
