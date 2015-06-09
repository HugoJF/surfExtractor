package surfExtractor.bow_classifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import surfExtractor.image_set.Image;
import surfExtractor.image_set.ImageClass;
import surfExtractor.image_set.ImageSet;
import surfExtractor.misc.Utils;
import boofcv.struct.feature.SurfFeature;
import surfExtractor.clustering.Cluster;
import surfExtractor.clustering.Clustering;

public class Bow {
	/**
	 * The image set being BOW'ed
	 */
	private surfExtractor.image_set.ImageSet imageSet;
	/**
	 * Array of final clusters
	 */
	private ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	/**
	 * List of histograms being generated
	 */
	private ArrayList<Histogram> histogram = new ArrayList<Histogram>();

	/**
	 * The log4j object
	 */
	private final static Logger LOGGER = Logger.getLogger(Bow.class);

	/**
	 * @param is ImageSet being classified
	 * @param clusters - clusters array after clustering
	 */
	public Bow(ImageSet is, ArrayList<Cluster> clusters) {
		this.imageSet = is;
		this.clusters = clusters;
	}

	/**
	 * @param is - ImageSet being classified
	 * @param clusters - File object of exported clusters
	 */
	public Bow(ImageSet is, File pathToClusters) {
		this.imageSet = is;
		try {
			this.clusters = Clustering.getClustersFromFile(pathToClusters);
		} catch (IOException e) {
			LOGGER.info("Error loading clusters file");
			e.printStackTrace();
		}
	}

	/**
	 * Start creating frequency histograms for each image
	 */
	public void computeHistograms() {

		for (ImageClass is : imageSet.getImageClasses()) {
			for (Image i : is.getImages()) {
				Histogram h = new Histogram(this.clusters.size());
				h.setFolderName(i.getFolderName());
				for (SurfFeature sf : i.getFeatures()) {
					Cluster c = getClosestCluster(sf);
					h.setValue(c.getId() - 1, h.getValue(c.getId() - 1) + 1);
				}
				histogram.add(h);
			}
		}
	}

	/**
	 * @return computed histograms
	 */
	public ArrayList<Histogram> getHistograms() {
		return this.histogram;
	}

	/**
	 * Finds the closest clusters to a Feature
	 * 
	 * @param sf - The feature to find its closest clusters
	 * @return The closest cluster to the feature
	 */
	private Cluster getClosestCluster(SurfFeature sf) {
		double closestDistance = 0;
		Cluster closestCluster = null;
		for (Cluster c : clusters) {
			if (closestCluster == null) {
				closestCluster = c;
				closestDistance = Utils.getFeatureDistanceFromCluster(sf, c);
				continue;
			}
			double d = Utils.getFeatureDistanceFromCluster(sf, c);
			if (d < closestDistance) {
				closestCluster = c;
				closestDistance = d;
			}
		}

		return closestCluster;
	}

	/**
	 * @return Amount of clusters created
	 */
	public int getClusterNum() {
		return this.clusters.size();
	}

	/**
	 * @return Array of max values for each attribute
	 */
	public double[] getMaxValues() {
		double[] maxVals = new double[this.histogram.get(0).getSize()];
		
		for(int i = 0; i < this.histogram.size(); i++) {
			for(int j = 0; j < this.histogram.get(0).getHistogram().length; j++) {
				if(maxVals[j] < this.histogram.get(i).getValue(j)) {
					maxVals[j] = this.histogram.get(i).getValue(j);
				}
			}
		}
		
		return null;
	}

}
