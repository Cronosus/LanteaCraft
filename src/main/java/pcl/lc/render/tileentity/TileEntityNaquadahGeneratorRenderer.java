package pcl.lc.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;
import cpw.mods.fml.client.FMLClientHandler;

public class TileEntityNaquadahGeneratorRenderer extends TileEntitySpecialRenderer {
	private ResourceLocation inactiveTexture;
	private ResourceLocation activeTexture;
	private float scale = 1;

	public TileEntityNaquadahGeneratorRenderer() {
		activeTexture = LanteaCraft.getResource("textures/models/naquada_generator_on_"
				+ LanteaCraft.getProxy().getRenderMode() + ".png");
		inactiveTexture = LanteaCraft.getResource("textures/models/naquada_generator_off_"
				+ LanteaCraft.getProxy().getRenderMode() + ".png");
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		if (LanteaCraft.getProxy().isUsingModels()) {
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glScalef(scale, scale, scale);
			GL11.glTranslated(x, y, z);
			int dir = tileEntity.getBlockMetadata();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			if (dir == 1 || dir == 3)
				GL11.glRotatef(dir * 90F, 0F, 1F, 0F);
			else if (dir == 0)
				GL11.glRotatef(-180F, 0F, 1F, 0F);
			else
				GL11.glRotatef(dir * 180F, 0F, 1F, 0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(((TileEntityNaquadahGenerator) tileEntity)
					.isActive() ? activeTexture : inactiveTexture);
			LanteaCraft.Render.modelNaquadahGenerator.renderAll();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}
}