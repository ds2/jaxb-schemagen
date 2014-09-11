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
import org.codehaus.plexus.classworlds.realm.ClassRealm;

/**
 * The schemagen mojo.
 *
 * @author dstrauss
 * @version 0.1
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class SchemaGenMojo extends AbstractMojo {

    /**
     * The list of classes.
     */
    @Parameter(required = true)
    private Set<String> classNames;
    @Parameter(required = true)
    private Set<NamespaceFilenameDto> namespaces;
    @Parameter(defaultValue = "${project.build.directory}")
    private File buildDirectory;
    @Component
    private PluginDescriptor pluginDescr;
    @Component
    private MavenProject project;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Target is " + buildDirectory);
        if (pluginDescr != null) {
            getLog().info("PD exists");
        }
        
        try {
            Class<?> foundClasses[] = parseFileList(classNames);
            if (foundClasses == null || foundClasses.length <= 0) {
                getLog().info("No classes found. Ignoring execution.");
                return;
            }
            JAXBContext ctx = JAXBContext.newInstance(foundClasses);
            SchemaGenResolver sor = new SchemaGenResolver();
            getLog().info("NS: " + namespaces);
            sor.addNamespaces(namespaces);
            ctx.generateSchema(sor);
            getLog().info("done");
        } catch (JAXBException e) {
            throw new MojoExecutionException("Error when generating the JAXB context!", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Error when writing the schema!", e);
        } catch (DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException("Dep Resolution failed!", ex);
        }
    }
    
    private Class<?>[] parseFileList(Set<String> fileList) throws DependencyResolutionRequiredException {
        URLClassLoader ucl;
        List<Class<?>> rc = new ArrayList<>();
        if (fileList != null && !fileList.isEmpty()) {
            for (String classFile : fileList) {
                getLog().info("Trying to load class " + classFile);
                ClassLoader cl = createClassLoader();
                try {
                    Class<?> foundClass = cl.loadClass(classFile);
                    rc.add(foundClass);
                } catch (ClassNotFoundException e) {
                    getLog().warn("Cannot load class " + classFile, e);
                    testClassLoaders(classFile);
                }
            }
        }
        return rc.toArray(new Class<?>[0]);
    }
    
    private void testClassLoaders(String c) throws DependencyResolutionRequiredException {
        ClassLoader ucl = createClassLoader();
        try {
            ucl.loadClass(c);
            getLog().info("Found " + c + " in UCL");
        } catch (ClassNotFoundException e) {
            getLog().error("No", e);
        }
    }

    /**
     * Creates the classloader for this run.
     *
     * @return the classloader to use
     * @throws DependencyResolutionRequiredException
     */
    private ClassLoader createClassLoader() throws DependencyResolutionRequiredException {
        List<String> runtimeClasspathElements = project.getRuntimeClasspathElements();
        ClassRealm realm = pluginDescr.getClassRealm();
        Set<URL> urls = new HashSet<>();
        runtimeClasspathElements.stream().forEach((element) -> {
            getLog().info("runtime element: " + element);
            try {
                File f = new File(element);
                URL elementUrl = f.toURI().toURL();
                urls.add(elementUrl);
                realm.addURL(elementUrl);
            } catch (MalformedURLException ex) {
                getLog().warn(ex);
            }
        });
        URLClassLoader ucl = new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
        return ucl;
    }
}
