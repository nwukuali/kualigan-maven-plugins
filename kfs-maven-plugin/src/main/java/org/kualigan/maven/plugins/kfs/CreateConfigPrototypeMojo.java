package org.kualigan.maven.plugins.kfs;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.archiver.UnixStat;
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates a config prototype from the given KFS project resource of all 'build-time' properties. Resolves only required properties and
 * converts them into xml format rice PropertyLoadingFactoryBean understands. A KFS project resource can only be
 * a KFS project directory with source
 */
@Mojo(
	name = "create-config-prototype",
	requiresProject = false
)
public class CreateConfigPrototypeMojo {

	@Parameter(property = "workingDir", required = false, defaultValue = "./")
	private File workingDir;

	/**
	 */
	@Parameter(property = "groupId", defaultValue = "org.kuali.kfs")
	protected String groupId;

	/**
	 */
	@Parameter(property = "artifactId", defaultValue = "kfs")
	protected String artifactId;

	/**
	 */
	@Parameter(property = "version", defaultValue = "5.0")
	protected String version;

	private Map<String, Properties> templateProperties;
	private Map<String, Properties> resolvedProperties;
	private static final String DEFAULT_PROPERTIES = "kfs";
	private static final String UNRESOLVED_PROPERTIES = "unresolved";
	public static final String ZIP_PROPERTIES_PATH = System.getProperty("java.io.tmpdir") + File.separator + "config-properties.zip";

	public void execute() throws MojoExecutionException {
		verifyWorkingDir();
		loadTemplateProperties();
		resolveProperties();
		File propertiesFolder = writeResolvedPropertiesToFiles();
		File zipFile = zipResolvedPropertyFiles(propertiesFolder);
	 	installArtifact(zipFile);
	}

	private void installArtifact(File zipArtifact) {

	}

	private File zipResolvedPropertyFiles(File propertiesFolder) throws MojoExecutionException {
		ZipArchiver zipArchiver = new ZipArchiver();
		final DefaultFileSet fileSet = new DefaultFileSet();
		fileSet.setDirectory(propertiesFolder.getParentFile());
		fileSet.setIncludingEmptyDirectories(true);
		zipArchiver.addFileSet(fileSet);
		final File zipFile = new File(ZIP_PROPERTIES_PATH);
		zipFile.deleteOnExit();
		zipArchiver.setDestFile(zipFile);
		zipArchiver.setDirectoryMode(UnixStat.DIR_FLAG);
		try {
			zipArchiver.createArchive();
		} catch (Exception e) {
			throw new MojoExecutionException("Unable to zip property files", e);
		}
		return zipFile;
	}

	private File writeResolvedPropertiesToFiles() throws MojoExecutionException {
		try {
			File tempZipFolder = new File(System.getProperty("java.io.tmpdir") + File.separator + "props" + File.separator + "META-INF");
			tempZipFolder.mkdirs();
			tempZipFolder.deleteOnExit();
			for (Object propertiesCategory : resolvedProperties.keySet()) {
				final String propertyCategory = (String) propertiesCategory;
				File propertiesFile = new File(tempZipFolder, getPropertyFileName(propertyCategory));
				Writer writer = new BufferedWriter(new FileWriter(propertiesFile));
				writer.append("<config>\r\n");

				final Properties properties = resolvedProperties.get(propertiesCategory);
				for (Object propKey : properties.keySet()) {
					writer.append(String.format("\t<param name=\"%s\">%s</param>\r\n", (String) propKey, properties.get(propKey)));
				}
				writer.append("</config>\r\n");
				writer.flush();
				writer.close();
			}
			return tempZipFolder;
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to write resolved properties to xml files", e);
		}
	}

	private String getPropertyFileName(String propertyCategory) {
		if (propertyCategory.endsWith(".properties")) {
			propertyCategory = propertyCategory.substring(0, propertyCategory.indexOf(".properties"));
		}
		return propertyCategory + "-defaults.xml";
	}

