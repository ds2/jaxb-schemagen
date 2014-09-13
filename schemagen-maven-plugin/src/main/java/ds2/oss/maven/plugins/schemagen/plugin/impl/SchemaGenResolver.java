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
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Our schema gen resolver.
 *
 * @author dstrauss
 * @version 0.1
 */
public class SchemaGenResolver extends SchemaOutputResolver {
    /**
     * A logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    /**
     * The namespace map.
     */
    private final Map<String, File> nsMap;
    /**
     * The base directory.
     */
    private Path baseDirectory;
    
    /**
     * Inits the resolver.
     * 
     * @param base
     *            the base directory to use
     */
    public SchemaGenResolver(final Path base) {
        super();
        nsMap = new HashMap<>();
        baseDirectory = base;
    }
    
    @Override
    public Result createOutput(final String namespaceUri, final String suggestedFileName) throws IOException {
        File file = nsMap.get(namespaceUri);
        if (file == null) {
            file = new File(suggestedFileName);
        }
        LOG.debug("NS {} will get File {}", new Object[] { namespaceUri, file });
        final StreamResult result = new StreamResult(file);
        result.setSystemId(file.toURI().toURL().toString());
        return result;
    }
    
    /**
     * Returns the current namespace map.
     * 
     * @return the namespace map
     */
    public Map<String, File> getNsMap() {
        return nsMap;
    }
    
    /**
     * Adds some new namespace data.
     * 
     * @param namespaces
     *            the namespace data
     */
    public void addNamespaces(final Set<NamespaceFilenameDto> namespaces) {
        if (namespaces == null || namespaces.isEmpty()) {
            return;
        }
        for (NamespaceFilenameDto d : namespaces) {
            String fileName = d.getFilename();
            if (!fileName.toLowerCase().endsWith(".xsd")) {
                fileName += ".xsd";
            }
            final Path filePath = baseDirectory.resolve(fileName).normalize();
            LOG.debug("Namespace {} configured to be put into {}", new Object[] { d.getNamespace(), filePath });
            nsMap.put(d.getNamespace().toString(), filePath.toFile());
        }
    }
}
