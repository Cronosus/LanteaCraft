package pcl.common.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public abstract class GenericContainerGUI extends GuiContainer {

	final static int defaultTextColor = 0x404040;

	double uscale, vscale;
	float red = 1.0F, green = 1.0F, blue = 1.0F;
	protected int textColor = defaultTextColor;
	boolean textShadow = false;

	public GenericContainerGUI(Container container, int width, int height) {
		super(container);
		field_146999_f = width;
		field_147000_g = height;
	}

	public GenericContainerGUI(GenericContainer container) {
		this(container, container.xSize, container.ySize);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		resetColor();
		textColor = defaultTextColor;
		textShadow = false;
		super.drawScreen(par1, par2, par3);
	}

	@Override
	protected void func_146976_a(float var1, int var2, int var3) {
		GL11.glPushMatrix();
		GL11.glTranslatef(field_147003_i, field_147009_r, 0.0F);
		drawBackgroundLayer();
		GL11.glPopMatrix();
	}

	protected abstract void drawBackgroundLayer();

	@Override
	protected void func_146979_b(int par1, int par2) {
		drawForegroundLayer();
	}

	protected abstract void drawForegroundLayer();

	protected void close() {
		Minecraft.getMinecraft().thePlayer.closeScreen();
	}

	protected void bindTexture(ResourceLocation rsrc) {
		bindTexture(rsrc, 1, 1);
	}

	public void bindTexture(ResourceLocation rsrc, int usize, int vsize) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(rsrc);
		uscale = 1.0 / usize;
		vscale = 1.0 / vsize;
	}

	public void drawTexturedRect(double x, double y, double w, double h) {
		drawTexturedRectUV(x, y, w, h, 0, 0, 1, 1);
	}

	public void drawTexturedRect(double x, double y, double w, double h, double u, double v) {
		drawTexturedRect(x, y, w, h, u, v, w, h);
	}

	public void drawTexturedRect(double x, double y, double w, double h, double u, double v, double us, double vs) {
		drawTexturedRectUV(x, y, w, h, u * uscale, v * vscale, us * uscale, vs * vscale);
	}

	public void drawTexturedRectUV(double x, double y, double w, double h, double u, double v, double us, double vs) {
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.setColorOpaque_F(red, green, blue);
		tess.addVertexWithUV(x, y + h, zLevel, u, v + vs);
		tess.addVertexWithUV(x + w, y + h, zLevel, u + us, v + vs);
		tess.addVertexWithUV(x + w, y, zLevel, u + us, v);
		tess.addVertexWithUV(x, y, zLevel, u, v);
		tess.draw();
	}

	public void setColor(int hex) {
		setColor((hex >> 16) / 255.0, (hex >> 8 & 0xff) / 255.0, (hex & 0xff) / 255.0);
	}

	public void setColor(double r, double g, double b) {
		red = (float) r;
		green = (float) g;
		blue = (float) b;
	}

	public void resetColor() {
		setColor(1, 1, 1);
	}

	public void drawString(String s, int x, int y) {
		Minecraft.getMinecraft().fontRenderer.drawString(s, x, y, textColor, textShadow);
	}

	public void drawCenteredString(String s, int x, int y) {
		Minecraft.getMinecraft().fontRenderer.drawString(s, x - Minecraft.getMinecraft().fontRenderer.getStringWidth(s) / 2, y, textColor,
				textShadow);
	}

	public void drawInventoryName(IInventory inv, int x, int y) {
		drawString(inventoryName(inv), x, y);
	}

	public void drawPlayerInventoryName() {
		drawString(playerInventoryName(), 8, field_146999_f - 96 + 2);
	}

	public static String inventoryName(IInventory inv) {
		String name = inv.func_145825_b();
		if (!inv.func_145818_k_())
			name = StatCollector.translateToLocal(name);
		return name;
	}

	public static String playerInventoryName() {
		return StatCollector.translateToLocal("container.inventory");
	}
}