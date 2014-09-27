package surfExtractor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import misc.Configuration;

import org.apache.log4j.Logger;

import clustering.Cluster;
import clustering.Clustering;
import bowClassifier.Bow;
import bowClassifier.Histogram;

/**
 * @author Hugo
 * 
 *         Main class
 */
public class Main {
	private final static Logger LOGGER = Logger.getLogger(Main.class);

	/**
	 * @param args
	 *            - Run configuration parameters
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Configuration.readFromRunArgs(args);
		Configuration.debugParameters();
		long start = System.currentTimeMillis();
		new Main();
		long duration = System.currentTimeMillis() - start;
		LOGGER.info("Duration of the process: " + (duration / 1000) + " seconds.");
	}

	/**
	 * Main class object
	 * 
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public Main() throws FileNotFoundException, UnsupportedEncodingException {

		// Load images from ImageSet
		ImageSet is = new ImageSet(Configuration.getConfiguration("imageset.path"));

		// Hardcoded configuration
		Clustering clustering = new Clustering(is, Integer.valueOf(Configuration.getConfiguration("kmeans.kvalue")), Integer.valueOf(Configuration.getConfiguration("kmeans.iteration")));
		is.setRelation(Configuration.getConfiguration("arff.relation"));
		SurfExtractor surfExtractor = new SurfExtractor();

		is.getImageClasses();

		// Use surfExtractor to extract SURF features
		surfExtractor.extractImageSet(is);

		// Debug feature number for each image
		/*for (ImageClass ic : is.getImageClasses()) {
			for (Image i : ic.getImages()) {
				LOGGER.info(i.getFeatures().size() + " SURF features detected for: " + i.getFile().getName());
			}
		}*/

		// Cluster all features
		clustering.cluster();

		// Return final clusters
		ArrayList<Cluster> featureCluster = clustering.getClusters();

		// Load Bag Of Words classifier
		Bow bow = new Bow(is, featureCluster);

		// Compute frequency histograms
		bow.computeHistograms();

		// Return frequency histograms
		ArrayList<Histogram> h = bow.getHistograms();

		// Debug histograms
		/*LOGGER.info("Debugging image histograms");
		for (Histogram hh : h) {
			LOGGER.info("Histogram: " + histogramToString(hh));
		}*/

		// Write experimental arff
		PrintWriter writer = new PrintWriter(Configuration.getConfiguration("arff.path") + Configuration.getConfiguration("arff.filename.prefix") + is.getRelation() + Configuration.getConfiguration("arff.filename.sufix"), "UTF-8");
		writer.println("@relation " + Configuration.getConfiguration("arff.relation"));
		writer.println();
		for (int i = 0; i < bow.getClusterNum(); i++) {
			writer.println("@attribute A" + i + " numeric");
		}
		writer.print("@attribute class {");
		for (int i = 0; i < is.getImageClasses().size(); i++) {
			writer.print(is.getImageClasses().get(i).getFile().getName());
			if (i != is.getImageClasses().size() - 1) {
				writer.print(", ");
			}
		}
		writer.print("}");
		writer.println();
		writer.println("@data");
		for (Histogram hh : h) {
			hh.normalize();
			writer.println(hh.toString());
		}
		writer.close();
	}

	/**
	 * @param h
	 *            - Histogram object
	 * @return String representation of the histogram h
	 */
	public String histogramToString(Histogram h) {
		String s = "[";
		for (int i = 0; i < h.getSize(); i++) {
			s += h.getValue(i) + ", ";
		}
		s += "]";

		return s;
	}
}