	private void resolveProperties() {
		resolvedProperties = new HashMap<String, Properties>();
		final String propertyCategory = DEFAULT_PROPERTIES;
		Properties defaultProperties = templateProperties.get(propertyCategory);
		for (Object key : defaultProperties.keySet()) {
			final String value = (String) defaultProperties.get(key);
			List<String> unresolvedSubProperties = listUnresolvedSubProperties(value);
			resolveSubProperties(unresolvedSubProperties);
			addResolvedProperty(propertyCategory, key, value);
		}
	}

	private void resolveSubProperties(List<String> unresolvedSubProperties) {
		boolean subPropertyResolved;
		for (String subProperty : unresolvedSubProperties) {
			subPropertyResolved = false;
			for (String category : templateProperties.keySet()) {
				if (templateProperties.get(category).containsKey(subProperty)) {
					addResolvedProperty(category, subProperty, (String) templateProperties.get(category).get(subProperty));
					List<String> unresolvedSubSubProperties = listUnresolvedSubProperties(subProperty);
					if (unresolvedSubProperties.size() > 0) {
						resolveSubProperties(unresolvedSubSubProperties);
					}
					subPropertyResolved = true;
					break;
				}
			}
			if (!subPropertyResolved) {
				//Add it anyway as unresolved
				addResolvedProperty(UNRESOLVED_PROPERTIES, subProperty, "");
			}
		}
	}

	private void addResolvedProperty(String propertyCategory, Object key, String value) {
		if (!resolvedProperties.containsKey(propertyCategory)) {
			resolvedProperties.put(propertyCategory, new Properties());
		}
		resolvedProperties.get(propertyCategory).put(key, value);
	}

	private List<String> listUnresolvedSubProperties(String value) {
		Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
		Matcher matcher = pattern.matcher(value);
		List<String> result = new ArrayList<String>();
		while (matcher.find()) {
			result.add(matcher.group(1));
		}
		return result;
	}

	private void loadTemplateProperties() throws MojoExecutionException {
		try {
			templateProperties = new HashMap<String, Properties>();
			Properties masterConfigProperties = loadProperties(getMainConfig());
			templateProperties.put(DEFAULT_PROPERTIES, masterConfigProperties);
			for (File propertiesFile : getPropertiesDir().listFiles(new FilenameFilter())) {
				Properties subProperties = loadProperties(propertiesFile);
				templateProperties.put(propertiesFile.getName(), subProperties);
			}
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to load template property files", e);
		}
	}

	private Properties loadProperties(File propertiesFile) throws IOException {
		Properties props = new Properties();
		FileInputStream fis = new FileInputStream(propertiesFile);
		props.load(fis);
		IOUtils.closeQuietly(fis);
		return props;
	}

	private void verifyWorkingDir() throws MojoExecutionException {
		if (!workingDir.exists()) {
			throw new MojoExecutionException("Working dir does not exist - " + workingDir.getAbsolutePath());
		}
		if (!workingDir.isDirectory()) {
			throw new MojoExecutionException("Working dir specified is not a directory - " + workingDir.getAbsolutePath());
		}
		File propertiesDir = getPropertiesDir();
		if (!propertiesDir.exists()) {
			throw new MojoExecutionException("Invalid Working dir specified - Does not contain build/properties sub directory.");
		}
		File configFile = getMainConfig();
		if (!configFile.exists()) {
			throw new MojoExecutionException("Invalid Working dir specified - Configuration file 'work/src/configuration.properties' does not exist");
		}
	}

	private File getMainConfig() {
		return new File(workingDir, "build/project/configuration.properties");
	}

	private File getPropertiesDir() {
		return new File(workingDir, "build/properties");
	}

	private class FilenameFilter implements java.io.FilenameFilter {

		public boolean accept(File dir, String name) {
			return name.endsWith(".properties");
		}
	}


	public static void main(String[] args) {
		CreateConfigPrototypeMojo mojo = new CreateConfigPrototypeMojo();
		mojo.workingDir = new File("C:\\_Projects\\kuali-kfs");
		try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			e.printStackTrace();
		}
	}
}
