package org.jniflect.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jniflect.JNIflection;
import org.junit.Test;

public class JNIFlectTest
{
	static String readFile(String path) throws IOException
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, Charset.defaultCharset());
	}

	@Test
	public void testJavaLangThread()
	{
		ArrayList<String> whatToFlect = new ArrayList<String>();
        whatToFlect.add("java.lang.String");
		whatToFlect.add("java.lang.Thread");
		
		JNIflection flect = new JNIflection(new URL[]{});

		try
		{
			String[] jniflection = flect.jniflect(whatToFlect);
			String expectedHeader = readFile("test/data/jni_flection.h");
			String expectedSource = readFile("test/data/jni_flection.cpp");

			assertEquals(expectedHeader, jniflection[0]);
			assertEquals(expectedSource, jniflection[1]);
		} catch (IOException e)
		{
			e.printStackTrace();
			assertTrue("Could not read test data.", false);
		}
	}
}
