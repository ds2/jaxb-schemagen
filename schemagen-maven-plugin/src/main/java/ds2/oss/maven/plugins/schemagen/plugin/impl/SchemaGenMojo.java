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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The schemagen mojo.
 *
 * @author dstrauss
 * @version 0.1
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class SchemaGenMojo extends AbstractMojo {
  /**
   * The list of classes.
   */
  @Parameter(required = true, alias = "classNames",property = "schemagen.classNames")
  private Set<String> classNames;
  @Parameter(required = true)
  private Set<NamespaceFilenameDto> namespaces;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    Class<?> foundClasses[] = parseFileList(classNames);
    if(foundClasses==null||foundClasses.length<=0){
      getLog().info("No classes found. Ignoring execution.");
      return;
    }
    try {
      JAXBContext ctx = JAXBContext.newInstance(foundClasses);
      SchemaGenResolver sor=new SchemaGenResolver();
      ctx.generateSchema(sor);
    } catch (JAXBException e) {
      throw new MojoExecutionException("Error when generating the JAXB context!", e);
    } catch (IOException e) {
      throw new MojoExecutionException("Error when writing the schema!",e);
    }
  }

  private Class<?>[] parseFileList(Set<String> fileList) {
    List<Class<?>> rc=new ArrayList<>();
    if(fileList!=null&&fileList.isEmpty()){
      for(String classFile : fileList){
        try {
          Class<?> foundClass=Class.forName(classFile);
          rc.add(foundClass);
        } catch (ClassNotFoundException e) {
          getLog().warn("Cannot load class "+classFile,e);
        }
      }
    }
    return rc.toArray(new Class<?>[0]);
  }
}
