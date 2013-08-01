package ds2.oss.maven.plugins.schemagen.plugin.impl;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Our schema gen resolver.
 * @author dstrauss
 * @version  0.1
 */
public class SchemaGenResolver extends SchemaOutputResolver{
  private Map<String,File> nsMap;

  public SchemaGenResolver(){
    super();
    nsMap=new HashMap<>();
  }

  @Override
  public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
    File file = new File(suggestedFileName);
    StreamResult result = new StreamResult(file);
    result.setSystemId(file.toURI().toURL().toString());
    return result;
  }

  public Map<String, File> getNsMap() {
    return nsMap;
  }
}
