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
package org.kualigan.maven.plugins.kuali;

import org.kualigan.maven.plugins.api.OverlayHelper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import org.apache.maven.archetype.Archetype;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.InvokerLogger;
import org.apache.maven.shared.invoker.MavenInvocationException;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Creates a maven overlay for the given rice prototype
 * 
 * @author Leo Przybylski (przybyls [at] arizona.edu)
 */
 @Mojo(
     name="create-rice-overlay",
     requiresProject = false
     )
public class CreateOverlayMojo extends AbstractMojo {
    @Component(role = org.kualigan.maven.plugins.api.OverlayHelper.class)
    private OverlayHelper helper;

    /**
     */
    @Parameter(property = "localRepository", required = true)
    private ArtifactRepository localRepository;
    
    /**
     */
    @Parameter(property = "groupId", required = true)
    private String groupId;

    /**
     */
    @Parameter(property = "artifactId", required = true)
    private String artifactId;

    /**
     */
    @Parameter(property="version", required = true, defaultValue="1.0-SNAPSHOT")
    private String version;
    
    @Parameter(property = "kr.prototype.groupId", defaultValue = "org.kuali.rice")
    protected String prototypeGroupId;
    
    @Parameter(property = "kr.prototype.artifactId", defaultValue = "rice-web")
    protected String prototypeArtifactId;
    
    @Parameter(property = "kr.prototype.version", defaultValue = "2.2.0-M3")
    protected String prototypeVersion;
    
    @Parameter(property = "archetypeGroupId", defaultValue = "org.kualigan.maven.archetypes")
    protected String archetypeGroupId;
    
    @Parameter(property = "archetypeArtifactId", defaultValue = "kr-archetype")
    protected String archetypeArtifactId;
    
    @Parameter(property = "archetypeVersion", defaultValue = "1.1.4")
    protected String archetypeVersion;
    
    /**
     * The {@code M2_HOME} parameter to use for forked Maven invocations.
     *
     */
    @Parameter(defaultValue = "${maven.home}")
    protected File mavenHome;

    /**
     */
    @Parameter(property = "project")
    private MavenProject project;

    /**
     * Produce an overlay from a given prototype. 
     */
    public void execute() throws MojoExecutionException {
        helper.setCaller(this);
        helper.generateArchetype(getMavenHome(),
                                 archetypeGroupId,
                                 archetypeArtifactId,
                                 archetypeVersion,
                                 groupId,
                                 artifactId,
                                 version,
                                 prototypeGroupId,
                                 prototypeArtifactId,
                                 prototypeVersion);
    }
    
    public void setMavenHome(final File mavenHome) {
        this.mavenHome = mavenHome;
    }
    
    public File getMavenHome() {
        return this.mavenHome;
    }
        
}
