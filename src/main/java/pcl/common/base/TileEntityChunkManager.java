package pcl.common.base;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import pcl.lc.LanteaCraft;

public class TileEntityChunkManager implements ForgeChunkManager.LoadingCallback {

	private final LanteaCraft mod;

	public TileEntityChunkManager(LanteaCraft mod) {
		this.mod = mod;
		ForgeChunkManager.setForcedChunkLoadingCallback(mod, this);
	}

	Ticket newTicket(World world) {
		return ForgeChunkManager.requestTicket(mod, world, Type.NORMAL);
	}

	@Override
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
		for (Ticket ticket : tickets) {
			NBTTagCompound nbt = ticket.getModData();
			if (nbt != null)
				if (nbt.getString("type").equals("TileEntity")) {
					int x = nbt.getInteger("xCoord");
					int y = nbt.getInteger("yCoord");
					int z = nbt.getInteger("zCoord");
					TileEntity te = world.getBlockTileEntity(x, y, z);
					if (te instanceof TileEntityChunkLoader)
						if (!((TileEntityChunkLoader) te).reinstateChunkTicket(ticket))
							ForgeChunkManager.releaseTicket(ticket);
				}
		}
	}

}
