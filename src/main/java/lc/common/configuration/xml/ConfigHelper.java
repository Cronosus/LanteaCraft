package lc.common.configuration.xml;

import java.util.ArrayList;

/**
 * Configuration file helper class
 *
 * @author AfterLifeLochie
 *
 */
public class ConfigHelper {

	/**
	 * Find or create a configuration set for a module by name, case
	 * insensitive.
	 *
	 * @param list
	 *            The list of modules
	 * @param name
	 *            The name to find
	 * @return The module config, or null if it does not exist
	 */
	public static ComponentConfig findComponentContainer(ComponentConfigList list, String name) {
		for (ComponentConfig element : list.children())
			if (element.name().equalsIgnoreCase("Component") && element.parameters().containsKey("name")
					&& element.parameters().get("name").toString().equalsIgnoreCase(name))
				return element;
		ComponentConfig config = new ComponentConfig("Component", list);
		config.parameters().put("name", name);
		config.parameters().put("enabled", true);
		list.children().add(config);
		config.modify();
		return config;
	}

	/**
	 * Find a configuration node by a class
	 *
	 * @param list
	 *            The list
	 * @param clazz
	 *            A class
	 * @return The node or null if no node exists
	 */
	public static ConfigNode findConfigByClass(ConfigList list, String clazz) {
		ConfigNode targetNode = null;
		for (ConfigNode child : list.children())
			if (child.name().equals(clazz))
				targetNode = child;
		return targetNode;
	}

	/**
	 * Find all configuration nodes of a class
	 *
	 * @param list
	 *            The list
	 * @param clazz
	 *            A class
	 * @return A list of nodes
	 */
	public static ArrayList<ConfigNode> findAllConfigByClass(ConfigList list, String clazz) {
		ArrayList<ConfigNode> result = new ArrayList<ConfigNode>();
		for (ConfigNode child : list.children())
			if (child.name().equals(clazz))
				result.add(child);
		return result;
	}

	/**
	 * Gets or set a parameter on a node in a list if it does not exist.
	 *
	 * @param list
	 *            The list object.
	 * @param clazz
	 *            The class of object
	 * @param name
	 *            The name of the qualifier
	 * @param paramName
	 *            The name of the parameter
	 * @param comment
	 *            The parameter comment, if any
	 * @param value
	 *            The value to set
	 * @return The value which exists or was set
	 */
	public static Object getOrSetParam(ConfigList list, String clazz, String name, String paramName, String comment,
			Object value) {
		ConfigNode targetNode = null;
		for (ConfigNode child : list.children())
			if (child.name().equals(clazz))
				if (child.parameters().containsKey("name") && child.parameters().get("name") instanceof String)
					if (((String) child.parameters().get("name")).equalsIgnoreCase(name))
						targetNode = child;
		if (targetNode == null) {
			targetNode = new ConfigNode(clazz, comment, list);
			targetNode.parameters().put("name", name);
			list.children().add(targetNode);
			targetNode.modify();
		}
		if (!targetNode.parameters().containsKey(paramName)) {
			targetNode.parameters().put(paramName, value);
			targetNode.modify();
		}
		return targetNode.parameters().get(paramName);
	}

	/**
	 * Gets or set a boolean parameter on a node in a list if it does not exist.
	 *
	 * @param list
	 *            The list object.
	 * @param clazz
	 *            The class of object
	 * @param name
	 *            The name of the qualifier
	 * @param paramName
	 *            The name of the parameter
	 * @param comment
	 *            The parameter comment, if any
	 * @param state
	 *            The value to set
	 * @return The value which exists or was set
	 */
	public static boolean getOrSetBooleanParam(ConfigList list, String clazz, String name, String paramName,
			String comment, Boolean state) {
		ConfigNode targetNode = null;
		for (ConfigNode child : list.children())
			if (child.name().equals(clazz))
				if (child.parameters().containsKey("name") && child.parameters().get("name") instanceof String)
					if (((String) child.parameters().get("name")).equalsIgnoreCase(name))
						targetNode = child;
		if (targetNode == null) {
			targetNode = new ConfigNode(clazz, comment, list);
			targetNode.parameters().put("name", name);
			list.children().add(targetNode);
			targetNode.modify();
		}
		if (!targetNode.parameters().containsKey(paramName)) {
			targetNode.parameters().put(paramName, state);
			targetNode.modify();
		}
		return DOMHelper.popBoolean(targetNode.parameters().get(paramName).toString(), false);
	}
}
