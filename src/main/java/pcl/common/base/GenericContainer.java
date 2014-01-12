package pcl.common.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class GenericContainer extends Container {

	protected int xSize, ySize;

	public GenericContainer(int width, int height) {
		xSize = width;
		ySize = height;
	}

	public void addPlayerSlots(EntityPlayer player) {
		addPlayerSlots(player, (xSize - 160) / 2, ySize - 82);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}

	public void addPlayerSlots(EntityPlayer player, int x, int y) {
		InventoryPlayer inventory = player.inventory;
		for (int var3 = 0; var3 < 3; ++var3)
			for (int var4 = 0; var4 < 9; ++var4)
				addSlotToContainer(new Slot(inventory, var4 + var3 * 9 + 9, x + var4 * 18, y + var3 * 18));
		for (int var3 = 0; var3 < 9; ++var3)
			addSlotToContainer(new Slot(inventory, var3, x + var3 * 18, y + 58));
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int i = 0; i < crafters.size(); i++) {
			ICrafting crafter = (ICrafting) crafters.get(i);
			sendStateTo(crafter);
		}
	}

	public abstract void sendStateTo(ICrafting crafter);

	@Override
	public void updateProgressBar(int i, int value) {
	}

}
