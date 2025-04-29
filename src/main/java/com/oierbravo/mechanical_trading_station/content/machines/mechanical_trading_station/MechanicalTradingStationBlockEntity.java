package com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station;

import com.oierbravo.mechanical_trading_station.registrate.ModBlockEntities;
import com.oierbravo.mechanicals.compat.jade.IHavePercent;
import com.oierbravo.mechanicals.foundation.blockEntity.behaviour.DynamicCycleBehavior;
import com.oierbravo.mechanicals.foundation.blockEntity.behaviour.RecipeRequirementsBehaviour;
import com.oierbravo.mechanicals.utility.LibLang;
import com.oierbravo.trading_station.content.trading_recipe.IHaveMachineId;
import com.oierbravo.trading_station.content.trading_recipe.TradingRecipe;
import com.oierbravo.trading_station.content.trading_station.ITradingStationBlockEntity;
import com.oierbravo.trading_station.content.trading_station.TradingStationBlock;
import com.oierbravo.trading_station.content.trading_station.TradingStationMenu;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MechanicalTradingStationBlockEntity extends KineticBlockEntity implements MenuProvider, ITradingStationBlockEntity,DynamicCycleBehavior.DynamicCycleBehaviorSpecifics, IHavePercent, IHaveMachineId, RecipeRequirementsBehaviour.RecipeRequirementsSpecifics<TradingRecipe>{


    public ItemStackHandler inputItems = createInputItemHandler();
    public ItemStackHandler outputItems = createOutputItemHandler();

    public ItemStackHandler targetItemHandler = createTargetItemHandler();

    public int progress = 0;
    public int maxProgress = 1;

    public final ContainerData containerData;
    byte currentRedstoneMode = 0;
    private boolean isWorking = false;

    DynamicCycleBehavior dynamicCycleBehaviour;
    public RecipeRequirementsBehaviour<TradingRecipe> recipeRequirementsBehaviour;

    protected boolean inputLocked = false;


    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        dynamicCycleBehaviour = new DynamicCycleBehavior(this);
        behaviours.add(dynamicCycleBehaviour);

        recipeRequirementsBehaviour = new RecipeRequirementsBehaviour<TradingRecipe>(this);
        behaviours.add(recipeRequirementsBehaviour);
    }

    public MechanicalTradingStationBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        containerData = createContainerData();
    }

    public ContainerData createContainerData(){
        return new ContainerData(){
            @Override
            public int get(int pIndex){
                return switch (pIndex) {
                    case 0 -> dynamicCycleBehaviour.getPrevRunningTicks();
                    case 1 -> dynamicCycleBehaviour.getCycleTime();
                    case 2 -> (int) currentRedstoneMode;
                    default -> 0;
                };
            }


            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> progress = pValue;
                    case 1 -> maxProgress = pValue;
                    case 2 -> currentRedstoneMode = (byte) pValue;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    private ItemStackHandler createTargetItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                resetProgress();
            }
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return true;
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return ItemStack.EMPTY;
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }
        };
    }

    private ItemStackHandler createInputItemHandler() {
        return new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if(!isLocked())
                    return true;
                Optional<RecipeHolder<?>> recipe = getRecipe();
                if(recipe.isEmpty())
                    return true;
                return ((TradingRecipe) recipe.get().value()).matchIngredient(slot, stack);
            }
        };
    }

    @NotNull
    @Nonnull
    private ItemStackHandler createOutputItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return canProcess(stack) && super.isItemValid(slot, stack);
            }
        };
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.MECHANICAL_TRADING_STATION.get(),
                (be, context) -> {
                    Direction localDir = be.getBlockState().getValue(MechanicalTradingStationBlock.HORIZONTAL_FACING);
                    if(context != null && localDir == context.getCounterClockWise())
                        return be.getInputItemHandler();
                    if(context != null && localDir == context.getClockWise())
                        return be.getOutputItemHandler();
                    if(context == null)
                        return null;
                    return null;
                }
        );
    }
    @Override
    public byte getRedstoneMode() {
        return currentRedstoneMode;
    }


    public boolean canProcess(ItemStack stack) {
        return getRecipe().isPresent();
    }

    public void resetProgress() {
        this.dynamicCycleBehaviour.stop();
    }

    @Override
    public int getProcessingTime() {
        return ITradingStationBlockEntity.super.getProcessingTime();
    }

    @Override
    public float getKineticSpeed() {
        return getSpeed();
    }

    @Override
    public boolean tryProcess(boolean simulate) {

        if(getRecipe().isEmpty()){
            dynamicCycleBehaviour.stop();
            return false;
        }
        if(getSpeed() == 0)
            return false;
        if(!isSpeedRequirementFulfilled())
            return false;
        if(!isPowered())
            return false;
        if(!canCraftItem())
            return false;
        if(simulate)
            return true;

        if(this.level != null && this.level.isClientSide())
            return true;

        craftItem();

        notifyUpdate();
        return true;
    }

    @Override
    public void craftCompleted(ItemStack resulItemStack) {
        ITradingStationBlockEntity.super.craftCompleted(resulItemStack);
        DisplayLinkBlock.sendToGatherers(level, getBlockPos(),
                (dgte, b) -> b.itemReceived(dgte, resulItemStack.getCount()), TradedItemCountDisplaySource.class);
    }

    @Override
    public void setRedstoneMode(byte mode) {
        currentRedstoneMode = mode;
        setChanged();
    }

    @Override
    public byte getCurrentRedstoneMode() {
        return currentRedstoneMode;
    }


    @Override
    public void invalidate() {
        super.invalidate();
        invalidateCapabilities();
    }

    public ItemStackHandler  getInputItemHandler(){
        return inputItems;
    }
    public ItemStackHandler getOutputItemHandler(){
        return outputItems;
    }
    public ItemStackHandler getTargetItemHandler(){ return targetItemHandler;}

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inputItems);
        ItemHelper.dropContents(level, worldPosition, outputItems);
    }


    @Override
    public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.put("input", inputItems.serializeNBT(registries));
        tag.put("output", outputItems.serializeNBT(registries));
        tag.put("target", targetItemHandler.serializeNBT(registries));
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        tag.putByte("redstoneMode", currentRedstoneMode);
        tag.putBoolean("isWorking", isWorking);
        tag.putBoolean("inputLocked",inputLocked);
        super.write(tag, registries, clientPacket);

    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        inputItems.deserializeNBT(registries,tag.getCompound("input"));
        outputItems.deserializeNBT(registries,tag.getCompound("output"));
        targetItemHandler.deserializeNBT(registries,tag.getCompound("target"));
        currentRedstoneMode = tag.getByte("redstoneMode");
        inputLocked = tag.getBoolean("inputLocked");
        super.read(tag,registries, clientPacket);

    }

    public void sendToMenu(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(getBlockPos());
        buffer.writeNbt(getUpdateTag(buffer.registryAccess()));
    }

    @Override
    public void setChangedInternal() {
        setChanged();
    }



    public Component getDisplayName() {
        return Component.translatable("block.mechanical_trading_station.mechanical_trading_station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return TradingStationMenu.create(pContainerId, pPlayerInventory, this, this.containerData);

    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        int per = this.getProgressPercent();
        if(this.getProgressPercent() > 0) {
            LibLang.translate("ui.progress", this.getProgressPercent())
                    .forGoggles(tooltip);
            added = true;
        }
        boolean addedRequirements = recipeRequirementsBehaviour.addToGoggleTooltip(tooltip, isPlayerSneaking, added);
        if(addedRequirements)
            added = true;

        return added;
    }
    @Override
    public int getProgressPercent() {
        return  this.dynamicCycleBehaviour.getProgressPercent();
    }

    @Override
    public int getProgress() {
        return this.dynamicCycleBehaviour.getPrevRunningTicks();
    }


    @Override
    public int getMaxProgress() {
        return this.dynamicCycleBehaviour.getCycleTime();
    }

    @Override
    public void cycleStart() {
        setWorking(true);
    }

    @Override
    public void cycleStop() {
        setWorking(false);
    }

    public void setWorking(boolean value){
        isWorking = value;
        if(this.level == null)
            return;
        BlockState pState = getBlockState().setValue(TradingStationBlock.LIT, isWorking());

        getLevel().setBlock(getBlockPos(), pState, 2);
        setChanged(getLevel(), getBlockPos(), pState);
    }

    public boolean isWorking() {
        return isWorking;
    }

    @Override
    public String getMachineId() {
        return "mechanical";
    }


    @Override
    public void setInputLock(boolean lock) {
        inputLocked = lock;
    }

    @Override
    public boolean isLocked() {
        return inputLocked;
    }

    @Override
    public boolean hasEnoughOutputSpace(TradingRecipe tradingRecipe) {
        return hasEnoughOutputSpace(getOutputItemHandler(),tradingRecipe.getResult());
    }

    @Override
    public boolean matchesIngredients(TradingRecipe tradingRecipe) {
        IItemHandler inputInventory = getInputItemHandler();
        Optional<RecipeHolder<?>> match = getRecipe();

        if(match.isEmpty()) {
            this.resetProgress();
            return false;
        }

        ArrayList<ItemStack> currentStackList = new ArrayList<>();
        for(int i = 0; i< 2; i++){
            ItemStack currentStack = inputInventory.getStackInSlot(i);
            if(!currentStack.isEmpty())
                currentStackList.add(currentStack);
        }
        return tradingRecipe.getIngredients().size() == currentStackList.size();
    }
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        readClient(tag, registries);
    }


    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        readClient(tag == null ? new CompoundTag() : tag, registries);
    }
}
