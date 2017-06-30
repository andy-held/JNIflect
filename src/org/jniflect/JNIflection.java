package org.jniflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class JNIflection
{
    
    private ClassLoader cl;
    public JNIflection(URL[] jarURLs)
    {
        cl = new URLClassLoader(jarURLs, JNIflection.class.getClassLoader());
    }

    public String[] jniflect(Collection<String> binary_class_names)
    {
        CodeFile header = new CodeFile("jni_resolved_classes.h");
        CodeFile source = new CodeFile("jni_resolved_classes.cpp");
        
        Set<Class<?>> resolved_classes = new HashSet<Class<?>>();

        header.addIndentedLine("#pragma once");
        header.addIndentedLine("");
        header.addIndentedLine("class _jobject;");
        
        source.addIndentedLine("#include \"" + header.name + "\"");

        for (String binary_class_name : binary_class_names)
        {
            Class<?> clazz;
            try
            {
                clazz = cl.loadClass(binary_class_name);
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
                continue;
            }

            if (resolved_classes.contains(clazz))
            {
                System.out.println(clazz.getName() + " was already resolved.");
                continue;
            }
            // possible modifiers: public protected private abstract static final strictfp
            int class_modifiers = clazz.getModifiers();
            if(Modifier.isProtected(class_modifiers) || Modifier.isPrivate(class_modifiers) || Modifier.isAbstract(class_modifiers)) // what about static, final strictfp?
            {
                System.out.println("Not considering class '" + clazz.getName() + "' because of modifiers : " + Modifier.toString(class_modifiers));
                continue;
            }
            if(clazz.isInterface()) // this will probably become a problem sooon
            {
                System.out.println("Not considering class'" + clazz.toString() + "' because it is an interface");
            }
            resolved_classes.add(clazz);
            
            String class_path = clazz.getName();
            String[] binary_name_tokens = class_path.split("\\.");
            ArrayList<String> namespaces = new ArrayList<String>(Arrays.asList(binary_name_tokens));
            namespaces.remove(binary_name_tokens.length-1);
            namespaces.add(0, "JNI");
            String class_name = binary_name_tokens[binary_name_tokens.length-1];
            for(String namespace: namespaces)
            {
                header.addIndentedLine("namespace "+namespace);
                header.addIndentedLine("{");
                
                source.addIndentedLine("namespace "+namespace);
                source.addIndentedLine("{");
            }

            header.addIndentedLine("struct " + class_name);
            header.addIndentedLine("{");
            header.increaseIndentation();
            source.increaseIndentation();
            
            Constructor<?>[] constructors = clazz.getConstructors();
            for(Constructor<?> c: constructors)
            {
                boolean all_params_resolved = true;
                // possible modifiers: public protected private
                int constructor_mods = c.getModifiers();
                if(Modifier.isPrivate(constructor_mods) || Modifier.isProtected(constructor_mods))
                {
                    System.out.println("Not considering constructor '" + c.toGenericString() + "' because of modifiers : " + Modifier.toString(constructor_mods));
                    continue;
                }
                Class<?>[] parameters = c.getParameterTypes();
                String param_string = "";
                for(int i = 0; i < parameters.length; i++)
                {
                    Class<?> param = parameters[i];
                    if(!resolved_classes.contains(param))
                    {
                        all_params_resolved = false;
                        System.out.println("Could not resolve parameter " + param.getName() + " of constructor " + c.toString());
                        break;
                    }
                    String param_name = "JNI::" + param.getName().replace(".", "::");
                    String param_short_name = param.getSimpleName().toLowerCase();
                    if(i != 0)
                        param_string += ", ";
                    param_string += param_name + " " + param_short_name;
                }
                if(all_params_resolved)
                    header.addIndentedLine(class_name + " (" + param_string + ");");
            }

//            Method[] methods = clazz.getMethods();
//            for (Method m : methods)
//            {
//                boolean all_params_resolved = true;
//                // possible modifiers: public protected private abstract static final synchronized native strictfp
//                int method_mods = m.getModifiers();
//                if(Modifier.isPrivate(method_mods) || Modifier.isProtected(method_mods) || Modifier.isAbstract(method_mods)) // what about final, synchronized, native, strictfp?
//                {
//                    System.out.println("Not considering constructor '" + m.toGenericString() + "' because of modifiers : " + Modifier.toString(method_mods));
//                    continue;
//                }
//                Class<?>[] parameters = m.getParameterTypes();
//                for(Class<?> param: parameters)
//                {
//                    if(!resolved_classes.contains(param))
//                    {
//                        all_params_resolved = false;
//                        System.out.println("Could not resolve parameter " + param.getName() + " of constructor " + m.getName());
//                        break;
//                    }
//                }
//                if(all_params_resolved)
//                {
//                    System.out.println("JNIflecting " + m.toGenericString());
//                }
//            }
            //possible field modifiers: public protected private static final transient volatile
            //possible interface modifiers: public protected private abstract static strictfp
            
            header.addLine("private:");
            header.addIndentedLine("_jobject* jni____instance;");
            header.decreaseIndentation();
            source.decreaseIndentation();
            header.addIndentedLine("}");
            for (int i = 0; i < namespaces.size(); i++)
            {
                header.addIndentedLine("}");
                source.addIndentedLine("}");
            }
        }

        header.checkIndentationIsZero();
        source.checkIndentationIsZero();
        System.out.println(header.data);
        System.out.println(source.data);
        return new String[] { header.data, source.data };
    }
}
