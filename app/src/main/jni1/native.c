#include <jni.h>
#include <string.h>
#include <android/log.h>

#define DEBUG_TAG "NDK_MainActivity"
jstring Java_com_example_intern_giftest_MainActivity_helloLog(JNIEnv * env, jobject this) {
     return (*env)->NewStringUTF(env, "Hello from native code!");
}