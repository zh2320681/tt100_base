package cn.tt100.base.imageLoader;

import java.io.File;

/**
 * �ļ�����
 * @author shrek
 *
 */
public class FileCache {
private File cacheDir;
	
    public FileCache(File cacheDir){
       this.cacheDir = cacheDir;
    }
    
    /**
     * �õ��ļ�����
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
     * ɾ������ �ļ����������ļ�
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
