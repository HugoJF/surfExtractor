package surfExtractor.exporter;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import surfExtractor.bow_classifier.Bow;
import surfExtractor.image_set.ImageSet;

public abstract class Exporter {
	/**
	 * The name
	 */
	protected String name;
	/**
	 * What ImageSet we are exporting
	 */
	protected ImageSet imageSet;
	/**
	 * BOW information
	 */
	protected Bow bow;
	
	
	protected String path;
	
	
	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public abstract void export();


	public ImageSet getImageSet() {
		return imageSet;
	}


	public void setImageSet(ImageSet imageSet) {
		this.imageSet = imageSet;
	}


	public Bow getBow() {
		return bow;
	}


	public void setBow(Bow bow) {
		this.bow = bow;
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	
	
}
