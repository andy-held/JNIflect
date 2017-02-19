import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class JNIflectTest {
	
	static String readFile(String path) throws IOException {
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, Charset.defaultCharset());
	}

	@Test
	public void testJavaLangThread() {
		Map<String, List<JNIflection.MethodDescription>> whatToFlect = new HashMap<String, List<JNIflection.MethodDescription>>();
		List<JNIflection.MethodDescription> methods = new ArrayList<JNIflection.MethodDescription>();
		JNIflection.MethodDescription md = new JNIflection.MethodDescription();
		md.name = "Thread";
		methods.add(md);
		whatToFlect.put("java.lang.Thread", methods);
		JNIflection flecter = new JNIflection(whatToFlect);
		
		try {
			String expectedHeader = readFile("test/data/jni_flection.h");
			String expectedSource = readFile("test/data/jni_flection.cpp");
			
			assertEquals(expectedHeader, flecter.header);
			assertEquals(expectedSource, flecter.source);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue("Could not read test data.", false);
		}
	}

}
