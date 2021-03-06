package surfExtractor.image_set;

import ij.IJ;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import surfExtractor.TaggedSurfFeature;
import surfExtractor.exceptions.UnsuportedImageException;
import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.struct.feature.SurfFeature;
import boofcv.struct.image.ImageFloat32;

/**
 * @author Hugo
 * 
 */
public class Image {
	/**
	 * Absolute Image path
	 */
	private String absolutePath;
	/**
	 * What class it`s inside
	 */
	private String folder;
	/**
	 * Every feature detected in the Image
	 */
	private ArrayList<SurfFeature> fl = new ArrayList<SurfFeature>();
	/**
	 * log4j object
	 */
	private final static Logger LOGGER = Logger.getLogger(Image.class);

	/**
	 * @param absolutePath - path of the Image
	 * @param folder - the image's class/folder
	 * @throws Exception
	 */
	public Image(String absolutePath, String folder) throws Exception {
		this.absolutePath = absolutePath;
		BufferedImage image = ImageIO.read(new File(absolutePath));
		if (image == null) {
			LOGGER.info("Could not open image: " + absolutePath + " using ImageIO library. Trying again with BoofCV library");
			// image = UtilImageIO.loadImage(absolutePath);
			try {
				image = IJ.openImage(absolutePath).getBufferedImage();
			} catch (Exception e) {
				throw new UnsuportedImageException("Supplied file is not an supported image: " + absolutePath);
			}
			if (image == null) {
				throw new UnsuportedImageException("Supplied file is not an supported image: " + absolutePath);
			}
		}
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
		if (this.folder == "") {
			LOGGER.info("Name is empty");
		}
		return this.folder;
	}

	/**
	 * @param fl - replace the SurfFeature list inside this object
	 */
	public void addFeaturesFromList(ArrayList<SurfFeature> fl) {
		this.fl.addAll(fl);
	}

	/**
	 * @param ddp - add feature from BoofCV SurfFeature list
	 */
	public void addFeaturesFromDDP(DetectDescribePoint<ImageFloat32, SurfFeature> ddp) {
		for (int i = 0; i < ddp.getNumberOfFeatures(); i++) {
			this.addFeature(ddp.getDescription(i));
		}
	}

	/**
	 * @param f - Add single SurfFeature to the list
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
