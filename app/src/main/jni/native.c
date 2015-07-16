#include <jni.h>
#include <string.h>
#include <android/log.h>

#define DEBUG_TAG "NDK_MainActivity"


JNIEXPORT jint JNICALL Java_com_example_intern_giftest_MainActivity_helloLog(JNIEnv * env, jobject this) {

  jclass cls = (*env)->GetObjectClass(env, this);
  jmethodID mid = (*env)->GetStaticMethodID(env, cls, "intMethod", "(I)I");
  jint square = (*env)->CallStaticIntMethod(env, cls, mid, 5);
     return square;

}

JNIEXPORT jint JNICALL Java_Sample1_intMethod (JNIEnv *env, jobject this, jint num) {
    return num * num;
}

