package surfExtractor.image_set;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import surfExtractor.exceptions.UnsuportedImageException;

/**
 * @author Hugo
 * 
 */
public class ImageClass {

	/**
	 * ImageClass' folder path
	 */
	private String absolutePath;

	/**
	 * Array of it's images
	 */
	private ArrayList<Image> images = new ArrayList<Image>();

	/**
	 * The folder name
	 */
	private String className;

	/**
	 * log4j object
	 */
	private final static Logger LOGGER = Logger.getLogger(ImageClass.class);

	/**
	 * @param absolutePath - path of the ImageClass
	 */
	public ImageClass(String absolutePath) {
		this.absolutePath = absolutePath;
		File f = new File(absolutePath);
		this.className = f.getName();
		getImagesFromPath();
	}

	/**
	 * @param i Manually add new Image to the ImageClass
	 */
	public void addImage(Image i) {
		this.images.add(i);
	}

	/**
	 * @return every Image in this ImageClass
	 */
	public ArrayList<Image> getImages() {
		return this.images;
	}

	/**
	 * Load Images inside ImageClass folder
	 */
	private void getImagesFromPath() {
		File folder = new File(this.absolutePath);
		File[] images = folder.listFiles();
		for (File f : images) {
			if (f.isDirectory()) {
				LOGGER.info("Folders are not supposed to exist inside a image class (" + f.getName() + ").");
			} else {
				try {
					this.images.add(new Image(f.getAbsolutePath(), folder.getName()));
				} catch (UnsuportedImageException ex) {
					LOGGER.info("Could not load image UnsuportedImageException: " + ex.getMessage() + " " + f.getAbsolutePath());
				} catch (Exception ex1) {
					ex1.printStackTrace();
				}
			}
		}
	}

	/**
	 * @return return File object of class absolute path
	 */
	public File getFile() {
		return new File(this.absolutePath);
	}

	/**
	 * @return class name
	 */
	public String getClassName() {
		return this.className;
	}
}
