package org.geoladris.config.providers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.geoladris.Plugin;
import org.geoladris.config.Config;
import org.geoladris.config.PluginConfigProvider;

import net.sf.json.JSONObject;

/**
 * @deprecated Use {@link PublicConfProvider} instead.
 * @author victorzinho
 */
public class PluginJSONConfigProvider implements PluginConfigProvider {
  private static final Logger logger = Logger.getLogger(PluginJSONConfigProvider.class);

  private static final String PLUGIN_NAME = "core";

  @Override
  public Map<String, JSONObject> getPluginConfig(Config config,
      Map<String, JSONObject> currentConfig, HttpServletRequest request) throws IOException {
    File publicConf = new File(config.getDir(), PublicConfProvider.FILE);
    if (publicConf.isFile()) {
      return null;
    }

    logger.warn("Using deprecated plugin-conf.json; use public-conf instead");
    File pluginConf = new File(config.getDir(), "plugin-conf.json");

    try {
      Plugin plugin = new Plugin(PLUGIN_NAME, pluginConf);
      Map<String, JSONObject> ret = new HashMap<>();
      if (plugin != null) {
        ret.put(PLUGIN_NAME, plugin.getConfiguration());
      }
      return ret;
    } catch (IOException e) {
      return null;
    }

  }

  @Override
  public boolean canBeCached() {
    return true;
  }
}
