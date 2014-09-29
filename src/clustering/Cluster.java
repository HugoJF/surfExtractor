package clustering;

import java.util.ArrayList;
import java.util.Random;

import misc.Utils;

import org.apache.log4j.Logger;

import surf_extractor.TaggedSurfFeature;

public class Cluster {
	private double[] centroid;

	private ArrayList<TaggedSurfFeature> children = new ArrayList<TaggedSurfFeature>();

	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(Cluster.class);

	private int id;

	/**
	 * @param centroid
	 *            Cluster object with supplied centroid
	 */
	public Cluster(double[] centroid) {
		this.centroid = centroid;
	}

	/**
	 * Cluster object with random centroid
	 */
	public Cluster() {
		double[] c = new double[64];
		Random rand = new Random();
		for (int i = 0; i < 64; i++) {
			c[i] = rand.nextDouble();
		}
		this.centroid = c;
	}

	/**
	 * @param i
	 *            - Unique identification number
	 */
	public void setId(int i) {
		this.id = i;
	}

	/**
	 * @return the unique identification number
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Removes all features inside this cluster
	 */
	public void clearChildren() {
		this.children = new ArrayList<TaggedSurfFeature>();
	}

	/**
	 * @param f
	 *            - TaggedSurfFeature to add inside cluster
	 */
	public void addChild(TaggedSurfFeature f) {
		this.children.add(f);
	}

	/**
	 * Recalculates centroid based on the feature's average value
	 */
	public void recalculateCentroid() {
		if (children.size() != 0) {

			double[] newCentroid = new double[64];
			for (TaggedSurfFeature tsf : children) {
				newCentroid = Utils.vectorSum(tsf.getFeature().value, newCentroid);
			}
			newCentroid = Utils.vectorDiv(newCentroid, children.size());
			this.centroid = newCentroid;
		}
		this.children.clear();
	}

	/**
	 * Recalculates centroid based on the feature's average value
	 * @return the difference in position from old to new position
	 */
	public double recalculateCentroidWithDifference() {
		double delta = 0;
		if (children.size() != 0) {

			double[] newCentroid = new double[64];
			for (TaggedSurfFeature tsf : children) {
				newCentroid = Utils.vectorSum(tsf.getFeature().value, newCentroid);
			}
			newCentroid = Utils.vectorDiv(newCentroid, children.size());
			delta = Utils.getVectorDistance(this.centroid, newCentroid);
			this.centroid = newCentroid;
		}
		this.children.clear();
		
		return delta;
	}

	/**
	 * @return return cluster centroid
	 */
	public double[] getCentroid() {
		return this.centroid;
	}

	/**
	 * @return return the number of features inside this cluster
	 */
	public int getChildrenCount() {
		return this.children.size();
	}

}
