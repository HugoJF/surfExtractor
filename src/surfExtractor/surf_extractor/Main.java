package surfExtractor.surf_extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import surfExtractor.image_set.ImageSet;
import configuration.*;

import org.apache.log4j.Logger;

import surfExtractor.clustering.Cluster;
import surfExtractor.clustering.Clustering;
import surfExtractor.exporter.Exporter;
import surfExtractor.bow_classifier.Bow;

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
	public static void main(String[] args) {
		Configuration.addNewValidParameter("imageset.path", true);
		Configuration.addNewValidParameter("imageset.relation", true);
		Configuration.addNewValidParameter("random.seed", false);
		Configuration.addNewValidParameter("arff.relation", false);
		Configuration.addNewValidParameter("arff.path", true);
		Configuration.addNewValidParameter("kmeans.iteration", true);
		Configuration.addNewValidParameter("kmeans.kvalue", true);
		Configuration.addNewValidParameter("cluster.save_path", false);
		Configuration.addNewValidParameter("cluster.load_path", false);

		Configuration.setConfiguration("random.seed", "1");

		Configuration.readFromRunArgs(args);

		try {
			// Check if we have enought parameters to start
			Configuration.verifyArgs();
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		/*
		 * Moved gui to another project if (Configuration.getCommand("gui") ==
		 * true) { // Open UserInterface, and wait input from user
		 * UserInterface.initialize(); UserInterface.start();
		 * UserInterface.hold(); UserInterface.setConfiguration(); }
		 */

		// Print loaded configuration
		Configuration.debugParameters();

		// Time extraction process started
		long start = System.currentTimeMillis();

		try {
			Main m = new Main();
			m.run();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		long duration = System.currentTimeMillis() - start;

		// Goes through the process of ending extraction
		// Moved interface to another project
		// UserInterface.done();
		LOGGER.info("Duration of the process: " + (duration / 1000) + " seconds.");
	}

	public Main() {

	}

	/**
	 * Main class object
	 * 
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */

	public void run() {

		// Load images from ImageSet
		ImageSet is;
		try {
			is = new ImageSet(Configuration.getConfiguration("imageset.path"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		is.setRelation(Configuration.getConfiguration("imageset.relation"));

		// Create clustering object
		Clustering clustering = new Clustering(is, Integer.valueOf(Configuration.getConfiguration("kmeans.kvalue")), Integer.valueOf(Configuration.getConfiguration("kmeans.iteration")));

		// Set Dataset 'name'
		is.setRelation(Configuration.getConfiguration("arff.relation"));

		// Create SURF Feature extractor objects
		SurfExtractor surfExtractor = new SurfExtractor();

		// Load images from ImageSet
		is.getImageClasses();

		// Use surfExtractor to extract SURF features
		surfExtractor.extractImageSet(is);

		// Debug feature number for each image
		/*
		 * for (ImageClass ic : is.getImageClasses()) { for (Image i :
		 * ic.getImages()) { LOGGER.info(i.getFeatures().size() +
		 * " SURF features detected for: " + i.getFile().getName()); } }
		 */

		// Cluster all features
		if (Configuration.getConfiguration("cluster.load_path") != null) {
			try {
				clustering.loadClustersFromFile(new File(Configuration.getConfiguration("cluster.load_path")));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} else {
			clustering.cluster();
		}

		// Export clusters
		if (Configuration.getConfiguration("cluster.save_path") != null) {
			LOGGER.info("Saving clusters to file");
			try {
				clustering.saveClustersToFile(new File(Configuration.getConfiguration("clusters.save_path")));
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			}
		}

		// Return final clusters
		ArrayList<Cluster> featureCluster = clustering.getClusters();

		// Load Bag Of Words classifier
		Bow bow = new Bow(is, featureCluster);

		// Compute frequency histograms
		bow.computeHistograms();

		// Return frequency histograms
		// ArrayList<Histogram> h = bow.getHistograms();

		// Debug histograms
		/*
		 * LOGGER.info("Debugging image histograms"); for (Histogram hh : h) {
		 * LOGGER.info("Histogram: " + histogramToString(hh)); }
		 */

		// Write experimental arff
		Exporter exporter = new Exporter(is, bow);
		try {
			exporter.generateArffFile(Configuration.getConfiguration("arff.path"));
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * 
	 * @param imagesetPath
	 * @param kmeansK
	 * @param kmeansIterations
	 * @param arffRelation
	 * @param arffPath
	 */
	public void generateArff(String imagesetPath, int kmeansK, int kmeansIterations, String arffRelation, String arffPath) {

		// Load images from ImageSet
		ImageSet is = null;
		try {
			is = new ImageSet(imagesetPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		// Create clustering object
		Clustering clustering = new Clustering(is, kmeansK, kmeansIterations);

		// Set Dataset 'name'
		is.setRelation(arffRelation);

		// Create SURF Feature extractor objects
		SurfExtractor surfExtractor = new SurfExtractor();

		// Load images from ImageSet
		is.getImageClasses();

		// Use surfExtractor to extract SURF features
		surfExtractor.extractImageSet(is);

		// Debug feature number for each image
		/*
		 * for (ImageClass ic : is.getImageClasses()) { for (Image i :
		 * ic.getImages()) { LOGGER.info(i.getFeatures().size() +
		 * " SURF features detected for: " + i.getFile().getName()); } }
		 */

		// Cluster all features
		clustering.cluster();

		// Return final clusters
		ArrayList<Cluster> featureCluster = clustering.getClusters();

		// Load Bag Of Words classifier
		Bow bow = new Bow(is, featureCluster);

		// Compute frequency histograms
		bow.computeHistograms();

		// Return frequency histograms
		// ArrayList<Histogram> h = bow.getHistograms();

		// Debug histograms
		/*
		 * LOGGER.info("Debugging image histograms"); for (Histogram hh : h) {
		 * LOGGER.info("Histogram: " + histogramToString(hh)); }
		 */

		// Write experimental arff
		Exporter exporter = new Exporter(is, bow);
		try {
			exporter.generateArffFile(arffPath);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
	}
}
