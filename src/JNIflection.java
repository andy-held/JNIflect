import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JNIflection {
	
	static public class MethodDescription {
		public String name;
		public String[] parameters = null;
		
//		static MethodDescription create(String name, String... argument_class_paths) {
//			MethodDescription md = new MethodDescription();
//			md.name = name;
//			md.parameters = new Class[argument_class_paths.length];
//			for(String class_path: argument_class_paths) {
//				
//			}
//			return md;
//		}
	}
	
	public JNIflection(Map<String, List<MethodDescription>> classes) {
		// TODO: add basic classes to resolved_classes
		// resolve all classes
		ClassLoader cl = this.getClass().getClassLoader();
		Iterator<Map.Entry<String, List<MethodDescription>>> iter = classes.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, List<MethodDescription>> entry = iter.next();
			try {
				String binary_name = entry.getKey();
				Class<?> clazz = cl.loadClass(binary_name);
				if(resolved_classes.containsKey(binary_name)) {
					System.out.println(clazz.getName() + " was already resolved.");
					iter.remove();
				}
				resolved_classes.put(binary_name, clazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				continue;
			}
		}
		
		// JNIflect the classes and methodsja	
		for (Map.Entry<String, List<MethodDescription>> entry : classes.entrySet()) {
			String class_path = entry.getKey();
			List<MethodDescription> methods_names = entry.getValue();	
			Class<?> clazz = resolved_classes.get(class_path);

			Method[] methods = clazz.getMethods();
			for(Method m: methods) {
				System.out.println(m.getName());
			}
		}
	}
	
	private Class<?> getClass(String binary_name) throws ClassNotFoundException
	{
		if(resolved_classes.containsKey(binary_name)) {
			return resolved_classes.get(binary_name);
		}
		Class<?> clazz = this.getClass().getClassLoader().loadClass(binary_name);
		resolved_classes.put(binary_name, clazz);
		return clazz;
	}
	
	private HashMap<String, Class<?>> resolved_classes = new HashMap<String, Class<?>>();
	public String header = "";
	public String source = "";
}
