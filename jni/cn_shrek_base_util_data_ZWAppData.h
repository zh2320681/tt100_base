/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class cn_shrek_base_util_data_ZWAppData */

#ifndef _Included_cn_shrek_base_util_data_ZWAppData
#define _Included_cn_shrek_base_util_data_ZWAppData
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     cn_shrek_base_util_data_ZWAppData
 * Method:    putData
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_shrek_base_util_data_ZWAppData_putData
  (JNIEnv *, jobject, jstring, jstring);

/*
 * Class:     cn_shrek_base_util_data_ZWAppData
 * Method:    saveDataInfoFile
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_cn_shrek_base_util_data_ZWAppData_saveDataInfoFile
  (JNIEnv *, jobject);

/*
 * Class:     cn_shrek_base_util_data_ZWAppData
 * Method:    getValue
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_cn_shrek_base_util_data_ZWAppData_getValue
  (JNIEnv *, jobject, jstring);

/*
 * Class:     cn_shrek_base_util_data_ZWAppData
 * Method:    loadData
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_cn_shrek_base_util_data_ZWAppData_loadData
  (JNIEnv *, jobject);

/*
 * Class:     cn_shrek_base_util_data_ZWAppData
 * Method:    nativeSetAssetManager
 * Signature: (Ljava/lang/Object;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_shrek_base_util_data_ZWAppData_nativeSetAssetManager
  (JNIEnv *, jobject, jobject, jstring);

#ifdef __cplusplus
}
#endif
#endif
