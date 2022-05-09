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

package dev.architectury.transfer.item;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.transfer.TransferHandler;
import dev.architectury.transfer.access.BlockLookupAccess;
import dev.architectury.transfer.access.PlatformLookup;
import dev.architectury.transfer.item.wrapper.ContainerTransferHandler;
import dev.architectury.transfer.item.wrapper.WorldlyContainerTransferHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemTransfer {
    /**
     * A lookup access for item transfer handlers, the direction context is
     * only required on fabric.
     * <p>
     * This is the equivalent to getting the item transfer handler for a
     * block entity on Forge, or the storage with the lookup api on Fabric.
     * <p>
     * There are performance implications for using the architectury lookups,
     * please keep your implementations as simple as possible.
     */
    public static final BlockLookupAccess<TransferHandler<ItemStack>, Direction> BLOCK = BlockLookupAccess.create();
    
    static {
        PlatformLookup.attachBlock(BLOCK, platformBlockLookup(), ItemTransfer::wrap, (handler, direction) -> unwrap(handler));
    }
    
    @ExpectPlatform
    private static Object platformBlockLookup() {
        throw new AssertionError();
    }
    
    /**
     * Wraps a platform-specific item transfer handler into the architectury transfer handler.<br><br>
     * This accepts {@code IItemHandler} on Forge.<br>
     * This accepts {@code Storage<ItemVariant>} on Fabric.
     *
     * @param object the handler to wrap
     * @return the wrapped handler, or {@code null} if {@code object} is null
     * @throws IllegalArgumentException if {@code object} is not a supported handler
     */
    @ExpectPlatform
    @Nullable
    public static TransferHandler<ItemStack> wrap(@Nullable Object object) {
        throw new AssertionError();
    }
    
    public static TransferHandler<ItemStack> container(Container container, @Nullable Direction direction) {
        if (container instanceof WorldlyContainer && direction != null) {
            return new WorldlyContainerTransferHandler<>((WorldlyContainer) container, direction);
        }
        
        return ContainerTransferHandler.of(container);
    }
    
    public static TransferHandler<ItemStack> container(Container container) {
        return container(container, null);
    }
    
    @ExpectPlatform
    @Nullable
    public static Object unwrap(@Nullable TransferHandler<ItemStack> handler) {
        throw new AssertionError();
    }
}
