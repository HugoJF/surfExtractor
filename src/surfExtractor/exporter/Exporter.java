package surfExtractor.exporter;

import org.apache.log4j.Logger;

import surfExtractor.image_set.ImageSet;

public abstract class Exporter {
	
	/**
	 * The ImageSet used to export the attributes
	 */
	protected ImageSet imageSet;
	
	protected Logger LOGGER = Logger.getLogger(Exporter.class);
	
	/**
	 * @param is - sets a new ImageSet
	 */
	public void setImageSet(ImageSet is) {
		this.imageSet = is;
	}
	
	/**
	 * @return the ImageSet being used
	 */
	public ImageSet getImageSet() {
		return this.imageSet;
	}
	
	/**
	 * Method for processing results into a file
	 */
	public abstract void export();
}
