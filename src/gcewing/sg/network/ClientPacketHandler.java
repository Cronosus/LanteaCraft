package gcewing.sg.network;

import java.util.logging.Level;

import gcewing.sg.SGCraft;
import gcewing.sg.tileentity.TileEntityStargateBase;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.Player;

public class ClientPacketHandler {

	public void handlePacket(SGCraftPacket packet, Player player) {
		SGCraft.getLogger().log(Level.INFO, "Incoming SGCraft packet: " + packet.toString());
		if (packet.getType().equals(SGCraftPacket.PacketType.TileUpdate)) {
			int worldName = (Integer) packet.getValue("DimensionID");
			int currentWorld = Minecraft.getMinecraft().theWorld.provider.dimensionId;
			if (worldName == currentWorld) {
				int x = (Integer) packet.getValue("WorldX");
				int y = (Integer) packet.getValue("WorldY");
				int z = (Integer) packet.getValue("WorldZ");
				World w = Minecraft.getMinecraft().theWorld;
				TileEntity tile = w.getBlockTileEntity(x, y, z);
				if (tile instanceof TileEntityStargateBase) {
					TileEntityStargateBase base = (TileEntityStargateBase) tile;
					base.getAsStructure().unpack(packet);
					w.markBlockForRenderUpdate(x, y, z);
				}
			}
			
		}
	}

}
