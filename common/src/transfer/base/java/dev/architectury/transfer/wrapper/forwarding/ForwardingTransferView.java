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

package dev.architectury.transfer.wrapper.forwarding;

import dev.architectury.transfer.TransferAction;
import dev.architectury.transfer.TransferView;

import java.util.function.Predicate;

public interface ForwardingTransferView<T> extends TransferView<T> {
    TransferView<T> forwardingTo();
    
    @Override
    default long insert(T toInsert, TransferAction action) {
        return forwardingTo().insert(toInsert, action);
    }
    
    @Override
    default T extract(Predicate<T> toExtract, long maxAmount, TransferAction action) {
        return forwardingTo().extract(toExtract, maxAmount, action);
    }
    
    @Override
    default T extract(T toExtract, TransferAction action) {
        return forwardingTo().extract(toExtract, action);
    }
    
    @Override
    default T blank() {
        return forwardingTo().blank();
    }
    
    @Override
    default T copyWithAmount(T resource, long amount) {
        return forwardingTo().copyWithAmount(resource, amount);
    }
    
    @Override
    default Object saveState() {
        return forwardingTo().saveState();
    }
    
    @Override
    default void loadState(Object state) {
        forwardingTo().loadState(state);
    }
}
