package org.kualigan.maven.plugins.api;

import java.io.File;

public class InstallArtifactRequest {

	enum ArtifactType {CORE, WEB, PARENT, CONFIG}

	private File mavenHome;
	private File artifact;
	private String groupId;
	private String artifactId;
	private String version;
	private String packaging;
	private File sources;
	private String repositoryId;
	private String repositoryUrl;
	private ArtifactType artifactType;

	public static InstallArtifactRequest createCore(File mavenHome, File artifact, String groupId, String artifactId, String version, File artifactSources) {
		InstallArtifactRequest request = new InstallArtifactRequest(ArtifactType.CORE, mavenHome, artifact, groupId, artifactId, version);
		request.sources = artifactSources;
		return request;
	}

	public static InstallArtifactRequest createOverlay(File mavenHome, File artifact, String groupId, String artifactId, String version) {
		return new InstallArtifactRequest(ArtifactType.WEB, mavenHome, artifact, groupId, artifactId, version);
	}

	public static InstallArtifactRequest createParent(File mavenHome, File artifact, String groupId, String artifactId, String version) {
		return new InstallArtifactRequest(ArtifactType.PARENT, mavenHome, artifact, groupId, artifactId, version);
	}

	public static InstallArtifactRequest createConfig(File mavenHome, File artifact, String groupId, String artifactId, String version) {
		return new InstallArtifactRequest(ArtifactType.CONFIG, mavenHome, artifact, groupId, artifactId, version);
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

	//TODO: Hack to get past issue of having a proper api for deploying artifacts
	public InstallArtifactRequest deploy(String repositoryId, String repositoryUrl){
		this.repositoryId = repositoryId;
		this.repositoryUrl = repositoryUrl;
		return this;
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

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	private String resolvePackaging(ArtifactType artifactType) {
		switch (artifactType){
			case CORE: return "jar";
			case WEB: return "war";
			case PARENT: return "pom";
			case CONFIG: return "jar";
			default: return "undefined";
		}
	}
}
