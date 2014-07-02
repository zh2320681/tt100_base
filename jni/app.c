/*
 * app.c
 *
 *  Created on: 2014年6月23日
 *      Author: shrek
 */

#include "app.h"

int isDebug = YES;

void printInfo(const char * TAG,const char* info,void* para){
	if(isDebug == YES){
		__android_log_print(ANDROID_LOG_INFO,TAG,info,para);
	}
}

void printError(const char * TAG,const char* info,void* para){
	if(isDebug == YES){
		__android_log_print(ANDROID_LOG_ERROR,TAG,info,para);
	}
}
