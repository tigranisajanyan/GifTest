#include <jni.h>
#include <string.h>
#include <android/log.h>

#define DEBUG_TAG "NDK_MainActivity"


JNIEXPORT jstring JNICALL Java_com_example_intern_giftest_MainActivity_helloLog(JNIEnv * env, jobject this) {

     return (*env)->NewStringUTF(env, "Hello from native code!");

}

JNIEXPORT jstring JNICALL Java_com_example_intern_giftest_MainActivity_gog(JNIEnv * env, jobject this) {

      int i;
      int ds_ret;

      char* newstring;

      jstring ret = 0;

      newstring = (char*)malloc(30);

      if(newstring == NULL)
      {
          return ret;
      }

      memset(newstring, 0, 30);

      newstring = "foo: Test program of JNI.\\n";


      ret = (*env)->NewStringUTF(env, newstring);

      free(newstring);

      return ret;
}