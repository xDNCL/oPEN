package System;

import java.io.File;
import java.io.FilenameFilter;

public class FileFilter implements FilenameFilter{
	@Override
	public boolean accept(File dir, String name) {
		if(name.matches(".*\\.xml$")){
			return true;
		}
		return false;
	}
} 
