package bow_classifier;

import java.util.ArrayList;

import misc.Utils;
import boofcv.struct.feature.SurfFeature;
import clustering.Cluster;
import surf_extractor.Image;
import surf_extractor.ImageClass;
import surf_extractor.ImageSet;

public class Bow {
	private ImageSet imageSet;
	private ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	private ArrayList<Histogram> histogram = new ArrayList<Histogram>();

	/**
	 * @param is
	 *            ImageSet being classified
	 * @param clusters
	 *            - clusters after clustering
	 */
	public Bow(ImageSet is, ArrayList<Cluster> clusters) {
		this.imageSet = is;
		this.clusters = clusters;
		int i = 0;
		for (Cluster c : clusters) {
			i++;
			c.setId(i);

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

}
