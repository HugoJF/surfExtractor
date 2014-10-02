package surfExtractor.surf_extractor;

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

public class SurfExtractor {
	private final static Logger LOGGER = Logger.getLogger(SurfExtractor.class);

	/**
	 * Extract features for the entire ImageSet
	 * 
	 * @param is
	 *            - ImageSet to be extracted
	 */
	public void extractImageSet(ImageSet is) {
		LOGGER.info("Starting extraction for ImageSet located at: " + is.getFile().getAbsolutePath());
		for (ImageClass ic : is.getImageClasses()) {
			for (Image i : ic.getImages()) {
				long start = System.currentTimeMillis();
				ImageFloat32 image = UtilImageIO.loadImage(i.getFile().getAbsolutePath(), ImageFloat32.class);
				//LOGGER.info("Extracting image in: " + i.getFile().getAbsolutePath());
				if (image == null) {
					LOGGER.info("NULL Image detected");
				}
				DetectDescribePoint<ImageFloat32, SurfFeature> attributes = easy(image);
				i.addFeaturesFromDDP(attributes);
				//LOGGER.info("Extracted SURF features from: " + i.getFile().getName() + ". Processing time: " + (System.currentTimeMillis() - start));
			}
		}
	}

	/**
	 * @param image
	 * @return SurfFeature list
	 */
	public DetectDescribePoint<ImageFloat32, SurfFeature> easy(ImageFloat32 image) {
		// create the detector and descriptors
		DetectDescribePoint<ImageFloat32, SurfFeature> surf = FactoryDetectDescribe.surfStable(new ConfigFastHessian(0, 2, 200, 2, 9, 4, 4), null, null, ImageFloat32.class);

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
		NonMaxSuppression extractor = FactoryFeatureExtractor.nonmax(new ConfigExtract(2, 0, 5, true));
		FastHessianFeatureDetector<II> detector = new FastHessianFeatureDetector<II>(extractor, 500, 2, 9, 4, 4);

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
}
