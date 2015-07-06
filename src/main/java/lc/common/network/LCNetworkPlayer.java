package lc.common.network;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SignatureException;

import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.relauncher.Side;
import lc.LCRuntime;
import lc.common.LCLog;
import lc.common.crypto.DSAProvider;
import lc.common.crypto.KeyTrustRegistry;
import lc.common.network.packets.LCServerToServerEnvelope;
import lc.server.HintProviderServer;

public class LCNetworkPlayer {

	private final LCNetworkController controller;
	public int expectedEnvelopes;
	private LCPacketBuffer<LCServerToServerEnvelope> envelopes;
	
	public LCNetworkPlayer(LCNetworkController controller) { 
		this.controller = controller;
	}

	public void addEnvelopePacket(EntityPlayerMP player, LCServerToServerEnvelope envelope) {
		if (envelopes == null)
			envelopes = new LCPacketBuffer<LCServerToServerEnvelope>();
		envelopes.addPacket(envelope);
		if (envelopes.size() >= expectedEnvelopes) {
			try {
				KeyTrustRegistry registry = ((HintProviderServer) LCRuntime.runtime.hints()).getTrustChain();
				PublicKey[] allKeys = registry.contents();
				LCServerToServerEnvelope[] blobs = envelopes.packets();
				PublicKey foundKey = null;
				for (PublicKey aKey : allKeys)
					if (DSAProvider.verify(blobs[0].signature(), blobs[0].data(), aKey))
						foundKey = aKey;
				if (foundKey == null) {
					envelopes.clear();
					throw new LCNetworkException("No public key found for signed payload. Dropping contents.");
				}
				for (LCServerToServerEnvelope blob : blobs) {
					if (!DSAProvider.verify(blob.signature(), blob.data(), foundKey)) {
						envelopes.clear();
						throw new LCNetworkException("Found invalid siganture for signed data. Possibly tampered or invalid packets!");
					}
				}
				for (LCServerToServerEnvelope blob : blobs) {
					LCPacket packet = LCServerToServerEnvelope.unenvelope(blob);
					controller.injectPacket(Side.SERVER, packet, player);
				}
			} catch (IOException ex) {
				LCLog.warn("Problem unpacking enveloped data.", ex);
			} catch (LCNetworkException ex) {
				LCLog.warn("Problem handling enveloped packets.", ex);
			} catch (InvalidKeyException ex) {
				LCLog.warn("Problem with local key storage.", ex);
			} catch (SignatureException ex) {
				LCLog.fatal("Failed to handle cryptographic data.", ex);
			}
		}
	}

}
