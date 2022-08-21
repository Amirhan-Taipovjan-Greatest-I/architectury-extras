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

package dev.architectury.transfer.wrapper.combined;

import dev.architectury.transfer.ResourceView;
import dev.architectury.transfer.TransferHandler;
import dev.architectury.transfer.wrapper.single.SingleTransferHandler;

import java.util.Iterator;
import java.util.List;

/**
 * A {@link TransferHandler} that combines multiple {@link SingleTransferHandler}s.<br>
 * This is faster than using {@link CombinedTransferHandler} directly, as the size of
 * each {@link SingleTransferHandler} is known in advance.
 *
 * @param <T> the type of resource
 */
public interface CombinedSingleTransferHandler<T, P extends SingleTransferHandler<T>> extends CombinedTransferHandler<T> {
    @Override
    default Iterable<TransferHandler<T>> getHandlers() {
        return (Iterable<TransferHandler<T>>) (Iterable<? super P>) getContents();
    }
    
    List<P> getContents();
    
    @Override
    default int size() {
        return getContents().size();
    }
    
    @Override
    default P get(int index) {
        return getContents().get(index);
    }
    
    @Override
    default Iterator<ResourceView<T>> iterator() {
        return (Iterator<ResourceView<T>>) (Iterator<? extends ResourceView<T>>) getContents().iterator();
    }
}
