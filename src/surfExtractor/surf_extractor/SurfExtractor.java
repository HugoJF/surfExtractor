package surfExtractor.surf_extractor;

import ij.IJ;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import surfExtractor.image_set.Image;
import surfExtractor.image_set.ImageClass;
import surfExtractor.image_set.ImageSet;
import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.extract.ConfigExtract;
import boofcv.abst.feature.detect.extract.NonMaxSuppression;
import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.feature.orientation.OrientationIntegral;
import boofcv.alg.feature.describe.DescribePointSurf;
import boofcv.alg.feature.detect.interest.FastHessianFeatureDetector;
import boofcv.alg.transform.ii.GIntegralImageOps;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.core.image.GeneralizedImageOps;
import boofcv.factory.feature.describe.FactoryDescribePointAlgs;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.factory.feature.detect.extract.FactoryFeatureExtractor;
import boofcv.factory.feature.orientation.FactoryOrientationAlgs;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.feature.ScalePoint;
import boofcv.struct.feature.SurfFeature;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;

/**
 * @author Hugo
 * 
 */
public class SurfExtractor {
	private final static Logger LOGGER = Logger.getLogger(SurfExtractor.class);

	/**
	 * SURF parameters
	 */
	private int radius = 2;
	private float threshold = 0;
	private int ignoreBorder = 5;
	private boolean strictRule = true;
	private int maxFeaturesPerScale = 200;
	private int initialSampleRate = 2;
	private int initialSize = 9;
	private int numberScalesPerOctave = 4;
	private int numberOfOctaves = 4;

	/**
	 * Extract features for the entire ImageSet
	 * 
	 * @param is - ImageSet to be extracted
	 */

	public SurfExtractor(int radius, float threshold, int ignoreBorder, boolean strictRule, int maxFeaturesPerScale, int initialSampleRate, int initialSize, int numberOfScalesPerOctave, int numberOfOctaves) {
		this.radius = radius;
		this.threshold = threshold;
		this.ignoreBorder = ignoreBorder;
		this.strictRule = strictRule;
		this.maxFeaturesPerScale = maxFeaturesPerScale;
		this.initialSampleRate = initialSampleRate;
		this.initialSize = initialSize;
		this.numberScalesPerOctave = numberOfScalesPerOctave;
		this.numberOfOctaves = numberOfOctaves;
	}

	/**
	 * This should be use only when changing some parameters and lefting the
	 * rest default
	 */
	public SurfExtractor() {

	}

	/**
	 * @param is start extraction process for given ImageSet
	 */
	public void extractImageSet(ImageSet is) {
		LOGGER.info("Starting extraction for ImageSet located at: " + is.getFile().getAbsolutePath());
		for (ImageClass ic : is.getImageClasses()) {
			for (Image i : ic.getImages()) {
				@SuppressWarnings("unused")
				long start = System.currentTimeMillis();
				ImageFloat32 image = UtilImageIO.loadImage(i.getFile().getAbsolutePath(), ImageFloat32.class);
				// LOGGER.info("Extracting image in: " +
				// i.getFile().getAbsolutePath());
				if (image == null) {
					LOGGER.info("Reloading image using ImageJ library" + i.getFile().getAbsolutePath());
					BufferedImage imageBuf;
					imageBuf = IJ.openImage(i.getFile().getAbsolutePath()).getBufferedImage();
					image = ConvertBufferedImage.convertFrom(imageBuf, image);
				}
				DetectDescribePoint<ImageFloat32, SurfFeature> attributes = easy(image);
				i.addFeaturesFromDDP(attributes);
				// LOGGER.info("Extracted SURF features from: " +
				// i.getFile().getName() + ". Processing time: " +
				// (System.currentTimeMillis() - start));
			}
		}
	}

	/**
	 * @param image
	 * @return SurfFeature list
	 */
	public DetectDescribePoint<ImageFloat32, SurfFeature> easy(ImageFloat32 image) {
		// create the detector and descriptors
		// DetectDescribePoint<ImageFloat32, SurfFeature> surf =
		// FactoryDetectDescribe.surfStable(new ConfigFastHessian(0, 2, 200, 2,
		// 9, 4, 4), null, null, ImageFloat32.class);
		DetectDescribePoint<ImageFloat32, SurfFeature> surf = FactoryDetectDescribe.surfStable(new ConfigFastHessian(threshold, radius, maxFeaturesPerScale, initialSampleRate, initialSize, numberScalesPerOctave, numberOfOctaves), null, null, ImageFloat32.class);

		// specify the image to process
		surf.detect(image);
		return surf;
	}

	/**
	 * @param image
	 * @return SurfFeature list
	 */
	@SuppressWarnings("rawtypes")
	public <II extends ImageSingleBand> List<SurfFeature> harder(ImageFloat32 image) {
		// SURF works off of integral images
		Class<II> integralType = GIntegralImageOps.getIntegralType(ImageFloat32.class);

		// define the feature detection algorithm

		NonMaxSuppression extractor = FactoryFeatureExtractor.nonmax(new ConfigExtract(radius, threshold, ignoreBorder, strictRule));
		FastHessianFeatureDetector<II> detector = new FastHessianFeatureDetector<II>(extractor, maxFeaturesPerScale, initialSampleRate, initialSize, numberScalesPerOctave, numberOfOctaves);

		// estimate orientation
		OrientationIntegral<II> orientation = FactoryOrientationAlgs.sliding_ii(null, integralType);

		DescribePointSurf<II> descriptor = FactoryDescribePointAlgs.<II> surfStability(null, integralType);

		// compute the integral image of 'image'
		II integral = GeneralizedImageOps.createSingleBand(integralType, image.width, image.height);
		GIntegralImageOps.transform(image, integral);

		// detect fast hessian features
		detector.detect(integral);
		// tell algorithms which image to process
		orientation.setImage(integral);
		descriptor.setImage(integral);

		List<ScalePoint> points = detector.getFoundPoints();

		List<SurfFeature> descriptions = new ArrayList<SurfFeature>();

		for (ScalePoint p : points) {
			// estimate orientation
			orientation.setScale(p.scale);
			double angle = orientation.compute(p.x, p.y);

			// extract the SURF description for this region
			SurfFeature desc = descriptor.createDescription();
			descriptor.describe(p.x, p.y, angle, p.scale, desc);

			// save everything for processing later on
			descriptions.add(desc);

		}
		return descriptions;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public int getIgnoreBorder() {
		return ignoreBorder;
	}

	public void setIgnoreBorder(int ignoreBorder) {
		this.ignoreBorder = ignoreBorder;
	}

	public boolean isStrictRule() {
		return strictRule;
	}

	public void setStrictRule(boolean strictRule) {
		this.strictRule = strictRule;
	}

	public int getMaxFeaturesPerScale() {
		return maxFeaturesPerScale;
	}

	public void setMaxFeaturesPerScale(int maxFeaturesPerScale) {
		this.maxFeaturesPerScale = maxFeaturesPerScale;
	}

	public int getInitialSampleRate() {
		return initialSampleRate;
	}

	public void setInitialSampleRate(int initialSampleRate) {
		this.initialSampleRate = initialSampleRate;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public int getNumberScalesPerOctave() {
		return numberScalesPerOctave;
	}

	public void setNumberScalesPerOctave(int numberScalesPerOctave) {
		this.numberScalesPerOctave = numberScalesPerOctave;
	}

	public int getNumberOfOctaves() {
		return numberOfOctaves;
	}

	public void setNumberOfOctaves(int numberOfOctaves) {
		this.numberOfOctaves = numberOfOctaves;
	}

}
