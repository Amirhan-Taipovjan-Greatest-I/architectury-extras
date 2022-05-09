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

package dev.architectury.transfer.fluid;

import dev.architectury.fluid.FluidStack;
import dev.architectury.transfer.TransferHandler;
import dev.architectury.transfer.view.VariantView;

/**
 * This is a convenience class that implements methods for {@link FluidStack}s.
 */
public interface FluidTransferHandler extends FluidTransferView, TransferHandler<FluidStack>, VariantView<FluidStack> {
    /**
     * Returns an empty fluid transfer handler, which does nothing.
     *
     * @return an empty fluid transfer handler
     */
    static TransferHandler<FluidStack> empty() {
        return EmptyFluidTransferHandler.INSTANCE;
    }
    
    @Override
    default long getAmount(FluidStack resource) {
        return resource.getAmount();
    }
    
    @Override
    default boolean isSameVariant(FluidStack first, FluidStack second) {
        return first.isFluidEqual(second) && first.isTagEqual(second);
    }
}
