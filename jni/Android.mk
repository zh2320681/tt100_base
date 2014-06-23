LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_LDLIBS := -llog -landroid

LOCAL_MODULE    := ZWTool
LOCAL_SRC_FILES := mainNDK.c app.c secretFileOpt.c 

include $(BUILD_SHARED_LIBRARY)
