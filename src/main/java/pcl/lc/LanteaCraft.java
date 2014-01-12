package pcl.lc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import pcl.common.helpers.CreativeTabHelper;
import pcl.common.helpers.SpecialBucketHandler;
import pcl.common.network.ModPacket;
import pcl.common.render.RotationOrientedBlockRenderer;
import pcl.lc.blocks.BlockNaquadahGenerator;
import pcl.lc.blocks.BlockStargateBase;
import pcl.lc.blocks.BlockStargateController;
import pcl.lc.blocks.BlockStargateRing;
import pcl.lc.core.MountDir;
import pcl.lc.entity.EntityTokra;
import pcl.lc.fluids.BlockLiquidNaquadah;
import pcl.lc.fluids.ItemSpecialBucket;
import pcl.lc.fluids.LiquidNaquadah;
import pcl.lc.items.ItemControllerCrystal;
import pcl.lc.items.ItemCoreCrystal;
import pcl.lc.items.ItemDebugTool;
import pcl.lc.items.ItemEnergyCrystal;
import pcl.lc.items.ItemNaquadah;
import pcl.lc.items.ItemNaquadahIngot;
import pcl.lc.items.ItemTokraSpawnEgg;
import pcl.lc.render.blocks.BlockNaquadahGeneratorRenderer;
import pcl.lc.render.blocks.BlockStargateBaseRenderer;
import pcl.lc.render.blocks.BlockStargateControllerRenderer;
import pcl.lc.render.blocks.BlockStargateRingRenderer;
import pcl.lc.render.entities.EntityTokraRenderer;
import pcl.lc.render.models.NaquadahGeneratorModel;
import pcl.lc.render.models.StargateControllerModel;
import pcl.lc.render.tileentity.TileEntityNaquadahGeneratorRenderer;
import pcl.lc.render.tileentity.TileEntityStargateBaseRenderer;
import pcl.lc.render.tileentity.TileEntityStargateControllerRenderer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = BuildInfo.modID, name = BuildInfo.modName, version = BuildInfo.versionNumber
		+ "build" + BuildInfo.buildNumber, dependencies = "after:ComputerCraft;after:BuildCraft|Core;after:IC2;after:SGCraft")
public class LanteaCraft {

	/**
	 * The private instance of the mod. Use {@link #getInstance()} to access
	 * this object safely
	 */
	private static LanteaCraft mod;

	/**
	 * Returns the current instance singleton of the LanteaCraft mod object
	 * 
	 * @return The current, if any, instance of the LanteaCraft mod
	 */
	public static LanteaCraft getInstance() {
		return LanteaCraft.mod;
	}

	public static MountDir mount = new MountDir();
	/**
	 * The private instance of the logger used. Use {@link #getLogger()} to
	 * access this object safely
	 */
	private static Logger logger;

	public static Logger getLogger() {
		return LanteaCraft.logger;
	}

	/**
	 * Public declaration of all Block objects
	 */
	public static class Blocks {
		public static BlockStargateBase stargateBaseBlock;
		public static BlockStargateRing stargateRingBlock;
		public static BlockStargateController stargateControllerBlock;

		public static Block naquadahBlock;
		public static Block naquadahOre;

		public static BlockNaquadahGenerator naquadahGenerator;
	}

	/**
	 * Public declaration of all Item objects
	 */
	public static class Items {
		public static ItemNaquadah naquadah;
		public static ItemNaquadahIngot naquadahIngot;
		public static ItemCoreCrystal coreCrystal;
		public static ItemControllerCrystal controllerCrystal;

		public static ItemEnergyCrystal energyCrystal;

		public static ItemTokraSpawnEgg tokraSpawnEgg;

		public static ItemDebugTool debugger;

	}

	/**
	 * Public declaration of all render objects
	 */
	public static class Render {
		public static StargateControllerModel modelController;
		public static NaquadahGeneratorModel modelNaquadahGenerator;

		public static RotationOrientedBlockRenderer blockOrientedRenderer;

		public static BlockStargateBaseRenderer blockBaseRenderer;
		public static BlockStargateRingRenderer blockRingRenderer;
		public static BlockStargateControllerRenderer blockControllerRenderer;
		public static BlockNaquadahGeneratorRenderer blockNaquadahGeneratorRenderer;

