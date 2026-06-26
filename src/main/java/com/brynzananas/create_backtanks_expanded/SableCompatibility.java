package com.brynzananas.create_backtanks_expanded;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SableCompatibility {
    public static List<BlockPos> GetNearbySolidBlocks(LivingEntity entity, int radiusX, int radiusZ, int radiusY) {
        List<BlockPos> solidBlocks = new ArrayList<>();
        Level level = entity.level();

        if (level == null) return solidBlocks;

        BlockPos centerPos = entity.blockPosition();
        final BoundingBox3d globalBounds = new BoundingBox3d(centerPos.getX() - radiusX, centerPos.getY() - radiusY, centerPos.getZ() - radiusZ, centerPos.getX() + radiusX, centerPos.getY() + radiusY, centerPos.getZ() + radiusZ);
        final Iterable<SubLevel> subLevels = Sable.HELPER.getAllIntersecting(level, globalBounds);

        for (final SubLevel subLevel : subLevels) {
            final Pose3d pose = subLevel.logicalPose();
            Vec3 vec3 = pose.transformPositionInverse(centerPos.getBottomCenter());
            BlockPos centerPos2 = new BlockPos(new Vec3i((int)vec3.x, (int)vec3.y, (int)vec3.z));
            BlockPos minPos2 = centerPos2.offset(-radiusX, -radiusY, -radiusZ);
            BlockPos maxPos2 = centerPos2.offset(radiusX, radiusY, radiusZ);
            for (BlockPos pos : BlockPos.betweenClosed(minPos2, maxPos2)) {
                BlockState state = level.getBlockState(pos);
                if (state.isSolid()) {
                    solidBlocks.add(pos.immutable());
                }
            }
        }

        return solidBlocks;
    }
}
