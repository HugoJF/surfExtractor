package surf_extractor;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.struct.feature.SurfFeature;
import boofcv.struct.image.ImageFloat32;

/**
 * @author Hugo
 * 
 */
public class Image {
	private String absolutePath;
	private String folder;
	private ArrayList<SurfFeature> fl = new ArrayList<SurfFeature>();
	private final static Logger LOGGER = Logger.getLogger(Image.class);

	/**
	 * @param absolutePath
	 *            - path of the Image
	 * @param folder
	 *            - the image's class/folder
	 */
	public Image(String absolutePath, String folder) {
		this.absolutePath = absolutePath;
		this.folder = folder;
	}

	/**
	 * @return - File object representation of the Image
	 */
	public File getFile() {
		return new File(this.absolutePath);
	}

	/**
	 * @return - the image's class/folder
	 */
	public String getFolderName() {
		if(this.folder == "") {
			LOGGER.info("Name is empty");
		}
		return this.folder;
	}

	/**
	 * @param fl
	 *            - replace the SurfFeature list inside this object
	 */
	public void addFeaturesFromList(ArrayList<SurfFeature> fl) {
		this.fl.addAll(fl);
	}

	/**
	 * @param ddp
	 *            - add feature from BoofCV SurfFeature list
	 */
	public void addFeaturesFromDDP(DetectDescribePoint<ImageFloat32, SurfFeature> ddp) {
		for (int i = 0; i < ddp.getNumberOfFeatures(); i++) {
			this.addFeature(ddp.getDescription(i));
		}
	}

	/**
	 * @param f
	 *            - Add single SurfFeature to the list
	 */
	public void addFeature(SurfFeature f) {
		this.fl.add(f);
	}

	/**
	 * @return - Return image SurfFeature list
	 */
	public ArrayList<SurfFeature> getFeatures() {
		return this.fl;
	}

	/**
	 * @return Return TaggedSurfFeature list
	 */
	public ArrayList<TaggedSurfFeature> getTaggedFeatures() {
		ArrayList<TaggedSurfFeature> tsf = new ArrayList<TaggedSurfFeature>();
		for (SurfFeature sf : this.fl) {
			tsf.add(new TaggedSurfFeature(this.absolutePath, this.folder, sf));
		}

		return tsf;
	}
}
