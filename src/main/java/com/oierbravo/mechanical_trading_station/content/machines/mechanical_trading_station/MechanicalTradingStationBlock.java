package com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station;

import com.oierbravo.mechanical_trading_station.registrate.ModBlockEntities;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;


public class MechanicalTradingStationBlock extends HorizontalKineticBlock implements IBE<MechanicalTradingStationBlockEntity> {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;


    public static final VoxelShape SHAPE =new AllShapes.Builder(
            Block.box(0, 0, 0, 16, 1, 16)
    )
            .add(
                    new AllShapes.Builder(
                            Block.box(1, 2, 1, 15, 15, 15)
                    ).build()
            )
            .add(
                    new AllShapes.Builder(
                            Block.box(0, 16, 0, 16, 16, 16)
                    ).build()
            )
            .build();

    private static final VoxelShape RENDER_SHAPE = Shapes.box(0, 0, 0, 1, 1, 1);
    //private static final VoxelShape RENDER_SHAPE = Shapes.box(0, 0, 0, 1, 1, 1);
    public MechanicalTradingStationBlock(Properties pProperties) {
        super(pProperties);
       registerDefaultState(getStateDefinition().any().setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(POWERED,false).setValue(LIT, true));

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
        builder.add(LIT);
        super.createBlockStateDefinition(builder);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction prefferedSide = getPreferredHorizontalFacing(context);
        if (prefferedSide != null)
            return defaultBlockState()
                    .setValue(HORIZONTAL_FACING, prefferedSide.getOpposite())
                    .setValue(LIT, false)
                    .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()))
        ;
        return super.getStateForPlacement(context)
                .setValue(LIT, false)
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }
    @Override
    public Class<MechanicalTradingStationBlockEntity> getBlockEntityClass() {
        return MechanicalTradingStationBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MechanicalTradingStationBlockEntity> getBlockEntityType() {
        return ModBlockEntities.MECHANICAL_TRADING_STATION.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.MECHANICAL_TRADING_STATION.create(pPos, pState);
    }


    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof MechanicalTradingStationBlockEntity) {
                ((MechanicalTradingStationBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if(blockEntity instanceof MechanicalTradingStationBlockEntity tradingStationBlockEntity) {
                if (!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer) {
                    serverPlayer.openMenu(tradingStationBlockEntity, tradingStationBlockEntity::sendToMenu);

                }
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());

    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(HORIZONTAL_FACING)
                .getOpposite();
    }
    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING)
                .getAxis();
    }
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
                                boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        if (worldIn.isClientSide)
            return;
        if (!worldIn.getBlockTicks()
                .willTickThisTick(pos, this))
            worldIn.scheduleTick(pos, this, 0);
    }
    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource r) {
        boolean previouslyPowered = state.getValue(POWERED);
        if (previouslyPowered != worldIn.hasNeighborSignal(pos))
            worldIn.setBlock(pos, state.cycle(POWERED), 2);
    }

    public String getMachineId() {
        return "mechanical";
    }
}
