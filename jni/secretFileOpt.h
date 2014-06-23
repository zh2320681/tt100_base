//
//  secretFileOpt.h
//  SecretInfo
//
//  Created by Shrek on 14-6-19.
//  Copyright (c) 2014年 Shrek. All rights reserved.
//
#include <stdio.h>
#include <android/log.h>
#include <android/asset_manager_jni.h>

typedef struct SecretMap{
    char* key;
    char* value;
}SecretMap;

#ifndef SecretInfo_secretFileOpt_h
#define SecretInfo_secretFileOpt_h

/**
 *  通过key得到value值
 */
char* getValueInfo(char* keyStr);

/**
 *  加载所有信息
 */
void loadAllSecInfos();

/**
 *  添加信息
 */
void putValue(char *key,char *value);

/**
 *  保存信息
 */
void saveAllInfo();


void setAssetManager(AAssetManager* pAssetMgr,char* filePath);

#endif
