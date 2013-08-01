package ds2.oss.maven.plugins.schemagen.plugin.impl;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * The schemagen mojo.
 * @author dstrauss
 * @version 0.1
 */
@Mojo(name="schemagen",defaultPhase = LifecyclePhase.GENERATE_RESOURCES,requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,threadSafe = true)
public class SchemaGenMojo extends AbstractMojo{
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Creating world...");
  }
}
