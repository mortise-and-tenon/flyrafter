package fun.mortnon.flyrafter.resolver;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Moon Wu
 * @date 2021/6/4
 */
class AnnotationProcessorTest2 {
    @Test
    void classloaderTest() throws MalformedURLException {
        List<URL> list = new ArrayList<>();
        list.add(new File("C:\\projects\\mortnon-projects\\flyrafter\\target\\classes").toURI().toURL());
//        list.add(new File("C:\\Users\\Administrator\\.m2\\repository\\fun\\mortnon\\mortnon-dal\\0.0.1\\mortnon-dal-0.0.1.jar").toURI().toURL());

        URL url = new URL("jar:file:/C:/Users/Administrator/.m2/repository/fun/mortnon/mortnon-dal/0.0.1/mortnon-dal-0.0.1.jar!/");
        list.add(url);
        URLClassLoader urlClassLoader = new URLClassLoader(list.toArray(new URL[list.size()]), this.getClass().getClassLoader());
        AnnotationProcessor processor = new AnnotationProcessor(urlClassLoader);
        processor.process();
    }
}
