package buildcraft.transport.pipe.behaviour;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import buildcraft.transport.api_move.IPipe;
import buildcraft.transport.api_move.PipeBehaviour;

public class PipeBehaviourSandstone extends PipeBehaviour {

    public PipeBehaviourSandstone(IPipe pipe) {
        super(pipe);
    }

    public PipeBehaviourSandstone(IPipe pipe, NBTTagCompound nbt) {
        super(pipe, nbt);
    }

    @Override
    public int getTextureIndex(EnumFacing face) {
        return 0;
    }

    @Override
    public boolean canConnect(EnumFacing face, PipeBehaviour other) {
        return true;
    }

    @Override
    public boolean canConnect(EnumFacing face, TileEntity oTile) {
        return false;
    }
}
