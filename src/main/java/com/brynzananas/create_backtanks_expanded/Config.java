package com.brynzananas.create_backtanks_expanded;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue SPEED_UPGRADE_SPEED_MULTIPLIER;
    public static final ModConfigSpec.IntValue SPEED_UPGRADE_PRESSURIZED_AIR_REGENERATION;

    public static final ModConfigSpec.IntValue HOVER_UPGRADE_HOVER_REACH_RADIUS;
    public static final ModConfigSpec.IntValue HOVER_UPGRADE_MAX_HOVER_REACH_RADIUS;
    public static final ModConfigSpec.IntValue HOVER_UPGRADE_PRESSURIZED_AIR_REGENERATION;

    public static final ModConfigSpec.IntValue PRESSURIZED_AIR_REGENERATION_UPGRADE_PRESSURIZED_AIR_REGENERATION;

    public static final ModConfigSpec.IntValue FLUID_TANK_UPGRADE_MAX_CAPACITY;

    public static final ModConfigSpec.IntValue AUTO_DRINK_UPGRADE_POTION_CONSUME_AMOUNT;

    static {
        BUILDER.comment("Main configuration").push("create_backtanks_expanded_config");

        BUILDER.comment("Speed Upgrade").push("speed_upgrade_config");

        SPEED_UPGRADE_SPEED_MULTIPLIER = BUILDER
                .comment("Speed Multiplier")
                .defineInRange("speed_upgrade_speed_multiplier", 0.05, -Double.MAX_VALUE, Double.MAX_VALUE);

        SPEED_UPGRADE_PRESSURIZED_AIR_REGENERATION = BUILDER
                .comment("Pressurized Air Regeneration per Second")
                .defineInRange("speed_upgrade_pressurized_air_regeneration", 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.comment("Hover Upgrade").push("hover_upgrade_config");

        HOVER_UPGRADE_HOVER_REACH_RADIUS = BUILDER
                .comment("Hover Reach Radius")
                .defineInRange("hover_upgrade_hover_reach", 3, 0, Integer.MAX_VALUE);

        HOVER_UPGRADE_MAX_HOVER_REACH_RADIUS = BUILDER
                .comment("Max Hover Reach Radius. Expect heavy lag with high hover reach values")
                .defineInRange("hover_upgrade_max_hover_reach", 12, 0, Integer.MAX_VALUE);

        HOVER_UPGRADE_PRESSURIZED_AIR_REGENERATION = BUILDER
                .comment("Pressurized Air Regeneration per Second")
                .defineInRange("hover_upgrade_pressurized_air_regeneration", 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.comment("Pressurized Air Regeneration Upgrade").push("pressurized_air_regeneration_upgrade_config");

        PRESSURIZED_AIR_REGENERATION_UPGRADE_PRESSURIZED_AIR_REGENERATION = BUILDER
                .comment("Pressurized Air Regeneration per Second")
                .defineInRange("pressurized_air_regeneration_upgrade_pressurized_air_regeneration", 2, -Integer.MAX_VALUE, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.comment("Fluid Tank Upgrade").push("fluid_tank_upgrade_config");

        FLUID_TANK_UPGRADE_MAX_CAPACITY = BUILDER
                .comment("Maximum Fluid Capacity. Restart Required")
                .defineInRange("fluid_tank_upgrade_max_capacity", 1000, 0, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.comment("Auto Drink Upgrade").push("auto_drink_upgrade_config");

        AUTO_DRINK_UPGRADE_POTION_CONSUME_AMOUNT = BUILDER
                .comment("Potion Fluid Consume Amount in mB")
                .defineInRange("auto_drink_upgrade_potion_consume_amount", 250, 0, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
