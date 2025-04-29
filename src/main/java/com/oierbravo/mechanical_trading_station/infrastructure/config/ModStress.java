package com.oierbravo.mechanical_trading_station.infrastructure.config;

import com.oierbravo.mechanical_trading_station.ModConstants;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

public class ModStress extends ConfigBase {
	// bump this version to reset configured values.
	private static final int VERSION = 1;

	// IDs need to be used since configs load before registration

	private static final Object2DoubleMap<ResourceLocation> DEFAULT_IMPACTS = new Object2DoubleOpenHashMap<>();
	private static final Object2DoubleMap<ResourceLocation> DEFAULT_CAPACITIES = new Object2DoubleOpenHashMap<>();

	protected final Map<ResourceLocation, ConfigValue<Double>> capacities = new HashMap<>();
	protected final Map<ResourceLocation, ConfigValue<Double>> impacts = new HashMap<>();

	@Override
	public void registerAll(Builder builder) {
		builder.comment(".", Comments.su, Comments.impact)
			.push("impact");
		DEFAULT_IMPACTS.forEach((id, value) -> this.impacts.put(id, builder.define(id.getPath(), value)));
		builder.pop();

		builder.comment(".", Comments.su, Comments.capacity)
			.push("capacity");
		DEFAULT_CAPACITIES.forEach((id, value) -> this.capacities.put(id, builder.define(id.getPath(), value)));
		builder.pop();
	}

	@Override
	public String getName() {
		return "stressValues.v" + VERSION;
	}

	@Nullable
	public DoubleSupplier getImpact(Block block) {
		ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(block);
		ConfigValue<Double> value = this.impacts.get(id);
		return value == null ? null : value::get;
	}

	@Nullable
	public DoubleSupplier getCapacity(Block block) {
		ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(block);
		ConfigValue<Double> value = this.capacities.get(id);
		return value == null ? null : value::get;
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setNoImpact() {
		return setImpact(0);
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double value) {
		return builder -> {
			assertFromThisMod(builder);
			ResourceLocation id = ModConstants.asResource(builder.getName());
			DEFAULT_IMPACTS.put(id, value);
			return builder;
		};
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setCapacity(double value) {
		return builder -> {
			assertFromThisMod(builder);
			ResourceLocation id = ModConstants.asResource(builder.getName());
			DEFAULT_CAPACITIES.put(id, value);
			return builder;
		};
	}
	private static void assertFromThisMod(BlockBuilder<?, ?> builder) {
		if (!builder.getOwner().getModid().equals(ModConstants.MODID)) {
			throw new IllegalStateException("Non-Create blocks cannot be added to " + ModConstants.DISPLAY_NAME + "'s config.");
		}
	}

	private static class Comments {
		static String su = "[in Stress Units]";
		static String impact =
			"Configure the individual stress impact of mechanical blocks. Note that this cost is doubled for every speed increase it receives.";
		static String capacity = "Configure how much stress a source can accommodate for.";
	}

}
