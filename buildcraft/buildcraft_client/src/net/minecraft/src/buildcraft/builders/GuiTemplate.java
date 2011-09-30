/** 
 * BuildCraft is open-source. It is distributed under the terms of the 
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package net.minecraft.src.buildcraft.builders;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.IInventory;

public class GuiTemplate extends GuiContainer {
	
	IInventory playerInventory;
	TileTemplate template;
	
	public GuiTemplate(IInventory playerInventory, TileTemplate template) {
		super(new CraftingTemplate(playerInventory, template));
		this.playerInventory = playerInventory;
		this.template = template;
		xSize = 175;
		ySize = 225;
	}
	
    protected void drawGuiContainerForegroundLayer() {
        fontRenderer.drawString(template.getInvName(), 8, 6, 0x404040);
        fontRenderer.drawString(playerInventory.getInvName(), 8, ySize - 152, 0x404040);        
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f) {
		int i = mc.renderEngine
				.getTexture("/net/minecraft/src/buildcraft/builders/gui/template_gui.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(i);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
		int i1 = template.getComputingProgressScaled(24);
		drawTexturedModalRect(j + 79, k + 34, 176, 14, i1 + 1, 16);
	}
}
