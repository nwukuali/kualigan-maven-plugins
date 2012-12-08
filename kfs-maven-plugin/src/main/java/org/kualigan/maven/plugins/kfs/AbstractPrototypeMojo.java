package org.kualigan.maven.plugins.kfs;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.kualigan.maven.plugins.api.InstallArtifactRequest;
import org.kualigan.maven.plugins.api.PrototypeHelper;

import java.io.*;

public abstract class AbstractPrototypeMojo extends AbstractMojo {

	@Component
	protected PrototypeHelper helper;
	/**
	 */
	@Parameter(property = "localRepository")
	protected ArtifactRepository localRepository;
	/**
	 */
	@Parameter(property = "packageName", defaultValue = "org.kuali.kfs")
	protected String packageName;
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
	/**
	 * WAR file to create a prototype from. Only used when creating a prototype from a war.
	 */
	@Parameter(property = "file")
	protected File file;
	/**
	 * Assembled sources file.
	 */
	@Parameter(property = "sourcesDir")
	protected File sources;
	/**
	 */
	@Parameter(property = "project")
	protected MavenProject project;
	/**
	 */
	@Parameter(property = "repositoryId")
	protected String repositoryId;
	/**
	 */
	@Parameter(property = "repositoryUrl")
	protected String repositoryUrl;
	/**
	 * The {@code M2_HOME} parameter to use for forked Maven invocations.
	 */
	@Parameter(defaultValue = "${maven.home}")
	protected File mavenHome;

	public void execute() throws MojoExecutionException, MojoFailureException {
		helper.setCaller(this);
		helper.installArtifact(InstallArtifactRequest.createParent(mavenHome, generateParentPom(), groupId, artifactId, version).deploy(repositoryId, repositoryUrl));
		doExecute();
	}

	protected abstract void doExecute() throws MojoExecutionException;


	private File generateParentPom() throws MojoExecutionException {
		//TODO: Consolidate extractTempPom & filterTempPom and make this more generic!!
		File templatePomFile = extractTemplatePom();
		return filter(templatePomFile);
	}

	private File filter(File templateFile) throws MojoExecutionException {
		Writer writer = null;
		Reader reader = null;
		try {
			final File pomFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "filtered-pom.xml");
			Context context = new VelocityContext();
			context.put("groupId", groupId);
			context.put("artifactId", artifactId);
			context.put("version", version);
			context.put("packaging", "pom");

			writer = new FileWriter(pomFile);
			reader = new FileReader(templateFile);

			Velocity.init();
			Velocity.evaluate(context, writer, "pom-prototype", reader);

			return pomFile;
		} catch (Exception e) {
			throw new MojoExecutionException("Error trying to filter the pom ", e);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(writer);
		}
	}

	private File extractTemplatePom() throws MojoExecutionException {
		getLog().info("Extracting the Temp Pom");

		final InputStream pom_is = getClass().getClassLoader().getResourceAsStream("prototype-resources/pom.xml");

		byte[] fileBytes = null;
		try {
			final DataInputStream dis = new DataInputStream(pom_is);
			fileBytes = new byte[dis.available()];
			dis.readFully(fileBytes);
			dis.close();
		} catch (Exception e) {
			throw new MojoExecutionException("Wasn't able to read in the prototype pom", e);
		} finally {
			try {
				pom_is.close();
			} catch (Exception e) {
				// Ignore exceptions
			}
		}

		try {
			final File result = new File(System.getProperty("java.io.tmpdir") + File.separator + "pom.xml");
			final FileOutputStream fos = new FileOutputStream(result);
			try {
				fos.write(fileBytes);
			} finally {
				fos.close();
			}
			return result;
		} catch (Exception e) {
			throw new MojoExecutionException("Could not write temporary pom file", e);
		}
	}
}
