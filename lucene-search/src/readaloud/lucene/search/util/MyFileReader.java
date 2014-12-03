package readaloud.lucene.search.util;

import java.io.File;


/**
 *
 * @author Gaurav Bhardwaj 
 * This support class will get the directory path where all files are stored for indexing
 * and will return array of file list
 */
public class MyFileReader {
    
    private String files_to_index ;

    
    public MyFileReader(String files_to_index) {
        this.files_to_index = files_to_index;
    }
    
    public File[] getFileForReading(){
        System.out.println(files_to_index);
       File dir = new File(files_to_index);
       File[] files = dir.listFiles(); 
       return files; 
                
    }
            
}