		public static TileEntityStargateBaseRenderer tileEntityBaseRenderer;
		public static TileEntityStargateControllerRenderer tileEntityControllerRenderer;
		public static TileEntityNaquadahGeneratorRenderer tileEntityNaquadahGeneratorRenderer;

		public static EntityTokraRenderer entityTokraRenderer;
	}

	/**
	 * Public declaration of all fluids
	 */
	public static class Fluids {
		public static LiquidNaquadah fluidLiquidNaquadah;
		public static BlockLiquidNaquadah fluidLiquidNaquadahHost;
		public static ItemSpecialBucket fluidLiquidNaquadahBucket;
	}

	/**
	 * Public declaration of all entities
	 */
	public static class Entities {
		public static EntityTokra entityTokra;
	}

	public static enum EnumGUIs {
		StargateBase, StargateController, NaquadahGenerator;
	}

	/**
	 * Creative tab instance
	 */
	private static CreativeTabHelper lanteaCraftTab = new CreativeTabHelper(
			CreativeTabs.getNextID(), "LanteaCraft") {
		@Override
		public Item getTabIconItem() {
			return LanteaCraft.Items.debugger;
		}
	};

	/**
	 * Sided proxy.
	 * 
	 * @see pcl.lc.LanteaCraftCommonProxy
	 * @see pcl.lc.LanteaCraftClientProxy
	 */
	@SidedProxy(clientSide = "pcl.lc.LanteaCraftClientProxy", serverSide = "pcl.lc.LanteaCraftCommonProxy")
	public static LanteaCraftCommonProxy proxy;

	/**
	 * ItemSpecialBucket bucket collection handler object - Forge eventbus only.
	 */
	private SpecialBucketHandler bucketHandler = new SpecialBucketHandler();

	/**
	 * Declaration of asset key.
	 */
	private String assetKey = "pcl_lc";

	public LanteaCraft() {
		LanteaCraft.mod = this;
	}

	/**
	 * Get a resource location from an abstract path
	 * 
	 * @param path
	 *            The path to use
	 * @return A fully qualified {@link ResourceLocation} to the resource
	 */
	public static ResourceLocation getResource(String path) {
		return getProxy().fetchResource(path);
	}

	/**
	 * Gets the current Proxy object for LanteaCraft
	 * 
	 * @return The current Proxy object for LanteaCraft
	 */
	public static LanteaCraftCommonProxy getProxy() {
		LanteaCraft.getInstance();
		return LanteaCraft.proxy;
	}

	/**
	 * Gets the currently used asset key for resources and other paths
	 * 
	 * @return The current asset key
	 */
	public static String getAssetKey() {
		return LanteaCraft.getInstance().assetKey;
	}

	/**
	 * Gets the current CreativeTabs creative-tab object
	 * 
	 * @return The current creative tab
	 */
	public static CreativeTabs getCreativeTab() {
		LanteaCraft.getInstance();
		return LanteaCraft.lanteaCraftTab;
	}

	/**
	 * Gets the current SpecialBucketHandler
	 * 
	 * @return The current SpecialBucketHandler
	 */
	public static SpecialBucketHandler getSpecialBucketHandler() {
		return LanteaCraft.getInstance().bucketHandler;
	}

	/**
	 * Handles an incoming {@link ModPacket} with respect to the provided
	 * {@link Player}.
	 * 
	 * @param modPacket
	 *            The packet object
	 * @param player
	 *            The respective player
	 */
	public static void handlePacket(ModPacket modPacket, Player player) {
		proxy.handlePacket(modPacket, player);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		LanteaCraft.logger = e.getModLog();
		mount = new MountDir();
		copyLua();
		proxy.preInit(e);
	}

	/**
	 * FIXME: It's not safe to address things outside of proxies. This needs to
	 * be merged with a valid ComputerCraft handler (later).
	 */
	private void copyLua() {
		InputStream is = getClass().getResourceAsStream(
				"/assets/pcl_lc/lua/dhd");
		OutputStream os = null;

		File file = new File(MountDir.getLocalLuaFolder());
		if (!file.exists())
			file.mkdirs();

		try {
			os = new FileOutputStream(MountDir.getLocalLuaFolder() + "/dhd");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] buffer = new byte[4096];
		int length;
		try {
			while ((length = is.read(buffer)) > 0)
				os.write(buffer, 0, length);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			os.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			is.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}

	@SubscribeEvent
	public void onInitMapGen(InitMapGenEvent e) {
		proxy.onInitMapGen(e);
	}
}
