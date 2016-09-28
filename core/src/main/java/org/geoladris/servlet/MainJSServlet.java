package org.geoladris.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geoladris.PluginDescriptor;
import org.geoladris.RequireTemplate;
import org.geoladris.config.Config;
import org.geoladris.config.PluginDescriptors;

public class MainJSServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private String output;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    boolean useCache = Boolean.parseBoolean(System.getenv(AppContextListener.ENV_CONFIG_CACHE));
    doGet(req, resp, useCache);
  }

  /**
   * Only for testing purposes.
   * 
   * TODO: Fix when we have a consistent configuration variable management
   * (https://github.com/geoladris/core/issues/28)
   * 
   * @param req
   * @param resp
   * @param useCache
   * @throws ServletException
   * @throws IOException
   */
  void doGet(HttpServletRequest req, HttpServletResponse resp, boolean useCache)
      throws ServletException, IOException {
    if (!useCache || output == null) {
      Config config = (Config) getServletContext().getAttribute(AppContextListener.ATTR_CONFIG);
      Locale locale = (Locale) req.getAttribute(LangFilter.ATTR_LOCALE);
      PluginDescriptors pluginDescriptors = config.getPluginConfig(locale, req);
      Map<String, String> paths = new HashMap<>();
      Map<String, String> shims = new HashMap<>();
      for (PluginDescriptor plugin : pluginDescriptors.getEnabled()) {
        paths.putAll(plugin.getRequireJSPathsMap());
        shims.putAll(plugin.getRequireJSShims());
      }

      RequireTemplate template =
          new RequireTemplate("/main.js", paths, shims, Collections.<String>emptyList());

      output = template.generate();
    }

    resp.getWriter().print(output);
  }
}
