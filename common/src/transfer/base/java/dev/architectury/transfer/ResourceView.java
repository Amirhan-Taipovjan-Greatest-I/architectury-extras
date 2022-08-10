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

import com.google.common.base.Predicates;
import dev.architectury.transfer.view.CapacityView;
import dev.architectury.transfer.wrapper.filtering.FilteringResourceView;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;

/**
 * Represents an <b>immutable</b> view of a resource.
 *
 * @param <T> the type of resource
 */
public interface ResourceView<T> extends TransferView<T>, CapacityView<T> {
    /**
     * Returns the resource that this view represents.
     * The returned resource is <b>immutable</b>.
     *
     * @return the resource
     */
    T getResource();
    
    /**
     * Returns the capacity of this view.
     *
     * @return the capacity
     */
    @ApiStatus.NonExtendable
    default long getResourceCapacity() {
        return getCapacity(getResource());
    }
    
    @Override
    default T extract(Predicate<T> toExtract, long maxAmount, TransferAction action) {
        if (toExtract.test(getResource())) {
            return extract(copyWithAmount(getResource(), maxAmount), action);
        }
        
        return blank();
    }
    
    @Override
    default T extractAny(long maxAmount, TransferAction action) {
        return extract(copyWithAmount(getResource(), maxAmount), action);
    }
    
    @Override
    default ResourceView<T> unmodifiable() {
        return filter(Predicates.alwaysFalse());
    }
    
    @Override
    default ResourceView<T> onlyInsert() {
        return filter(Predicates.alwaysTrue(), Predicates.alwaysFalse());
    }
    
    @Override
    default ResourceView<T> onlyExtract() {
        return filter(Predicates.alwaysFalse(), Predicates.alwaysTrue());
    }
    
    @Override
    default ResourceView<T> filter(Predicate<T> filter) {
        return filter(filter, filter);
    }
    
    @Override
    default ResourceView<T> filter(Predicate<T> insert, Predicate<T> extract) {
        return FilteringResourceView.of(this, insert, extract);
    }
}
