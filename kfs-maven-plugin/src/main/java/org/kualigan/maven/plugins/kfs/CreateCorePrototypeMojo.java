/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kualigan.maven.plugins.kfs;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.kualigan.maven.plugins.api.InstallArtifactRequest;

import java.io.File;

/**
 * Creates a prototype from the given KFS project resource. A KFS project resource can be either
 * of the following:
 * <ul>
 * <li>KFS war file</li>
 * <li>KFS project directory with source</li>
 * <li>KFS svn repo</li>
 * </ul>
 */
@Mojo(
	name = "create-core-prototype",
	requiresProject = false
)
public class CreateCorePrototypeMojo extends AbstractPrototypeMojo {

	/**
	 * <p>Create a prototype</p>
	 * <p/>
	 * <p>The following are the steps for creating a prototype from a KFS instance</p>
	 * <p>
	 * When using a war file:
	 * <ol>
	 * <li>Basically, use the install-file mojo and generate a POM from the archetype</li>
	 * </ol>
	 * </p>
	 * <p/>
	 * The basic way to understand how this works is the kfs-archetype is used to create kfs
	 * maven projects, but it is dynamically generated. Then, source files are copied to it.
	 */
	protected void doExecute() throws MojoExecutionException {
		try {
			final File prototypeJar = helper.repack(file, artifactId);
			helper.installArtifact(InstallArtifactRequest.createCore(mavenHome, prototypeJar, groupId, artifactId, version, sources).deploy(repositoryId, repositoryUrl));
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to create a new Core KFS Prototype", e);
		}
	}


}
