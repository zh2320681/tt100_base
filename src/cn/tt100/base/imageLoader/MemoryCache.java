package cn.tt100.base.imageLoader;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.util.Log;
import cn.tt100.base.util.BaseLog;

public class MemoryCache {
	private static final String TAG = "MemoryCache";
    private Map<String, Bitmap> cache=Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10,1.5f,true));
    private long size=0;//current allocated size
    private long limit = 1024*1024; //����ڴ�����  1M
    
    public MemoryCache(){
        //use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory()/4);
    }
    
    public void setLimit(long new_limit){
        limit=new_limit;
        BaseLog.printLog(MemoryCache.this, "�ڴ����ƵĴ�С��: "+limit/1024./1024.+"MB");
    }

    /**
     * �ӻ����еõ�Bitmap
     * @param id
     * @return
     */
    public Bitmap get(String id){
        try{
            if(!cache.containsKey(id)){
                return null;
            }
            return cache.get(id);
        }catch(NullPointerException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void put(String id, Bitmap bitmap){
        try{
            if(cache.containsKey(id)){
            	 size-=getSizeInBytes(cache.get(id));
            }
            cache.put(id, bitmap);
            size+=getSizeInBytes(bitmap);
            checkSize();
        }catch(Throwable th){
            th.printStackTrace();
        }
    }
    
    /**
     * ����ڴ滺�� ��û�г�������
     * ��������  ��ȡ�����һ��ɾ��
     */
    private void checkSize() {
        Log.i(TAG, "cache size="+size+" length="+cache.size());
        if(size>limit){
            Iterator<Entry<String, Bitmap>> iter=cache.entrySet().iterator();//least recently accessed item will be the first one iterated  
            while(iter.hasNext()){
                Entry<String, Bitmap> entry=iter.next();
                size-=getSizeInBytes(entry.getValue());
                iter.remove();
//                entry.getValue().recycle();
//                if(delegate != null){
//                	delegate.recycleImage(entry.getKey(),entry.getValue());
//                }
                if(size<=limit)
                    break;
            }
            Log.i(TAG, "Clean cache. New size "+cache.size());
        }
    }

    public void clear() {
        try{
            cache.clear();
            size=0;
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }
    }

    /**
     * ����ͼƬ�Ĵ�С
     * @param bitmap
     * @return
     */
    long getSizeInBytes(Bitmap bitmap) {
        if(bitmap==null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
