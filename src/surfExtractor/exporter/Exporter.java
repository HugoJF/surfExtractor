package surfExtractor.exporter;

import org.apache.log4j.Logger;

import surfExtractor.image_set.ImageSet;

public abstract class Exporter {
	
	protected ImageSet imageSet;
	
	protected Logger LOGGER = Logger.getLogger(Exporter.class);
	
	public void setImageSet(ImageSet is) {
		this.imageSet = is;
	}
	
	public ImageSet getImageSet() {
		return this.imageSet;
	}
	
	public abstract void export();
}
