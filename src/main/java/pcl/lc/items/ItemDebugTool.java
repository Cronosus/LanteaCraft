package pcl.lc.items;

import java.util.logging.Level;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateController;
import pcl.lc.tileentity.TileEntityStargateRing;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDebugTool extends Item {
	@SideOnly(Side.CLIENT)
	private IIcon theIcon;

	public ItemDebugTool() {
		super();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return LanteaCraft.getAssetKey() + ":" + getUnlocalizedName() + "_" + LanteaCraft.getProxy().getRenderMode();
	}

	/**
	 * Callback for item usage. If the item does something special on right clicking, he will
	 * have one of those. Return True if something happen and false if it don't. This is for
	 * ITEMS, not BLOCKS
	 */
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4,
			int par5, int par6, int par7, float par8, float par9, float par10) {
		String side = (par3World.isRemote) ? "client" : "server";
		LanteaCraft.getLogger().log(Level.INFO,
				"Debugger used at (" + par4 + ", " + par5 + ", " + par6 + ") on " + side);
		par2EntityPlayer.addChatMessage("Data for (" + par4 + ", " + par5 + ", " + par6 + ") side " + side + ":");

		TileEntity entity = par3World.getBlockTileEntity(par4, par5, par6);
		if (entity instanceof TileEntityStargateBase) {
			TileEntityStargateBase base = (TileEntityStargateBase) entity;
			par2EntityPlayer.addChatMessage("type: TileEntityStargateBase");
			par2EntityPlayer.addChatMessage("isValid: " + (base.getAsStructure().isValid() ? "yes" : "no"));
			par2EntityPlayer.addChatMessage("partCount: " + base.getAsStructure().getPartCount());
		} else if (entity instanceof TileEntityStargateRing) {
			TileEntityStargateRing ring = (TileEntityStargateRing) entity;
			par2EntityPlayer.addChatMessage("type: TileEntityStargateBase");
			par2EntityPlayer.addChatMessage("isMerged: " + (ring.getAsPart().isMerged() ? "yes" : "no"));
		} else if (entity instanceof TileEntityStargateController) {
			TileEntityStargateController controller = (TileEntityStargateController) entity;
			par2EntityPlayer.addChatMessage("type: TileEntityStargateController");
			par2EntityPlayer.addChatMessage("isLinkedToBase: " + (controller.isLinkedToStargate ? "yes" : "no"));
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		itemIcon = par1IconRegister.registerIcon("pcl_lc:creative_icon");
	}

}
