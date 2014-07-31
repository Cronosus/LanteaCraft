package pcl.lc.module.core.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import pcl.lc.BuildInfo;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.critters.entity.EntityReplicator;
import pcl.lc.util.RegistrationHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTokraSpawnEgg extends Item {
	private IIcon theIcon;

	public ItemTokraSpawnEgg() {
		super();
	}

	@Override
	protected String getIconString() {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", getUnlocalizedName());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
		return 16777215;
	}

	/**
	 * Callback for item usage. If the item does something special on right
	 * clicking, he will have one of those. Return True if something happen and
	 * false if it don't. This is for ITEMS, not BLOCKS
	 */
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4,
			int par5, int par6, int par7, float par8, float par9, float par10) {
		if (par3World.isRemote)
			return true;
		else {
			Block i1 = par3World.getBlock(par4, par5, par6);
			par4 += Facing.offsetsXForSide[par7];
			par5 += Facing.offsetsYForSide[par7];
			par6 += Facing.offsetsZForSide[par7];
			double d0 = 0.0D;

			if (i1.getRenderType() == 11)
				d0 = 0.5D;

			Entity entity = spawnCreature(par3World, par4 + 0.5D, par5 + d0, par6 + 0.5D);

			if (entity != null && !par2EntityPlayer.capabilities.isCreativeMode)
				--par1ItemStack.stackSize;

			return true;
		}
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if (par2World.isRemote)
			return par1ItemStack;
		else {
			MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer,
					true);

			if (movingobjectposition == null)
				return par1ItemStack;
			else {
				if (movingobjectposition.typeOfHit == MovingObjectType.BLOCK) {
					int i = movingobjectposition.blockX;
					int j = movingobjectposition.blockY;
					int k = movingobjectposition.blockZ;

					if (!par2World.canMineBlock(par3EntityPlayer, i, j, k))
						return par1ItemStack;

					if (!par3EntityPlayer.canPlayerEdit(i, j, k, movingobjectposition.sideHit, par1ItemStack))
						return par1ItemStack;

					if (par2World.getBlock(i, j, k).getMaterial() == Material.water) {
						Entity entity = spawnCreature(par2World, i, j, k);
						if (entity != null && !par3EntityPlayer.capabilities.isCreativeMode)
							--par1ItemStack.stackSize;
					}
				}

				return par1ItemStack;
			}
		}
	}

	/**
	 * Spawns the creature specified by the egg's type in the location specified
	 * by the last three parameters. Parameters: world, x, y, z.
	 */
	public static Entity spawnCreature(World par0World, double par2, double par4, double par6) {
		if (!BuildInfo.ENABLE_UNSTABLE)
			return null;
		
		EntityCreature newEntity = new EntityReplicator(par0World);
		newEntity.setLocationAndAngles(par2, par4, par6,
				MathHelper.wrapAngleTo180_float(par0World.rand.nextFloat() * 360.0F), 0.0F);
		EntityLiving newentityliving = newEntity;
		newentityliving.rotationYawHead = newentityliving.rotationYaw;
		newentityliving.renderYawOffset = newentityliving.rotationYaw;
		par0World.spawnEntityInWorld(newEntity);
		newEntity.playLivingSound();

		EntityVillager entity = new EntityVillager(par0World, RegistrationHelper.getRegisteredVillager("tokra"));
		entity.setProfession(RegistrationHelper.getRegisteredVillager("tokra"));
		EntityLiving entityliving = entity;
		entity.setLocationAndAngles(par2, par4, par6,
				MathHelper.wrapAngleTo180_float(par0World.rand.nextFloat() * 360.0F), 0.0F);
		entityliving.rotationYawHead = entityliving.rotationYaw;
		entityliving.renderYawOffset = entityliving.rotationYaw;
		par0World.spawnEntityInWorld(entity);
		entityliving.playLivingSound();

		return entity;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	/**
	 * Gets an icon index based on an item's damage value and the given render
	 * pass
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int par1, int par2) {
		return par2 > 0 ? theIcon : super.getIconFromDamageForRenderPass(par1, par2);
	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye
	 * returns 16 items)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
		super.getSubItems(item, par2CreativeTabs, par3List);
	}

	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		itemIcon = par1IconRegister.registerIcon("spawn_egg");
		theIcon = par1IconRegister.registerIcon("spawn_egg_overlay");
	}
}
