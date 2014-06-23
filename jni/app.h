/*
 * app.c
 *
 *  Created on: 2014年6月20日
 *      Author: shrek
 */
#include <jni.h>
#include <android/log.h>
#include <stdarg.h>

#define YES 1
#define NO 0

extern int isDebug ;

#ifndef APP_C_
#define APP_C_


void printInfo(const char * TAG,const char* info, void* para);

void printError(const char * TAG,const char* info, void* para);


#endif /* APP_C_ */
