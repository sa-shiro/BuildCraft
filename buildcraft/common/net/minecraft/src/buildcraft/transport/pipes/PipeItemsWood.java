/** 
 * BuildCraft is open-source. It is distributed under the terms of the 
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package net.minecraft.src.buildcraft.transport.pipes;

import net.minecraft.src.Block;
import net.minecraft.src.BuildCraftCore;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.buildcraft.api.EntityPassiveItem;
import net.minecraft.src.buildcraft.api.IPowerReceptor;
import net.minecraft.src.buildcraft.api.ISpecialInventory;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.Position;
import net.minecraft.src.buildcraft.api.PowerProvider;
import net.minecraft.src.buildcraft.core.ILiquidContainer;
import net.minecraft.src.buildcraft.core.Utils;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.buildcraft.transport.PipeLogicWood;
import net.minecraft.src.buildcraft.transport.PipeTransportItems;

public class PipeItemsWood extends Pipe implements IPowerReceptor {

	private PowerProvider powerProvider;

	private int baseTexture = 1 * 16 + 0;
	private int plainTexture = 1 * 16 + 15;
	private int nextTexture = baseTexture;

	public PipeItemsWood(int itemID) {
		super(new PipeTransportItems(), new PipeLogicWood(), itemID);

		powerProvider = BuildCraftCore.powerFramework.createPowerProvider();
		powerProvider.configure(50, 1, 64, 1, 64);
		powerProvider.configurePowerPerdition(64, 1);
	}

	@Override
	public int getBlockTexture() {
		return nextTexture;
	}

	@Override
	public void prepareTextureFor(Orientations connection) {
		if (connection == Orientations.Unknown) {
			nextTexture = baseTexture;
		} else {
			int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

			if (metadata == connection.ordinal()) {
				nextTexture = plainTexture;
			} else {
				nextTexture = baseTexture;
			}
		}

	}

	@Override
	public void setPowerProvider(PowerProvider provider) {
		provider = powerProvider;
	}

	@Override
	public PowerProvider getPowerProvider() {
		return powerProvider;
	}

	@Override
	public void doWork() {	
		if (powerProvider.energyStored <= 0) {
			return;
		}

		World w = worldObj;

		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		if (meta > 5) {
			return;
		}

		Position pos = new Position(xCoord, yCoord, zCoord,
				Orientations.values()[meta]);
		pos.moveForwards(1);
		int blockId = w.getBlockId((int) pos.x, (int) pos.y, (int) pos.z);
		TileEntity tile = w.getBlockTileEntity((int) pos.x, (int) pos.y,
				(int) pos.z);

		if (tile == null
				|| !(tile instanceof IInventory || tile instanceof ILiquidContainer)
				|| PipeLogicWood
						.isExcludedFromExtraction(Block.blocksList[blockId])) {
			return;
		}

		if (tile instanceof IInventory) {
			IInventory inventory = (IInventory) tile;

			ItemStack stack = checkExtract(inventory, true,
					pos.orientation.reverse());

			if (stack == null || stack.stackSize == 0) {
				powerProvider.useEnergy(1, 1, false);
				return;
			}

			Position entityPos = new Position(pos.x + 0.5, pos.y
					+ Utils.getPipeFloorOf(stack), pos.z + 0.5,
					pos.orientation.reverse());

			entityPos.moveForwards(0.5);

			EntityPassiveItem entity = new EntityPassiveItem(w, entityPos.x,
					entityPos.y, entityPos.z, stack);

			((PipeTransportItems) transport).entityEntering(entity,
					entityPos.orientation);
		}
	}

	/**
	 * Return the itemstack that can be if something can be extracted from this
	 * inventory, null if none. On certain cases, the extractable slot depends
	 * on the position of the pipe.
	 */
	public ItemStack checkExtract(IInventory inventory, boolean doRemove,
			Orientations from) {
		if (inventory instanceof ISpecialInventory) {
			// TAKE INTO ACCOUNT SPECIAL INVENTORIES!!!
			return ((ISpecialInventory) inventory).extractItem(doRemove, from);
		}

		if (inventory.getSizeInventory() == 2) {
			// This is an input-output inventory

			int slotIndex = 0;

			if (from == Orientations.YNeg || from == Orientations.YPos) {
				slotIndex = 0;
			} else {
				slotIndex = 1;
			}

			ItemStack slot = inventory.getStackInSlot(slotIndex);

			if (slot != null && slot.stackSize > 0) {
				if (doRemove) {
					return inventory.decrStackSize(slotIndex,
							powerProvider.useEnergy(1, slot.stackSize, true));
				} else {
					return slot;
				}
			}
		} else if (inventory.getSizeInventory() == 3) {
			// This is a furnace-like inventory

			int slotIndex = 0;

			if (from == Orientations.YPos) {
				slotIndex = 0;
			} else if (from == Orientations.YNeg) {
				slotIndex = 1;
			} else {
				slotIndex = 2;
			}

			ItemStack slot = inventory.getStackInSlot(slotIndex);

			if (slot != null && slot.stackSize > 0) {
				if (doRemove) {
					return inventory.decrStackSize(slotIndex,
							powerProvider.useEnergy(1, slot.stackSize, true));
				} else {
					return slot;
				}
			}
		} else {
			// This is a generic inventory
			IInventory inv = Utils.getInventory(inventory);

			ItemStack result = checkExtractGeneric(inv, doRemove, from);

			if (result != null) {
				return result;
			}
		}

		return null;
	}

	public ItemStack checkExtractGeneric(IInventory inventory,
			boolean doRemove, Orientations from) {
		for (int k = 0; k < inventory.getSizeInventory(); ++k) {
			if (inventory.getStackInSlot(k) != null
					&& inventory.getStackInSlot(k).stackSize > 0) {

				ItemStack slot = inventory.getStackInSlot(k);

				if (slot != null && slot.stackSize > 0) {
					if (doRemove) {
						return inventory.decrStackSize(k, powerProvider
								.useEnergy(1, slot.stackSize, true));
					} else {
						return slot;
					}
				}
			}
		}

		return null;
	}
	
	@Override
	public int powerRequest() {
		return getPowerProvider().maxEnergyReceived;
	}

}
