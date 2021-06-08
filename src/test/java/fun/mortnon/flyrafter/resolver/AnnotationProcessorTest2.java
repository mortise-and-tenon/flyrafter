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

        URL url = new URL("jar:" + new File("C:\\Users\\Administrator\\.m2\\repository\\org\\springframework\\boot\\spring-boot\\2.4.5\\spring-boot-2.4.5.jar").toURI().toURL().toString() + "!/");
        list.add(url);
        URLClassLoader urlClassLoader = new URLClassLoader(list.toArray(new URL[list.size()]), this.getClass().getClassLoader());

        List<String> packages = new ArrayList<>();
        packages.add("org.springframework");
        AnnotationProcessor processor = new AnnotationProcessor(urlClassLoader, packages);
        processor.process();
    }
}
