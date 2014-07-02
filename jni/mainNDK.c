/*
 * HelloNDK.c
 *
 *  Created on: 2014年6月20日
 *      Author: shrek
 */
#include "cn_shrek_base_util_data_ZWAppData.h"
#include <android/log.h>
#include "secretFileOpt.h"
#include <string.h>
#include <jni.h>
#include <stdlib.h>

#define JNI_HELLONDK "JNI_HELLONDK"
/*
 * Class:     com_example_hellondk_AppData
 * Method:    putData
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_cn_shrek_base_util_data_ZWAppData_putData
  (JNIEnv *env, jobject obj, jstring key, jstring value){
	char* keyStr = (char*)(*env)->GetStringUTFChars(env,key,NULL);
	char* valueStr = (char*)(*env)->GetStringUTFChars(env,value,NULL);
	printError(JNI_HELLONDK,"添加数据key = %s , value = %s",keyStr,valueStr);
	putValue(keyStr, valueStr);
}

/*
 * Class:     com_example_hellondk_AppData
 * Method:    saveDataInfoFile
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_cn_shrek_base_util_data_ZWAppData_saveDataInfoFile
  (JNIEnv *env, jobject obj){
	saveAllInfo();
}

/*
 * Class:     com_example_hellondk_AppData
 * Method:    getValue
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_cn_shrek_base_util_data_ZWAppData_getValue
  (JNIEnv *env, jobject obj, jstring key){
	char* keyStr = (char*)(*env)->GetStringUTFChars(env,key,NULL);
	char* value = getValueInfo(keyStr);
	return (*env)->NewStringUTF(env, value);
}

JNIEXPORT void JNICALL Java_cn_shrek_base_util_data_ZWAppData_loadData
  (JNIEnv *env, jobject obj){
	loadAllSecInfos();
}

JNIEXPORT void JNICALL Java_cn_shrek_base_util_data_ZWAppData_nativeSetAssetManager
  (JNIEnv *env, jobject obj, jobject assetManager, jstring jfilePath){
	AAssetManager* pAssetMgr = AAssetManager_fromJava(env, assetManager);
	char* filePath = (char*)(*env)->GetStringUTFChars(env,jfilePath,NULL);
	setAssetManager(pAssetMgr,filePath);
}

