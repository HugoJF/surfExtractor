package misc;

import boofcv.struct.feature.SurfFeature;
import clustering.Cluster;

public class Utils {
	/**
	 * @return distance between two multi-dimensional double arrays
	 * @throws Exception
	 */
	public static double getVectorDistance(double[] a, double[] b) throws RuntimeException {
		if (a.length != b.length)
			throw new RuntimeException("Vectors must have the same dimensional space");
		double sum = 0;
		double x, y;
		 for (int i = 0; i < a.length; i++) {
		//for (int i = a.length - 1; i >= 0; i--) {
			// sum += Math.pow(a[i] - b[i], 2);
			sum += ((a[i] - b[i]) * (a[i] - b[i]));

		}
		return Math.sqrt(sum);
	}

	/**
	 * @param f
	 *            - the feature to calculate the distance
	 * @param c
	 *            - the cluster to calculate the distance
	 * @return the 'distance' between TaggedSurfFeature f and Cluster c centroid
	 */
	public static double getFeatureDistanceFromCluster(SurfFeature f, Cluster c) {
		double[] a = f.getValue();
		double[] b = c.getCentroid();

		return Utils.getVectorDistance(a, b);
	}

	/**
	 * @return sum two multidimensional double vectors
	 */
	public static double[] vectorSum(double[] a, double[] b) {
		double[] sum = new double[64];
		for (int i = 0; i < a.length; i++) {
			sum[i] = a[i] + b[i];
		}

		return sum;
	}

	/**
	 * @return multiply multidimensional double vector a by scalar b
	 */
	public static double[] vectorMult(double[] a, double b) {
		double[] c = a;
		for (int i = 0; i < c.length; i++) {
			c[i] *= b;
		}
		return c;
	}

	/**
	 * @return divide multidimensional double vector by scalar b
	 */
	public static double[] vectorDiv(double[] a, double b) {
		double[] c = a;
		for (int i = 0; i < c.length; i++) {
			c[i] /= b;
		}
		return c;
	}

}
