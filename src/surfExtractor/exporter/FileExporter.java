package surfExtractor.exporter;

import java.io.File;

public abstract class FileExporter extends Exporter {

	/**
	 * Where to save the resulting file
	 */
	protected String path;

	/**
	 * File object of final path
	 */
	protected File file;

	/**
	 * Set a new path
	 * 
	 * @param path - absolute path
	 */
	public void setPath(String path) {
		this.path = path;
		this.file = new File(path);
	}

	/**
	 * Set a new result path using File object
	 * 
	 * @param file
	 */
	public void setFile(File file) {
		this.file = file;
		this.path = file.getAbsolutePath();
	}

	/**
	 * @return - current path used to save results file
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * @return - File object of path used to save results file
	 */
	public File getFile() {
		if (file == null) {
			this.file = new File(this.path);
		}

		return this.file;
	}
}
