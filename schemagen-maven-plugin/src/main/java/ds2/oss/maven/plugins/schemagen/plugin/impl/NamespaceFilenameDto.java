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

import java.net.URI;

/**
 * The dto.
 * 
 * @author dstrauss
 * @version 0.1
 */
public class NamespaceFilenameDto {
    /**
     * The filename.
     */
    private String filename;
    
    /**
     * The namespace.
     */
    private URI namespace;
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NamespaceFilenameDto)) {
            return false;
        }
        
        final NamespaceFilenameDto that = (NamespaceFilenameDto) o;
        
        if (!namespace.equals(that.namespace)) {
            return false;
        }
        
        return true;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public URI getNamespace() {
        return namespace;
    }
    
    @Override
    public int hashCode() {
        return namespace.hashCode();
    }
    
    public void setFilename(final String filename) {
        this.filename = filename;
    }
    
    public void setNamespace(final URI namespace) {
        this.namespace = namespace;
    }
    
    @Override
    public String toString() {
        return "NamespaceFilenameDto{" + "namespace=" + namespace + ", filename=" + filename + '}';
    }
    
}
