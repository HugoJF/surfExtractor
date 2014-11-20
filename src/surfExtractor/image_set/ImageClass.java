package surfExtractor.image_set;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * @author Hugo
 * 
 */
public class ImageClass {

	private String absolutePath;

	private ArrayList<Image> images = new ArrayList<Image>();

	private String className;

	private final static Logger LOGGER = Logger.getLogger(ImageClass.class);

	/**
	 * @param absolutePath
	 *            - path of the ImageClass
	 */
	public ImageClass(String absolutePath) {
		this.absolutePath = absolutePath;
		File f = new File(absolutePath);
		this.className = f.getName();
		getImagesFromPath();
	}

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
				} catch (Exception ex) {
					LOGGER.info("Detected file that is not an image: " + f.getAbsolutePath());
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
