/*
 * This file is part of architectury.
 * Copyright (C) 2020, 2021, 2022 architectury
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package dev.architectury.transfer.access;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface BlockLookup<T, Context> {
    static <T, Context> BlockLookup<T, Context> of(Simple<T, Context> simple) {
        return new BlockLookup<T, Context>() {
            @Override
            @Nullable
            public T get(Level level, BlockPos pos, Context context) {
                BlockState state = level.getBlockState(pos);
                BlockEntity blockEntity = null;
                if (state.hasBlockEntity()) {
                    blockEntity = level.getBlockEntity(pos);
                }
                return get(level, pos, state, blockEntity, context);
            }
            
            @Override
            @Nullable
            public T get(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Context context) {
                return simple.get(level, pos, state, blockEntity, context);
            }
        };
    }
    
    /**
     * Queries the api for the given block.
     * If you need the block state or block entity, you must query it yourself,
     * as this method will not do it for you.
     *
     * @param level   the level
     * @param pos     the position of the block
     * @param context the context
     * @return the transfer handler, or null if none was found
     */
    @Nullable
    T get(Level level, BlockPos pos, Context context);
    
    /**
     * Queries the api for the given block.
     *
     * @param level       the level
     * @param pos         the position of the block
     * @param state       the state of the block
     * @param blockEntity the block entity, or null if none
     * @param context     the context
     * @return the transfer handler, or null if none was found
     */
    @Nullable
    T get(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Context context);
    
    /**
     * Queries the api for the given block entity.
     *
     * @param blockEntity the block entity
     * @param context     the context
     * @return the transfer handler, or null if none was found
     */
    @Nullable
    default T get(@Nullable BlockEntity blockEntity, Context context) {
        if (blockEntity == null) return null;
        return get(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, context);
    }
    
    interface Simple<T, Context> {
        /**
         * Queries the api for the given block.
         *
         * @param level       the level
         * @param pos         the position of the block
         * @param state       the state of the block
         * @param blockEntity the block entity, or null if none
         * @param context     the context
         * @return the transfer handler, or null if none was found
         */
        @Nullable
        T get(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Context context);
    }
}