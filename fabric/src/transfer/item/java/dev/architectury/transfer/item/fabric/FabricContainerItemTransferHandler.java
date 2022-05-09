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

package dev.architectury.transfer.item.fabric;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import dev.architectury.hooks.item.ItemStackHooks;
import dev.architectury.transfer.ResourceView;
import dev.architectury.transfer.TransferAction;
import dev.architectury.transfer.TransferHandler;
import dev.architectury.utils.Amount;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static dev.architectury.utils.Amount.toInt;

@SuppressWarnings("UnstableApiUsage")
public class FabricContainerItemTransferHandler implements TransferHandler<ItemStack> {
    private final ContainerItemContext context;
    @Nullable
    private final Transaction transaction;
    
    public FabricContainerItemTransferHandler(ContainerItemContext context, @Nullable Transaction transaction) {
        this.context = context;
        this.transaction = transaction;
    }
    
    public ContainerItemContext getContext() {
        return context;
    }
    
    @Override
    public Stream<ResourceView<ItemStack>> streamContents() {
        return Stream.concat(Stream.of(context.getMainSlot()), context.getAdditionalSlots().stream())
                .map(FabricResourceView::new);
    }
    
    @Override
    public int getContentsSize() {
        return 1 + context.getAdditionalSlots().size();
    }
    
    @Override
    public ResourceView<ItemStack> getContent(int index) {
        if (index == 0) return new FabricResourceView(context.getMainSlot());
        return new FabricResourceView(context.getAdditionalSlots().get(index - 1));
    }
    
    public static <T> T firstNonNull(T first, T second) {
        if (first != null) {
            return first;
        }
        return second;
    }
    
    @Override
    public long insert(ItemStack toInsert, TransferAction action) {
        long inserted;
        
        try (Transaction nested = Transaction.openNested(firstNonNull(this.transaction, Transaction.getCurrentUnsafe()))) {
            inserted = this.context.insert(ItemVariant.of(toInsert), toInsert.getCount(), nested);
            
            if (action == TransferAction.ACT) {
                nested.commit();
            }
        }
        
        return inserted;
    }
    
    @Override
    public ItemStack extract(ItemStack toExtract, TransferAction action) {
        if (toExtract.isEmpty()) return blank();
        long extracted;
        
        try (Transaction nested = Transaction.openNested(firstNonNull(this.transaction, Transaction.getCurrentUnsafe()))) {
            extracted = this.context.extract(ItemVariant.of(toExtract), toExtract.getCount(), nested);
            
            if (action == TransferAction.ACT) {
                nested.commit();
            }
        }
        
        return ItemStackHooks.copyWithCount(toExtract, toInt(extracted));
    }
    
    @Override
    public ItemStack extract(Predicate<ItemStack> toExtract, long maxAmount, TransferAction action) {
        try (Transaction nested = Transaction.openNested(firstNonNull(this.transaction, Transaction.getCurrentUnsafe()))) {
            for (StorageView<ItemVariant> view : Iterables.concat(Collections.singletonList(context.getMainSlot()), context.getAdditionalSlots())) {
                if (!view.isResourceBlank() && toExtract.test(view.getResource().toStack(toInt(view.getAmount())))) {
                    long extracted = view.extract(view.getResource(), maxAmount, nested);
                    
                    if (action == TransferAction.ACT) {
                        nested.commit();
                    }
                    
                    return view.getResource().toStack(toInt(extracted));
                }
            }
        }
        
        return blank();
    }
    
    @Override
    public ItemStack blank() {
        return ItemStack.EMPTY;
    }
    
    @Override
    public ItemStack copyWithAmount(ItemStack resource, long amount) {
        return ItemStackHooks.copyWithCount(resource, toInt(amount));
    }
    
    @Override
    public Object saveState() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void loadState(Object state) {
        throw new UnsupportedOperationException();
    }
    
    private class FabricResourceView implements ResourceView<ItemStack> {
        private final SingleSlotStorage<ItemVariant> storage;
        
        private FabricResourceView(SingleSlotStorage<ItemVariant> storage) {
            this.storage = storage;
        }
        
        @Override
        public ItemStack getResource() {
            return storage.getResource().toStack(toInt(storage.getAmount()));
        }
        
        @Override
        public long getCapacity() {
            return storage.getCapacity();
        }
        
        @Override
        public ItemStack copyWithAmount(ItemStack resource, long amount) {
            return ItemStackHooks.copyWithCount(resource, toInt(amount));
        }
        
        @Override
        public long insert(ItemStack toInsert, TransferAction action) {
            if (toInsert.isEmpty()) return 0;
            long inserted;
            
            try (Transaction nested = Transaction.openNested(firstNonNull(FabricContainerItemTransferHandler.this.transaction, Transaction.getCurrentUnsafe()))) {
                inserted = this.storage.insert(ItemVariant.of(toInsert), toInsert.getCount(), nested);
                
                if (action == TransferAction.ACT) {
                    nested.commit();
                }
            }
            
            return inserted;
        }
        
        @Override
        public ItemStack extract(ItemStack toExtract, TransferAction action) {
            if (toExtract.isEmpty()) return blank();
            long extracted;
            
            try (Transaction nested = Transaction.openNested(firstNonNull(FabricContainerItemTransferHandler.this.transaction, Transaction.getCurrentUnsafe()))) {
                extracted = this.storage.extract(ItemVariant.of(toExtract), toExtract.getCount(), nested);
                
                if (action == TransferAction.ACT) {
                    nested.commit();
                }
            }
            
            return copyWithAmount(toExtract, extracted);
        }
        
        @Override
        public ItemStack blank() {
            return ItemStack.EMPTY;
        }
        
        @Override
        public Object saveState() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void loadState(Object state) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void close() {
        }
    }
}
