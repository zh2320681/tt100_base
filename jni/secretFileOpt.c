//
//  secretFileOpt.c
//  SecretInfo
//
//  Created by Shrek on 14-6-19.
//  Copyright (c) 2014年 Shrek. All rights reserved.
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "secretFileOpt.h"
#include <android/log.h>
#include "app.h"

//#define FILENAME "/mnt/sdcard/MINE1.Secret"
#define CUSTOMKEYSTON 's'
#define WARP '\n'
#define GET_ARRAY_LEN(array,len){len = (sizeof(array) / sizeof(array[0]));}

#define TAG "JNI_secretFileOpt"

static SecretMap* allLine[50];

AAssetManager* pAssetMgr;
char* fileName;

FILE* openFile(int isRead) {
	FILE* f;
	if (isRead == YES) {
		f = fopen(fileName, "r");
	} else {
		f = fopen(fileName, "w");
	}
	if (f == NULL) {
		printInfo(TAG,"无法打开文件:%s\n", fileName);
	} else {
		printInfo(TAG,"已经打开文件:%s\n", fileName);
	}
	return f;
}

/**
 * 打开只读
 */
FILE* openReadFile() {
	return openFile(YES);
}

/**
 * 打开只写
 */
FILE* openWriteFile() {
	return openFile(NO);
}

void closeFile(FILE* f) {
	if (f != NULL) {
		fclose(f);
	}
}

int getFileSize(FILE *aasset) {
	if (aasset == NULL) {
//		__android_log_print(ANDROID_LOG_ERROR, TAG, "【 aasset is null 】");
		printError(TAG, "【 FILE is null 】",NULL);
	} else {
		fseek(aasset, 0, SEEK_END);
	}
	return ftell(aasset);
}

char* encode(char* source) {
	char* tagetStr = malloc(strlen(source));
	int i;
	for (i = 0; i < strlen(source); i++) {
        tagetStr[i] = source[i]^CUSTOMKEYSTON;
//		tagetStr[i] = source[i];
	}
	printError(TAG, "加密后的内容是:%s", tagetStr);
	return tagetStr;
}

char* decode(char* source) {
	char* tagetStr = malloc(strlen(source));
	int i;
	for (i = 0; i < strlen(source); i++) {
		tagetStr[i] = source[i]^CUSTOMKEYSTON;
//		tagetStr[i] = source[i];
	}
	printError(TAG, "解码后的内容是:%s", tagetStr);
	return tagetStr;
}

/**
 *  读取所有文件信息
 *  @return 字串对象
 */
char* readInfos() {
	FILE* aasset = openReadFile();
	int size = getFileSize(aasset);
//    printInfo(TAG,"=================>%d",size);
	char* buff = malloc(size + 1);
    fseek(aasset, 0, SEEK_SET);
    fread(buff, sizeof(unsigned char), size, aasset);
	buff[size] = '\0';
//    printInfo(TAG,"=================>%s",buff);
	closeFile(aasset);
	return decode(buff);
}

SecretMap* addLineObj(char* content, int lineIndex) {
	int searchIndex = 0;
	int m;
	for (m = 0; m < strlen(content); m++) {
		if (content[m] == '=') {
			searchIndex = m;
			break;
		}
	}
	char *titleStr = malloc(searchIndex);
	char *contentStr = malloc(strlen(content) - searchIndex);

	SecretMap* map = malloc(sizeof(SecretMap));
	strncpy(titleStr, content, searchIndex);
	strncpy(contentStr, &content[0] + searchIndex + 1,
			strlen(content) - searchIndex);
	map->key = titleStr;
	map->value = contentStr;
	printInfo(TAG,"line.title = %s \n", titleStr);
	return map;
}

/**
 *  加载所有安全信息
 */
void loadAllSecInfos() {
	if (allLine[0] == NULL) {
		char* buff = readInfos();

//        int warpNum = 0;
//        for (int j = 0; j<sizeof(buff); j++) {
//            if (buff[j] == WARP) {
//                warpNum++;
//            }
//        }
//        allLine = malloc(sizeof(SecretMap)*50);
		int lastLineIndex = 0, index = 0;
		int j;
		for (j = 0; j < strlen(buff); j++) {
			char cChar = buff[j];
			if (cChar == WARP) {
				//下一行
				char* line = malloc(j - lastLineIndex);
				strncpy(line, &buff[lastLineIndex], j - lastLineIndex);
				lastLineIndex = (j + 1);
				//字符串查找
				allLine[index] = addLineObj(line, index);
				//memset(line, 0, 1024);
				index++;
			}
		}
	}

}

char* getValueInfo(char* keyStr) {
	int i;
	int arraySize;
	GET_ARRAY_LEN(allLine,arraySize);
	for (i = 0; i < arraySize; i++) {
		SecretMap *map = allLine[i];
//		__android_log_print(ANDROID_LOG_ERROR,TAG,"key====>%s , keyStr=====>%s",map->key,keyStr);
//		__android_log_print(ANDROID_LOG_ERROR,TAG,"key====>%p  ",map);
//		__android_log_print(ANDROID_LOG_ERROR,TAG," keyStr=====>%s",keyStr);
		if (map != NULL
				&& map->key != NULL
				&& keyStr != NULL
				&& strcmp(keyStr, map->key) == 0) {
			return map->value;
		}
	}
	return NULL;
}

/**
 *  添加的时候 判断 长度
 */
void putValue(char *key, char *value) {
	int curentSize = 0;
	int i;

	int arraySize;
	GET_ARRAY_LEN(allLine,arraySize);
	for (i = 0; i < arraySize; i++) {
		SecretMap *map = allLine[i];
		if (map != NULL) {
			curentSize++;
		}
	}
//	printInfo(TAG, "当前数据的长度为:%d\n", curentSize);
	//长度操过了
	if (curentSize == arraySize) {

	}
	SecretMap *map = malloc(sizeof(SecretMap));
	map->key = key;
	map->value = value;
	allLine[curentSize] = map;
	printInfo(TAG, "添加数据[key = %s]成功\n", key);
}

/**
 *  保存信息
 */
void saveAllInfo() {
	char* sourInfo = malloc(10000);

	int i;
	int arraySize;
	GET_ARRAY_LEN(allLine,arraySize);
	for (i = 0; i < arraySize; i++) {
		SecretMap *map = allLine[i];
		if (map != NULL
				&& map->key != NULL
				&& map->value != NULL) {
			strcat(sourInfo, map->key);
			strcat(sourInfo, "=");
			strcat(sourInfo, map->value);
			strcat(sourInfo, "\n");
		}
	}

    FILE *f = openWriteFile();
    if (f != NULL) {
        int size = fprintf(f, encode(sourInfo));
        printInfo(TAG,"保存到文件的数据长度: %d\n",size);
     }else{
    	 printError(TAG,"文件打不开!!!!\n",NULL);
     }
    closeFile(f);
}

int char2Int(char c) {
	if (c >= '0' && c <= '9')
		return c - 48;
	else if (c >= 'A' && c <= 'F')
		return c - 55;
	else if (c >= 'a' && c <= 'f')
		return c - 87;
	else
		return -1;
}

char int2Char(int i) {
	if (i >= 0 && i <= 9)
		return i + 48;
	else if (i >= 10 && i <= 15)
		return i + 55;
	else
		return 0;
}

void setAssetManager(AAssetManager* assetMgr,char* filePath) {
	pAssetMgr = assetMgr;
	fileName = filePath;
}
