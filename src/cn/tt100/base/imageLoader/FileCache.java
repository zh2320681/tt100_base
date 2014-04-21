package cn.tt100.base.imageLoader;

import java.io.File;

/**
 * 文件缓存
 * @author shrek
 *
 */
public class FileCache {
private File cacheDir;
	
    public FileCache(File cacheDir){
       this.cacheDir = cacheDir;
    }
    
    /**
     * 得到文件对象
     * @param url
     * @return
     */
    public File getFile(String url){
    	int index = url.lastIndexOf(".");
    	StringBuffer houzui = new StringBuffer(); 
    	if(index != -1){
    		houzui.append(url.substring(index, url.length())) ;
    	}
        String filename=String.valueOf(url.hashCode())+houzui.toString();
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    /**
     * 删除缓存 文件夹下所有文件
     */
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files){
        	f.delete();
        }  
    }
}
