package org.kualigan.maven.plugins.api;

import java.io.File;

public class InstallArtifactRequest {

	enum ArtifactType {CORE, OVERLAY, PARENT;}

	private File mavenHome;
	private File artifact;
	private String groupId;
	private String artifactId;
	private String version;
	private String packaging;
	private File sources;
	private String repositoryId;
	private ArtifactType artifactType;

	public static InstallArtifactRequest createCore(File mavenHome, File artifact, String groupId, String artifactId, String version, File artifactSources) {
		InstallArtifactRequest request = new InstallArtifactRequest(ArtifactType.CORE, mavenHome, artifact, groupId, artifactId, version);
		request.sources = artifactSources;
		return request;
	}

	public static InstallArtifactRequest createOverlay(File mavenHome, File artifact, String groupId, String artifactId, String version) {
		return new InstallArtifactRequest(ArtifactType.OVERLAY, mavenHome, artifact, groupId, artifactId, version);
	}

	public static InstallArtifactRequest createParent(File mavenHome, File artifact, String groupId, String artifactId, String version) {
		return new InstallArtifactRequest(ArtifactType.PARENT, mavenHome, artifact, groupId, artifactId, version);
	}

	private InstallArtifactRequest(ArtifactType artifactType, File mavenHome, File artifact, String groupId, String artifactId, String version) {
		this.mavenHome = mavenHome;
		this.artifact = artifact;
		this.groupId = groupId;
		this.version = version;
		this.artifactId = String.format("%s-%s",artifactId,artifactType.name().toLowerCase());
		this.packaging = resolvePackaging(artifactType);
		this.artifactType = artifactType;
	}

	public File getArtifact() {
		return artifact;
	}

	public File getSources() {
		return sources;
	}

	public File getMavenHome() {
		return mavenHome;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public String getPackaging() {
		return packaging;
	}

	public String getGeneratePom() {
		return String.valueOf(!artifactType.equals(ArtifactType.PARENT));
	}

	private String resolvePackaging(ArtifactType artifactType) {
		switch (artifactType){
			case CORE: return "jar";
			case OVERLAY: return "war";
			case PARENT: return "pom";
			default: return "undefined";
		}
	}
}
