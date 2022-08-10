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

package dev.architectury.transfer.wrapper.single;

import dev.architectury.transfer.TransferAction;

public interface SimpleSingleTransferHandler<T> extends SingleTransferHandler<T> {
    @Override
    default Object saveState() {
        return copy(getResource());
    }
    
    @Override
    default void loadState(Object state) {
        setResource((T) state);
    }
    
    void setResource(T resource);
    
    default T copy(T resource) {
        return copyWithAmount(resource, getAmount(resource));
    }
    
    @Override
    default long insert(T toInsert, TransferAction action) {
        T resource = getResource();
        long currentAmount = getAmount(resource);
        boolean isEmpty = currentAmount <= 0;
        if ((isEmpty || isSameVariant(resource, toInsert)) && canInsert(toInsert)) {
            long slotSpace = isEmpty ? getCapacity(toInsert) : getCapacity(toInsert) - currentAmount;
            long toInsertAmount = getAmount(toInsert);
            long inserted = Math.min(slotSpace, toInsertAmount);
            
            if (inserted > 0 && action == TransferAction.ACT) {
                if (isEmpty) {
                    setResource(copyWithAmount(toInsert, inserted));
                } else {
                    setResource(copyWithAmount(resource, currentAmount + inserted));
                }
            }
            
            return inserted;
        }
        
        return 0;
    }
    
    @Override
    default T extract(T toExtract, TransferAction action) {
        T resource = getResource();
        if (!isSameVariant(resource, toExtract)) return blank();
        long extracted = Math.min(getAmount(toExtract), getAmount(resource));
        if (extracted > 0) {
            if (action == TransferAction.ACT) {
                setResource(copyWithAmount(resource, getAmount(resource) - extracted));
            }
            
            return copyWithAmount(toExtract, extracted);
        }
        
        return blank();
    }
}
