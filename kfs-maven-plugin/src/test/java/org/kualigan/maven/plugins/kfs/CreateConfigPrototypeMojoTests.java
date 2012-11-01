package org.kualigan.maven.plugins.kfs;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.kualigan.maven.plugins.api.PrototypeHelper;
import org.mockito.Mockito;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CreateConfigPrototypeMojoTests extends TestCase {

	public void testExecute() throws Exception {

		PrototypeHelper prototypeHelper = Mockito.mock(PrototypeHelper.class);
		CreateConfigPrototypeMojo mojo = new CreateConfigPrototypeMojo();
		mojo.helper = prototypeHelper;
		mojo.workingDir = createTempWorkingDir();
		Map<String, Properties> resolvedProperties = mojo.debug();
		for (String cat : resolvedProperties.keySet()) {
			System.out.println(cat);
			final Properties properties = resolvedProperties.get(cat);
			for (Object propKey : properties.keySet()) {
				System.out.println(String.format("\t%s=%s", (String) propKey, properties.get(propKey)));
			}
		}
		assertEquals(12,resolvedProperties.size());
		assertEquals(189,resolvedProperties.get("kfs").size());
	}

	private File createTempWorkingDir() throws Exception {
		File workDir = new File(System.getProperty("java.io.tmpdir"), "workingDir");
		if (workDir.exists()) {
			FileUtils.deleteDirectory(workDir);
		}
		workDir.mkdirs();
		unzip(workDir, new File(this.getClass().getClassLoader().getResource("build.zip").getFile()));
		return workDir;
	}

	private void unzip(File dir, File zip) throws Exception {
		Enumeration entries;
		ZipFile zipFile = new ZipFile(zip);
		entries = zipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();

			if (entry.isDirectory()) {
				// Assume directories are stored parents first then children.
				// This is not robust, just for demonstration purposes.
				(new File(dir, entry.getName())).mkdir();
				continue;
			}

			final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(new File(dir, entry.getName())));
			IOUtils.copy(zipFile.getInputStream(entry), output);
			output.close();
		}
		zipFile.close();
	}
}
