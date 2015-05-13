package surfExtractor.exporter;

import java.io.File;

public abstract class FileExporter extends Exporter{
	
	protected String path;
	
	protected File file;
	
	public void setPath(String path) {
		this.path = path;
		this.file = new File(path);
	}
	
	public void setFile(File file) {
		this.file = file;
		this.path = file.getAbsolutePath();
	}
	public String getPath() {
		return this.path;
	}
	
	public File getFile() {
		if(file == null) {
			this.file = new File(this.path);
		}
		
		return this.file;
	}
}
