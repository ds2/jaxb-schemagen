/*
 * Copyright 2013 Dirk Strauss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ds2.oss.maven.plugins.schemagen.plugin.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * The schemagen mojo.
 *
 * @author dstrauss
 * @version 0.1
 */
@Mojo(
        name = "generate",
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.RUNTIME,
        threadSafe = true)
public class SchemaGenMojo extends AbstractMojo {

    /**
     * A logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SchemaGenMojo.class);

    /**
     * The list of classes to scan for JAXB annotations.
     */
    @Parameter(required = true)
    private Set<String> classNames;
    /**
     * This set contains the configuration for all found namespaces, and where
     * to put them into which XSD.
     */
    @Parameter(required = true)
    private Set<NamespaceFilenameDto> namespaces;
    /**
     * The target directory to generate the XSDs into.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/xsd")
    private File targetXsdDirectory;
    /**
     * The plugin descriptor.
     *
     * @parameter name="${plugin}" @readonly
     */
    @Component
    private PluginDescriptor pluginDescr;
    /**
     * The current maven project where this plugin gets executed.
     *
     * @parameter name="${project}" @readonly
     */
    @Component
    private MavenProject project;
    @Component
    private MavenProjectHelper helper;
    /**
     * The build context.
     */
    @Component
    private BuildContext buildCtx;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        LOG.debug("Target is {}", targetXsdDirectory);
        if (pluginDescr != null) {
            LOG.debug("PD exists");
        }

        try {
            Class<?> foundClasses[] = parseFileList(classNames);
            if (foundClasses == null || foundClasses.length <= 0) {
                LOG.warn("No classes found. Ignoring execution.");
                return;
            }
            JAXBContext ctx = JAXBContext.newInstance(foundClasses);
            FileUtils.mkdir(targetXsdDirectory.getAbsolutePath());
            //buildDirectory.mkdirs();
            SchemaGenResolver sor = new SchemaGenResolver(targetXsdDirectory.toPath());
            LOG.debug("NS: {}", namespaces);
            sor.addNamespaces(namespaces);
            ctx.generateSchema(sor);
            LOG.info("XSDs have been generated in {}", targetXsdDirectory);
            //Resource res = new Resource();
            //res.setDirectory(buildDirectory.getAbsolutePath());
            //res.addInclude("*.xsd");
            //project.addResource(res);
            helper.addResource(project, targetXsdDirectory.getAbsolutePath(), Arrays.asList("*.xsd"), null);
            buildCtx.refresh(targetXsdDirectory);
        } catch (JAXBException e) {
            throw new MojoExecutionException("Error when generating the JAXB context!", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Error when writing the schema!", e);
        } catch (DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException("Dep Resolution failed!", ex);
        }
    }

    /**
     * Returns all found classes.
     *
     * @param fileList a set of FQCN
     * @return the loaded classes
     * @throws DependencyResolutionRequiredException if we were unable to load
     * all deps
     */
    private Class<?>[] parseFileList(Set<String> fileList) throws DependencyResolutionRequiredException {
        List<Class<?>> rc = new ArrayList<>();
        ClassLoader cl = createClassLoader();
        if (fileList != null && !fileList.isEmpty()) {
            fileList.stream().map((classFile) -> {
                LOG.debug("Trying to load class {}", classFile);
                return classFile;
            }).forEach((classFile) -> {
                try {
                    Class<?> foundClass = cl.loadClass(classFile);
                    rc.add(foundClass);
                } catch (ClassNotFoundException e) {
                    LOG.warn("Cannot load class " + classFile, e);
                }
            });
        }
        return rc.toArray(new Class<?>[0]);
    }

    /**
     * Creates the classloader for this run.
     *
     * @return the classloader to use
     * @throws DependencyResolutionRequiredException if we could not properly
     * resolve the runtime classpath
     */
    private ClassLoader createClassLoader() throws DependencyResolutionRequiredException {
        List<String> runtimeClasspathElements = project.getRuntimeClasspathElements();
        //ClassRealm realm = pluginDescr.getClassRealm();
        Set<URL> urls = new HashSet<>();
        runtimeClasspathElements.stream().forEach((element) -> {
            LOG.debug("runtime element: {}", element);
            try {
                File f = new File(element);
                URL elementUrl = f.toURI().toURL();
                urls.add(elementUrl);
                //realm.addURL(elementUrl);
            } catch (MalformedURLException ex) {
                LOG.warn("Error when converting a file path to a URL.", ex);
            }
        });
        URLClassLoader ucl
                = new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
        return ucl;
    }
}
