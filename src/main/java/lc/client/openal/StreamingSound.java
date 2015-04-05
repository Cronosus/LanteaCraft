package lc.client.openal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import paulscode.sound.SoundSystem;
import lc.ResourceAccess;
import lc.api.audio.ISound;
import lc.api.audio.ISoundPosition;
import lc.api.audio.ISoundProperties;
import lc.api.audio.ISoundServer;
import lc.client.openal.io.StreamingSoundProtocolHandler;
import lc.common.LCLog;
import lc.common.util.math.Vector3;

public class StreamingSound implements ISound, Comparable<StreamingSound> {

	private final SoundSystem system;
	private ISoundPosition position;
	private ISoundProperties properties;

	private String tag, filename;

	private boolean valid, culled, paused;
	private float realVolume;

	public StreamingSound(ISoundServer server, SoundSystem system, ISoundPosition pos, String f,
			ISoundProperties props, String tag) {
		this.system = system;
		this.position = pos;
		this.properties = props;
		this.tag = tag;
		this.filename = f;
		try {
			String f0 = ResourceAccess.formatResourceName("${ASSET_KEY}:sound/%s", filename);
			ResourceLocation resourcelocation = new ResourceLocation(f0);
			String s1 = String.format("%s:%s:%s", "lcds", resourcelocation.getResourceDomain(),
					resourcelocation.getResourcePath());
			URL path = new URL(null, s1, new StreamingSoundProtocolHandler());
			Vector3 p = (Vector3) position.getPositionObject();
			system.newSource(properties.override(), tag, path, filename, properties.loop(), (float) p.x, (float) p.y,
					(float) p.z, 0, server.falloff() * Math.max(properties.volume(), 1.0F));
			valid = true;
		} catch (IOException ioex) {
			LCLog.warn("Can't create StreamingSound.", ioex);
		}
	}

	@Override
	public void play() {
		if (!valid || playing())
			return;
		if (culled)
			activate();
		paused = false;
		system.play(tag);
	}

	@Override
	public void pause() {
		if (!valid || !playing() || culled)
			return;
		paused = true;
		system.pause(tag);
	}

	@Override
	public void stop() {
		if (!valid || !playing() || culled)
			return;
		system.stop(tag);
		system.rewind(tag);
	}

	@Override
	public void remove() {
		if (!valid)
			return;
		stop();
		system.removeSource(tag);
		valid = false;
		tag = null;
	}

	@Override
	public void cull() {
		if (!valid || culled)
			return;
		system.cull(tag);
		culled = true;
	}

	@Override
	public void activate() {
		if (!valid || !culled)
			return;
		system.activate(tag);
		culled = false;
		if (playing()) {
			stop();
			play();
		}
	}

	@Override
	public void think(ISoundServer server, Object player) {
		if (!valid || !playing()) {
			realVolume = 0;
			return;
		}

		EntityPlayer clientPlayer = (EntityPlayer) player;

		float md = server.falloff();
		md *= Math.max(properties.volume(), 1.0F);
		float rolloff = 1.0F, rd = 1.0F;

		float d = 0.0F;

		if (position.getWorldObject().equals(clientPlayer.worldObj))
			d = (float) ((Vector3) position.getPositionObject()).sub(new Vector3(clientPlayer)).mag();

		if (d > md) {
			realVolume = 0.0F;
			cull();
			return;
		}

		if (rd > d)
			d = rd;
		float gain = 1.0F - rolloff * (d - rd) / (md - rd);
		float nrv = gain * properties.volume() * server.master();

		Vector3 i = new Vector3(clientPlayer);
		Vector3 j = ((Vector3) position.getPositionObject()).sub(i).div(d);
		if (nrv > 0.1f)
			for (int k = 0; k < d; k++) {
				Block b = clientPlayer.worldObj.getBlock((int) i.x, (int) i.y, (int) i.z);
				if (b.isOpaqueCube())
					nrv *= 0.5F;
				else
					nrv *= 0.85F;
				i.add(j);
			}

		if (Math.abs(realVolume / nrv - 1.0F) > 0.06D)
			system.setVolume(tag, Math.min(nrv, 1.0F) * server.master());
		realVolume = nrv;
	}

	@Override
	public float realvol() {
		return realVolume;
	}

	@Override
	public boolean playing() {
		return system.playing(tag);
	}

	@Override
	public boolean paused() {
		return paused;
	}

	@Override
	public ISoundProperties properties() {
		return properties;
	}

	@Override
	public int compareTo(StreamingSound x) {
		if (culled)
			return (int) ((realVolume * 0.9F - x.realVolume) * 128.0F);
		return (int) ((realVolume - x.realVolume) * 128.0F);
	}

}
