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

package dev.architectury.transfer;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;

public abstract class EmptyTransferHandler<T> implements TransferHandler<T> {
    @Override
    public Iterator<ResourceView<T>> iterator() {
        return Collections.emptyIterator();
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    @Override
    public ResourceView<T> get(int index) {
        throw new IndexOutOfBoundsException(index);
    }
    
    @Override
    public long insert(T toInsert, TransferAction action) {
        return 0;
    }
    
    @Override
    public T extract(T toExtract, TransferAction action) {
        return blank();
    }
    
    @Override
    public T extract(Predicate<T> toExtract, long maxAmount, TransferAction action) {
        return blank();
    }
    
    @Override
    public Object saveState() {
        return null;
    }
    
    @Override
    public void loadState(Object state) {
    }
}
