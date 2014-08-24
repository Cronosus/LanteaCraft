package lc.core;

/**
 * This file is automatically updated by Jenkins as part of the CI build script
 * in Gradle. Don't put any pre-set values here.
 * 
 * @author AfterLifeLochie
 */
public class BuildInfo {
	public static final String modName = "LanteaCraft";
	public static final String modID = "LanteaCraft";

	public static final String versionNumber = "@VERSION@";
	public static final String buildNumber = "@BUILD@";

	/**
	 * Enable or disable general debugging mode.
	 */
	public static final boolean DEBUG = true && isDevelopmentEnvironment();

	/**
	 * Enable or disable the SoundSystem debugging; often this is useful for
	 * recording all the operations on the SoundDevice, meaning that clients
	 * aren't slammed with logging if they don't need to be.
	 */
	public static final boolean SS_DEBUGGING = true && isDevelopmentEnvironment();

	/**
	 * Enable or disable network traffic dumping mode.
	 */
	public static final boolean NET_DEBUGGING = true && isDevelopmentEnvironment();

	/**
	 * Enable or disable asset and configuration access dumping.
	 */
	public static final boolean ASSET_DEBUGGING = true && isDevelopmentEnvironment();

	/**
	 * Enable or disable chunk loading dumping.
	 */
	public static final boolean CHUNK_DEBUGGING = true && isDevelopmentEnvironment();

	/**
	 * Enable unstable features flag.
	 */
	public static final boolean ENABLE_UNSTABLE = true && isDevelopmentEnvironment();

	private static boolean IS_DEV_ENV;

	static {
		IS_DEV_ENV = getBuildNumber() == 0;
	}

	public static int getBuildNumber() {
		if (buildNumber.equals("@" + "BUILD" + "@"))
			return 0;
		return Integer.parseInt(buildNumber);
	}

	public static boolean isDevelopmentEnvironment() {
		return IS_DEV_ENV;
	}

	public static final String webAPI = "http://lanteacraft.com/api/";
}