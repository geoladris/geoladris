package org.fao.unredd.jwebclientAnalyzer;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class PluginDescriptorTest {

	@Test
	public void canHaveNullName() {
		PluginDescriptor descriptor = new PluginDescriptor();
		assertNull(descriptor.getName());
	}

	@Test
	public void equals() {
		fail();
		// PluginDescriptor p1 = new PluginDescriptor();
		// p1.setInstallInRoot(true);
		// PluginDescriptor p2 = new PluginDescriptor();
		// p2.setInstallInRoot(true);
		//
		// String conf = "{'default-conf' : { m1 : true }, "
		// + "requirejs : { paths : { a : '../jslib/A', "
		// + "b : '../jslib/B' } } }";
		// new PluginDescriptorFileReader(conf, false, null)
		// .fillPluginDescriptor(p1);
		// new PluginDescriptorFileReader(conf, false, null)
		// .fillPluginDescriptor(p2);
		// p1.addModule("m1");
		// p2.addModule("m1");
		// p1.addStylesheet("s1");
		// p2.addStylesheet("s1");
		//
		// assertEquals(p1, p2);
		// assertEquals(p1.hashCode(), p2.hashCode());
	}

	@Test
	public void notEquals() {
		fail();
		// PluginDescriptor p1 = new PluginDescriptor(true);
		// PluginDescriptor p2 = new PluginDescriptor(true);
		//
		// String conf = "{'default-conf' : { m1 : true }, "
		// + "requirejs : { paths : { a : '../jslib/A', "
		// + "b : '../jslib/B' } } }";
		// p1.setConfiguration(conf);
		// p2.setConfiguration(conf);
		// p1.addModule("m1");
		// p2.addStylesheet("s1");
		//
		// assertNotSame(p1, p2);
		// assertNotSame(p1.hashCode(), p2.hashCode());
	}

	@Test
	public void equalsEmptyPlugins() {
		fail();
		// assertEquals(new PluginDescriptor(true), new PluginDescriptor(true));
	}

	@Test
	public void modulesAndStylesInstalledInRoot() throws Exception {
		PluginDescriptor descriptor = new PluginDescriptor();
		descriptor.setInstallInRoot(true);

		descriptor.addModule("m1");
		descriptor.addStylesheet("modules/s1.css");
		descriptor.addStylesheet("styles/s1.css");
		descriptor.addStylesheet("theme/s1.css");

		Set<String> modules = descriptor.getModules();
		assertEquals(1, modules.size());
		assertTrue(modules.contains("m1"));
		Set<String> styles = descriptor.getStylesheets();
		assertEquals(3, styles.size());
		assertTrue(styles.contains("modules/s1.css"));
		assertTrue(styles.contains("styles/s1.css"));
		assertTrue(styles.contains("theme/s1.css"));
	}

	@Test
	public void requirePathsAndShimsInstalledInRoot() throws Exception {
		PluginDescriptor descriptor = new PluginDescriptor();

		String paths = "jquery : '../jslib/jquery/jquery', "
				+ "openlayers : '../jslib/OpenLayers/OpenLayers.unredd'";
		String shim = "'fancy-box' : ['jquery']";
		String config = "{installInRoot:true, requirejs : { paths : {" + paths
				+ "}, shim : {" + shim + "}}}";
		new PluginDescriptorFileReader(config, false, null)
				.fillPluginDescriptor(descriptor);

		Map<String, String> pathsMap = descriptor.getRequireJSPathsMap();
		assertEquals(2, pathsMap.size());
		assertTrue(pathsMap.containsKey("openlayers"));
		assertTrue(pathsMap.containsKey("jquery"));
		assertEquals("../jslib/OpenLayers/OpenLayers.unredd",
				pathsMap.get("openlayers"));
		assertEquals("../jslib/jquery/jquery", pathsMap.get("jquery"));
		Map<String, String> shimMap = descriptor.getRequireJSShims();
		assertEquals(1, shimMap.size());
		assertTrue(shimMap.containsKey("fancy-box"));
		assertEquals("[\"jquery\"]", shimMap.get("fancy-box"));
	}

	@Test
	public void modulesAndStylesInstalledOutsideRoot() throws Exception {
		PluginDescriptor descriptor = new PluginDescriptor();
		descriptor.setInstallInRoot(false);
		String name = "myplugin";
		descriptor.setName(name);

		descriptor.addModule("m1");
		descriptor.addStylesheet("modules/s1.css");
		descriptor.addStylesheet("styles/s1.css");
		descriptor.addStylesheet("theme/s1.css");

		Set<String> modules = descriptor.getModules();
		assertEquals(1, modules.size());
		assertTrue(modules.contains(name + "/m1"));
		Set<String> styles = descriptor.getStylesheets();
		assertEquals(3, styles.size());
		assertTrue(styles.contains("modules/" + name + "/s1.css"));
		assertTrue(styles.contains("styles/" + name + "/s1.css"));
		assertTrue(styles.contains("theme/" + name + "/s1.css"));
	}

	@Test
	public void requirePathsAndShimsInstalledOutsideRoot() throws Exception {
		PluginDescriptor descriptor = new PluginDescriptor();
		String name = "myplugin";
		descriptor.setName(name);

		String paths = "jquery : '../jslib/jquery/jquery', "
				+ "openlayers : '../jslib/OpenLayers/OpenLayers.unredd'";
		String shim = "'fancy-box' : ['jquery']";
		String config = "{installInRoot:false, requirejs : { paths : {" + paths
				+ "}, shim : {" + shim + "}}}";
		new PluginDescriptorFileReader(config, false, name)
				.fillPluginDescriptor(descriptor);

		Map<String, String> pathsMap = descriptor.getRequireJSPathsMap();
		assertEquals(2, pathsMap.size());
		assertTrue(pathsMap.containsKey("openlayers"));
		assertTrue(pathsMap.containsKey("jquery"));
		assertEquals("../jslib/" + name + "/OpenLayers/OpenLayers.unredd",
				pathsMap.get("openlayers"));
		assertEquals("../jslib/" + name + "/jquery/jquery",
				pathsMap.get("jquery"));
		Map<String, String> shimMap = descriptor.getRequireJSShims();
		assertEquals(1, shimMap.size());
		assertTrue(shimMap.containsKey("fancy-box"));
		assertEquals("[\"jquery\"]", shimMap.get("fancy-box"));
	}

}
