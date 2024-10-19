package com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station;

import com.oierbravo.mechanical_lemon_lib.foundation.blockEntity.behaviour.DynamicCycleBehavior;
import com.oierbravo.mechanical_lemon_lib.foundation.blockEntity.behaviour.RecipeRequirementsBehaviour;
import com.oierbravo.mechanical_lemon_lib.jade.IHavePercent;
import com.oierbravo.trading_station.content.trading_recipe.IHaveMachineId;
import com.oierbravo.trading_station.content.trading_recipe.TradingRecipe;
import com.oierbravo.trading_station.content.trading_station.ITradingStationBlockEntity;
import com.oierbravo.trading_station.content.trading_station.TradingStationBlock;
import com.oierbravo.trading_station.content.trading_station.TradingStationMenu;
import com.oierbravo.trading_station.foundation.util.ModLang;
import com.oierbravo.trading_station.network.packets.ItemStackSyncS2CPacket;
import com.oierbravo.trading_station.registrate.ModMessages;
import com.oierbravo.trading_station.registrate.ModRecipes;
import com.simibubi.create.content.equipment.toolbox.ToolboxMenu;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.redstone.displayLink.source.AccumulatedItemCountDisplaySource;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class MechanicalTradingStationBlockEntity extends KineticBlockEntity implements MenuProvider, ITradingStationBlockEntity,DynamicCycleBehavior.DynamicCycleBehaviorSpecifics, IHavePercent, IHaveMachineId, RecipeRequirementsBehaviour.RecipeRequirementsSpecifics<TradingRecipe>{

    private CompoundTag updateTag;

    public ItemStackHandler inputItems = createInputItemHandler();
    public ItemStackHandler outputItems = createOutputItemHandler();

    public ItemStackHandler targetItemHandler = createTargetItemHandler();
    LazyOptional<IItemHandler> inputItemHandler = LazyOptional.of(() -> inputItems);
    LazyOptional<IItemHandler> outputItemHandler = LazyOptional.of(() -> outputItems);

    public int progress = 0;
    public int maxProgress = 1;

    public final ContainerData containerData;
    byte currentRedstoneMode = 0;
    private boolean isWorking = false;

    Optional<TradingRecipe> targetedRecipe;
    String targetedRecipeId;

    DynamicCycleBehavior dynamicCycleBehaviour;
    public RecipeRequirementsBehaviour<TradingRecipe> recipeRequirementsBehaviour;



    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        dynamicCycleBehaviour = new DynamicCycleBehavior(this);
        behaviours.add(dynamicCycleBehaviour);

        recipeRequirementsBehaviour = new RecipeRequirementsBehaviour<TradingRecipe>(this);
        behaviours.add(recipeRequirementsBehaviour);
    }

    public MechanicalTradingStationBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        updateTag = getPersistentData();
        containerData = createContainerData();
        targetedRecipe = Optional.empty();
    }
    public ContainerData createContainerData(){
        return new ContainerData(){
            @Override
            public int get(int pIndex){
                return switch (pIndex) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
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
                if(!getLevel().isClientSide()) {
                    ModMessages.sendToClients(new ItemStackSyncS2CPacket(slot,this.getStackInSlot(0), getBlockPos(), ItemStackSyncS2CPacket.SlotType.TARGET));
                }
                if(!getLevel().isClientSide()) {
                    getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
                // clientSync();
            }
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return true;
                //return canProcess(stack) && super.isItemValid(slot, stack);
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
                //resetProgress();
            }
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return true;
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
                //return false;
            }
        };
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(side == Direction.UP)
            return  super.getCapability(cap, side);
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            if(side == Direction.DOWN)
                return outputItemHandler.cast();
            return inputItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public LazyOptional<IItemHandler>  getInputItemHandler(){
        return inputItemHandler;
    }
    public LazyOptional<IItemHandler>  getOutputItemHandler(){
        return outputItemHandler;
    }

    @Override
    public IItemHandler getTargetItemHandler() {
        return targetItemHandler;
    }

    public ItemStack getTargetItemStack() {
        return targetItemHandler.getStackInSlot(0);
    }

    @Override
    public void setPreferedItem(ItemStack itemStack) {
        this.targetItemHandler.setStackInSlot(0,itemStack);
    }

    public void setItemStack(int slot, ItemStack itemStack,ItemStackSyncS2CPacket.SlotType slotType) {
        if(slotType == ItemStackSyncS2CPacket.SlotType.INPUT)
            inputItems.setStackInSlot(slot,itemStack);
        else if (slotType == ItemStackSyncS2CPacket.SlotType.OUTPUT)
            outputItems.setStackInSlot(slot,itemStack);
        else if (slotType == ItemStackSyncS2CPacket.SlotType.TARGET)
            targetItemHandler.setStackInSlot(slot,itemStack);

    }

    @Override
    public LazyOptional<IEnergyStorage> getEnergyStorageHandler() {
        return LazyOptional.empty();
    }

    @Override
    public byte getRedstoneMode() {
        return currentRedstoneMode;
    }


    public boolean canProcess(ItemStack stack) {
        return getRecipe().isPresent();
    }

    public void resetProgress() {
    //    this.progress = 0;
    //    this.maxProgress = 1;
    }

    @Override
    public void onCycleCompleted() {

    }

    @Override
    public float getKineticSpeed() {
        return getSpeed();
    }

    @Override
    public int getProcessingTime() {
        return getRecipe().map(TradingRecipe::getProcessingTime).orElse(1);
    }

    @Override
    public boolean tryProcess(boolean simulate) {
        return false;
    }

    @Override
    public void playSound() {

    }


    public void craftItem() {
        SimpleContainer inputInventory = getInputInventory();

        Optional<TradingRecipe> recipe = getRecipe();

        if(recipe.isPresent()){
            for (int i = 0; i < recipe.get().getIngredients().size(); i++) {
                Ingredient ingredient = recipe.get().getIngredients().get(i);

                for (int slot = 0; slot < this.inputItems.getSlots(); slot++) {
                    ItemStack itemStack = this.inputItems.getStackInSlot(slot);
                    if(ingredient.test(itemStack)){
                        this.inputItems.extractItem(slot,ingredient.getItems()[0].getCount(),false);
                        inputInventory.setChanged();
                    }
                }
            }
            this.outputItems.insertItem(0, recipe.get().getResult(), false);
        }
        DisplayLinkBlock.sendToGatherers(level, getBlockPos(),
                (dgte, b) -> b.itemReceived(dgte, recipe.get().getResult().getCount()), TradedItemCountDisplaySource.class);

        this.resetProgress();
    }

    public ItemStackHandler getInputItems() {
        return inputItems;
    }

    @Override
    public ItemStackHandler getOutputItems() {
        return null;
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
    public boolean isPowered() {
        if(getSpeed() == 0)
            return true;
        if(!isSpeedRequirementFulfilled())
            return false;
        if(currentRedstoneMode == REDSTONE_MODES.IGNORE.ordinal())
            return true;
        if(currentRedstoneMode == REDSTONE_MODES.LOW.ordinal())
            return !this.getLevel().getBlockState(getBlockPos())
                    .getValue(BlockStateProperties.POWERED);

        return this.getLevel().getBlockState(getBlockPos())
                .getValue(BlockStateProperties.POWERED);
    }


    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inputItemHandler.invalidate();
        outputItemHandler.invalidate();
    }



    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("input", inputItems.serializeNBT());
        tag.put("output", outputItems.serializeNBT());
        tag.put("target", targetItemHandler.serializeNBT());
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);
        tag.putByte("redstoneMode", currentRedstoneMode);
        tag.putBoolean("isWorking", isWorking);
        if(targetedRecipeId != null){
            tag.putString("targetedRecipeId", targetedRecipeId);
        }

    }
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        inputItems.deserializeNBT(tag.getCompound("input"));
        outputItems.deserializeNBT(tag.getCompound("output"));
        targetItemHandler.deserializeNBT(tag.getCompound("target"));
        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
        currentRedstoneMode = tag.getByte("redstoneMode");
        isWorking = tag.getBoolean("isWorking");
        setTargetedRecipeById(tag.getString("targetedRecipeId"));


    }

    public void sendToMenu(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.getBlockPos());
        buffer.writeNbt(this.getUpdateTag());
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(inputItems.getSlots() + 1);
        for (int i = 0; i < inputItems.getSlots(); i++) {
            inventory.setItem(i, inputItems.getStackInSlot(i));
        }
        inventory.setItem(inputItems.getSlots(), inputItems.getStackInSlot(0));

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public Component getDisplayName() {
        return Component.translatable("block.mechanical_trading_station.mechanical_trading_station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return TradingStationMenu.create(pContainerId, pPlayerInventory, this, this.containerData);

    }

    protected void updateProgress() {
        this.progress += Math.abs(getSpeed()/5);
    }

    /*@Override
    public void tick() {
        super.tick();

        if (getSpeed() == 0) {
            setWorking(false);
            return;
        }
        if(!isPowered()) {
            setWorking(false);
            return;
        }
        if(!canCraftItem()) {
            setWorking(false);
            return;
        }

        this.updateProgress();
        setWorking(true);

        maxProgress = getProcessingTime();
        if (progress > maxProgress) {
            craftItem();

        }
        setChanged(getLevel(), getBlockPos(), getBlockState());
    }*/

    @Override
    public int getProgressPercent() {
        return this.progress * 100 / this.maxProgress;
    }

    @Override
    public int getProgress() {
        return this.progress;
    }


    @Override
    public int getMaxProgress() {
        return this.maxProgress;
    }


    public void setWorking(boolean value){
        isWorking = value;
        BlockState pState = getBlockState().setValue(TradingStationBlock.LIT, isWorking());

        getLevel().setBlock(getBlockPos(), pState, 2);
        setChanged(getLevel(), getBlockPos(), pState);
    }

    public boolean isWorking() {
        return isWorking;
    }

    protected Optional<TradingRecipe> getRecipe(){
        if(targetedRecipeId == null)
            return Optional.empty();
        if(!targetedRecipeId.isEmpty())
            targetedRecipe = ModRecipes.findById(this.level,targetedRecipeId);
        return targetedRecipe;
    };

    public boolean canCraftItem() {
        SimpleContainer inputInventory = getInputInventory();
        Optional<TradingRecipe> match = getRecipe();

        if(match.isEmpty()) {
            return false;
        }
        return hasEnoughInputItems(inputInventory, match.get().getIngredients())
                && hasEnoughOutputSpace(this.outputItems, match.get().getResult());
    }
    protected boolean hasEnoughInputItems(SimpleContainer inventory, NonNullList<Ingredient> ingredients){
        int enough = 0;
        for(int ingredientIndex = 0; ingredientIndex < ingredients.size();ingredientIndex ++){
            Ingredient ingredient = ingredients.get(ingredientIndex);
            for(int slot = 0; slot < inventory.getContainerSize(); slot++){
                if(ingredient.test(inventory.getItem(slot))){
                    if(inventory.getItem(slot).getCount() >= ingredient.getItems()[0].getCount() )
                        enough++;
                }
            }
        }
        return ingredients.size() == enough;
    }

    protected boolean hasEnoughOutputSpace(ItemStackHandler stackHandler,ItemStack resultItemStack){
        return stackHandler.getStackInSlot(0).isEmpty() || stackHandler.getStackInSlot(0).is(resultItemStack.getItem()) &&  stackHandler.getStackInSlot(0).getMaxStackSize() - stackHandler.getStackInSlot(0).getCount()  >= resultItemStack.getCount() ;
    }

    public SimpleContainer getInputInventory(){
        int containerSize = 0;
        for(int index = 0; index < getInputItems().getSlots(); index++) {
            if (!getInputItems().getStackInSlot(index).isEmpty())
                containerSize++;
        }

        SimpleContainer inputInventory = new SimpleContainer(containerSize);
        getInputItemHandler().ifPresent(iItemHandler -> {
            for(int slot = 0; slot < iItemHandler.getSlots(); slot++) {
                if(!iItemHandler.getStackInSlot(slot).isEmpty()){
                    inputInventory.addItem(iItemHandler.getStackInSlot(slot));
                }
            }
        });
        return inputInventory;
    }
    @Override
    public void setTargetedRecipeById(ResourceLocation recipeId){
        Optional<TradingRecipe> recipe = ModRecipes.findById(this.getLevel(),recipeId);
        targetedRecipe = recipe;
        targetedRecipeId = recipeId.toString();
        recipe.ifPresent(tradingRecipe -> targetItemHandler.setStackInSlot(0, tradingRecipe.getResult()));
        setChanged();
    }
    public void setTargetedRecipeById(String recipeId){
        Optional<TradingRecipe> recipe = ModRecipes.findById(this.getLevel(), recipeId);
        targetedRecipe = recipe;
        if(recipe.isPresent()) {
            targetItemHandler.setStackInSlot(0,recipe.get().getResult());
        }
        setChanged();

    }
    @Nullable
    public Optional<TradingRecipe> getTargetedRecipe(){
        return targetedRecipe;
    }
    @Override
    public String getTargetedRecipeId() {
        if(targetedRecipe.isPresent())
            return targetedRecipe.get().getId().toString();
        /*if(updateTag.contains("targetedRecipeId"))
            return updateTag.getString("targetedRecipeId");*/
        return "";
    }
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        readClient(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        CompoundTag tag = packet.getTag();
        readClient(tag == null ? new CompoundTag() : tag);
    }

    @Override
    public String getMachineId() {
        return "mechanical";
    }

    @Override
    public boolean hasEnoughOutputSpace() {
        if(outputItems.getStackInSlot(0).getCount() == outputItems.getStackInSlot(0).getMaxStackSize()){
            return false;
        }
        if(!outputItems.getStackInSlot(0).isEmpty() && !outputItems.getStackInSlot(0).is(getRecipe().get().getResult().getItem())){
            return false;
        }
        return true;
    }

    @Override
    public boolean matchIngredients(TradingRecipe tradingRecipe) {
        return false;
    }
}
