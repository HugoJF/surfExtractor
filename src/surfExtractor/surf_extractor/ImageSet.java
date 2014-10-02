package surfExtractor.surf_extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * @author Hugo
 * 
 */
public class ImageSet {

	private String path;
	
	private String relation;

	private ArrayList<ImageClass> ic = new ArrayList<ImageClass>();

	private final static Logger LOGGER = Logger.getLogger(ImageSet.class);

	/**
	 * @param path
	 *            - path containing the ImageSet
	 * @throws FileNotFoundException
	 */
	public ImageSet(String path) throws FileNotFoundException {
		// Save image path
		this.path = path;

		// Validate path
		File basePath = new File(path);
		if (!basePath.exists())
			throw new FileNotFoundException("Invalid path to ImageSet");

		// Get files
		getFilesFromPath();
	}

	/**
	 * Loads every ImageClass(folder) from ImageSet
	 */
	private void getFilesFromPath() {
		File directory = new File(this.path);
		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				LOGGER.info("Files are not supposed to be in the base ImageSet path, ignoring");
			} else if (file.isDirectory()) {
				this.ic.add(new ImageClass(file.getAbsolutePath()));
				LOGGER.info("Creating new ImageClass: " + file.getName());
			}
		}
	}

	/**
	 * @return ArrayList containing all loaded ImageClasses
	 */
	public ArrayList<ImageClass> getImageClasses() {
		return this.ic;
	}

	/**
	 * @return File object representation of ImageSet folder
	 */
	public File getFile() {
		return new File(this.path);
	}

	public void setRelation(String clusterAmount) {
		this.relation = clusterAmount;
		
	}
	
	public String getRelation() {
		return this.relation;
	}
}
