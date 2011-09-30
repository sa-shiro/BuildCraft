/** 
 * BuildCraft is open-source. It is distributed under the terms of the 
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package net.minecraft.src.buildcraft.core;

import net.minecraft.src.World;
import net.minecraft.src.buildcraft.api.IAreaProvider;

public class BluePrintBuilder implements IAreaProvider {
	
	public static enum Mode {Simple, Template}
	
	public BluePrint bluePrint;
	int x, y, z;
	public boolean done;
	
	public BluePrintBuilder (BluePrint bluePrint, int x, int y, int z) {
		this.bluePrint = bluePrint;
		this.x = x;
		this.y = y;
		this.z = z;
		done = false;
	}
	
	public BlockContents findNextBlock (World world) {
		return findNextBlock(world, Mode.Simple);
	}
	
	public BlockContents findNextBlock (World world, Mode mode) {
		bluePrint.loadIfNeeded();
		
		for (int j = 0; j < bluePrint.sizeY; ++j) {
			for (int i = 0; i < bluePrint.sizeX; ++i) {
				for (int k = 0; k < bluePrint.sizeZ; ++k) {										
					int xCoord = i + x - bluePrint.anchorX;
					int yCoord = j + y - bluePrint.anchorY;
					int zCoord = k + z - bluePrint.anchorZ;
					
					if (yCoord <= 0) {
						continue;
					}
					
					int blockId = world.getBlockId (xCoord, yCoord, zCoord);
						
					BlockContents content = bluePrint.contents [i][j][k];
					
					if (content == null) {
						continue;
					}
					
					if (mode == Mode.Simple) {	
						content = content.clone ();
						content.x = xCoord;
						content.y = yCoord;
						content.z = zCoord;
						
						if (Utils.softBlock(content.blockId)) {
							if (Utils.softBlock(blockId)) {
								// don't do anything, we got only soft blocks 
								// here
							} else if (!Utils.unbreakableBlock(blockId)) {
								return content;
							}
						} else {
							if (blockId == content.blockId) {
								// don't do anything, we're already on the 
								// proper block
							} else if (!Utils.unbreakableBlock(blockId)) {
								return content;
							}
						}
					} else if (mode == Mode.Template) {
						if ((content.blockId != 0 && Utils.softBlock(blockId))
								|| (content.blockId == 0 
										&& !Utils.softBlock(blockId))
										&& !Utils.unbreakableBlock(blockId)) {
							
							content = new BlockContents();
							content.x = xCoord;
							content.y = yCoord;
							content.z = zCoord;
							content.blockId = blockId;
							
							return content;
						}
					}
				}

			}
		}
		
		done = true;
		
		return null;
	}

	@Override
	public int xMin() {
		bluePrint.loadIfNeeded();
		return x - bluePrint.anchorX;
	}

	@Override
	public int yMin() {
		bluePrint.loadIfNeeded();
		return y - bluePrint.anchorY;
	}

	@Override
	public int zMin() {
		bluePrint.loadIfNeeded();
		return z - bluePrint.anchorZ;
	}

	@Override
	public int xMax() {
		bluePrint.loadIfNeeded();
		return x + bluePrint.sizeX - bluePrint.anchorX - 1;
	}

	@Override
	public int yMax() {
		bluePrint.loadIfNeeded();
		return y + bluePrint.sizeY - bluePrint.anchorY - 1;
	}

	@Override
	public int zMax() {
		bluePrint.loadIfNeeded();
		return z + bluePrint.sizeZ - bluePrint.anchorZ - 1;
	}

	@Override
	public void removeFromWorld() {
		
	}

}
