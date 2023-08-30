package com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station;

import com.oierbravo.mechanical_trading_station.registrate.ModBlockEntities;
import com.oierbravo.trading_station.content.trading_station.powered.PoweredTradingStationBlockEntity;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;


public class MechanicalTradingStationBlock extends HorizontalKineticBlock implements IBE<MechanicalTradingStationBlockEntity> {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;


    private static final VoxelShape RENDER_SHAPE = Shapes.box(0, 0, 0, 1, 1, 1);
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
            if (blockEntity instanceof PoweredTradingStationBlockEntity) {
                ((PoweredTradingStationBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand handIn, BlockHitResult hit) {
        if (!pLevel.isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if(blockEntity instanceof MechanicalTradingStationBlockEntity) {
                NetworkHooks.openScreen(((ServerPlayer)pPlayer), (MechanicalTradingStationBlockEntity) blockEntity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
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
        return RENDER_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return RENDER_SHAPE;
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource r) {
        boolean previouslyPowered = state.getValue(POWERED);
        if (previouslyPowered != worldIn.hasNeighborSignal(pos))
            worldIn.setBlock(pos, state.cycle(POWERED), 2);
    }
    public static boolean canOutputTo(BlockGetter world, BlockPos basinPos, Direction direction) {
        BlockPos neighbour = basinPos.relative(direction);
        BlockPos output = neighbour.below();
        BlockState blockState = world.getBlockState(neighbour);

        if (FunnelBlock.isFunnel(blockState)) {
            if (FunnelBlock.getFunnelFacing(blockState) == direction)
                return false;
        } else if (!blockState.getCollisionShape(world, neighbour)
                .isEmpty()) {
            return false;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(output);
            if (blockEntity instanceof BeltBlockEntity) {
                BeltBlockEntity belt = (BeltBlockEntity) blockEntity;
                return belt.getSpeed() == 0 || belt.getMovementFacing() != direction.getOpposite();
            }
        }

        DirectBeltInputBehaviour directBeltInputBehaviour =
                BlockEntityBehaviour.get(world, output, DirectBeltInputBehaviour.TYPE);
        if (directBeltInputBehaviour != null)
            return directBeltInputBehaviour.canInsertFromSide(direction);
        return false;
    }
}
