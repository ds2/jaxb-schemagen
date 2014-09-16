package ds2.oss.bla.tests;

import java.io.InputStream;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test if the maven plugin has generated the xsd, and the xsd must be part of
 * the package now.
 *
 * @author dstrauss
 * @version 0.1
 */
public class ExistsXsdTest {

    @Test
    public void existsXsd() {
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("schemagen-1.xsd");
        Assert.assertNotNull(inputStream, "XSD has not been found. Assume that the plugin did not run.");
    }

    @Test
    public void existsText() {
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.txt");
        Assert.assertNotNull(inputStream);
    }
}
