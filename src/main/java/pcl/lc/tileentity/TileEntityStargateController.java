package pcl.lc.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import pcl.common.base.GenericTileEntity;
import pcl.common.helpers.ConfigurationHelper;
import pcl.common.util.Trans3;
import pcl.common.util.Vector3;
import pcl.lc.blocks.BlockStargateController;

public class TileEntityStargateController extends GenericTileEntity {

	public static int linkRangeX = 10;
	public static int linkRangeY = 10;
	public static int linkRangeZ = 10;

	public boolean isLinkedToStargate;
	public int linkedX, linkedY, linkedZ;

	public static void configure(ConfigurationHelper cfg) {
		linkRangeX = cfg.getInteger("dhd", "linkRangeX", linkRangeX);
		linkRangeY = cfg.getInteger("dhd", "linkRangeY", linkRangeY);
		linkRangeZ = cfg.getInteger("dhd", "linkRangeZ", linkRangeZ);
	}

	public BlockStargateController getBlock() {
		return (BlockStargateController) getBlockType();
	}

	public Trans3 localToGlobalTransformation() {
		return getBlock().localToGlobalTransformation(field_145851_c, field_145848_d, field_145849_e, func_145832_p(), this);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		isLinkedToStargate = nbt.getBoolean("isLinkedToStargate");
		linkedX = nbt.getInteger("linkedX");
		linkedY = nbt.getInteger("linkedY");
		linkedZ = nbt.getInteger("linkedZ");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("isLinkedToStargate", isLinkedToStargate);
		nbt.setInteger("linkedX", linkedX);
		nbt.setInteger("linkedY", linkedY);
		nbt.setInteger("linkedZ", linkedZ);
	}

	public TileEntityStargateBase getLinkedStargateTE() {
		if (isLinkedToStargate) {
			TileEntity gte = worldObj.getBlockTileEntity(linkedX, linkedY, linkedZ);
			if (gte instanceof TileEntityStargateBase)
				return (TileEntityStargateBase) gte;
		}
		return null;
	}

	public void checkForLink() {
		if (!isLinkedToStargate) {
			Trans3 t = localToGlobalTransformation();
			for (int i = -linkRangeX; i <= linkRangeX; i++)
				for (int j = -linkRangeY; j <= linkRangeY; j++)
					for (int k = 1; k <= linkRangeZ; k++) {
						Vector3 p = t.p(i, j, -k);
						TileEntity te = worldObj.getBlockTileEntity(p.floorX(), p.floorY(), p.floorZ());
						if (te instanceof TileEntityStargateBase)
							if (linkToStargate((TileEntityStargateBase) te))
								return;
					}
		}
	}

	boolean linkToStargate(TileEntityStargateBase gte) {
		if (!isLinkedToStargate && gte.getAsStructure().isValid()) {
			linkedX = gte.xCoord;
			linkedY = gte.yCoord;
			linkedZ = gte.zCoord;
			isLinkedToStargate = true;
			markBlockForUpdate();
			gte.markBlockForUpdate();
			return true;
		}
		return false;
	}

	public void clearLinkToStargate() {
		isLinkedToStargate = false;
		markBlockForUpdate();
	}
}
