package ds2.oss.maven.plugins.schemagen.plugin.impl;

import java.net.URI;

/**
 * Created by dstrauss on 01.08.13.
 */
public class NamespaceFilenameDto {
  private URI namespace;

  public URI getNamespace() {
    return namespace;
  }

  public void setNamespace(URI namespace) {
    this.namespace = namespace;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof NamespaceFilenameDto)) return false;

    NamespaceFilenameDto that = (NamespaceFilenameDto) o;

    if (!namespace.equals(that.namespace)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return namespace.hashCode();
  }

  private String filename;
}
