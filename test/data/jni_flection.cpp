#include "Thread.h"

#include <jni.h>

#include <jni_cpp.h>

namespace
{
jclass get_class()
{
	static constexpr const char class_name[] = "java.lang.Thread"
    static jclass clazz = jni::get_class(class_name);
    return clazz;
}
}

java::lang::Thread::Thread()
{
    static constexpr const char signature[] = "()V";
    static jmethodID methodID = jni::get_method(get_class(), constructor_method_name, signature);
    jvalue* args = nullptr;
    jni____instance = jni::new_object(get_class(), methodID, args);
}
