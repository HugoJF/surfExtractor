package surfExtractor.surf_extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import surfExtractor.image_set.ImageSet;
import configuration.*;

import org.apache.log4j.Logger;

import surfExtractor.user_interface.UserInterface;
import surfExtractor.clustering.Cluster;
import surfExtractor.clustering.Clustering;
import surfExtractor.exporter.Exporter;
import surfExtractor.bow_classifier.Bow;
import surfExtractor.bow_classifier.Histogram;

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
		Configuration.setConfiguration("random.seed", "1");
		Configuration.readFromRunArgs(args);

		if (Configuration.validParametersSet == 0) {
			// Open UserInterface, and wait input from user
			UserInterface.initialize();
			UserInterface.start();
			UserInterface.hold();
			UserInterface.setConfiguration();
		}

		// Print loaded configuration
		Configuration.debugParameters();
		long start = System.currentTimeMillis();
		try {
			Main m = new Main();
			m.run();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while extracting imageset, execute application via command line to see stack trace");
			e.printStackTrace();
		}
		long duration = System.currentTimeMillis() - start;

		// Goes through the process of ending extraction
		UserInterface.done();
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
	
	public void run() throws FileNotFoundException, UnsupportedEncodingException {

		// Load images from ImageSet
		ImageSet is = new ImageSet(Configuration.getConfiguration("imageset.path"));

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
		clustering.cluster();

		//Export clusters
		LOGGER.info("Saving clusters to file");
		clustering.saveClustersToFile(new File("C:\\clusters"));
		
		// Return final clusters
		ArrayList<Cluster> featureCluster = clustering.getClusters();

		// Load Bag Of Words classifier
		Bow bow = new Bow(is, featureCluster);

		// Compute frequency histograms
		bow.computeHistograms();

		// Return frequency histograms
		ArrayList<Histogram> h = bow.getHistograms();

		// Debug histograms
		/*
		 * LOGGER.info("Debugging image histograms"); for (Histogram hh : h) {
		 * LOGGER.info("Histogram: " + histogramToString(hh)); }
		 */

		// Write experimental arff
		Exporter exporter = new Exporter(is, bow);
		exporter.generateArffFile(Configuration.getConfiguration("arff.path"));
	}
	
	public void generateArff(String imagesetPath, int kmeansK, int kmeansIterations, String arffRelation, String arffPath) throws FileNotFoundException, UnsupportedEncodingException {

		// Load images from ImageSet
		ImageSet is = new ImageSet(imagesetPath);

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
		ArrayList<Histogram> h = bow.getHistograms();

		// Debug histograms
		/*
		 * LOGGER.info("Debugging image histograms"); for (Histogram hh : h) {
		 * LOGGER.info("Histogram: " + histogramToString(hh)); }
		 */

		// Write experimental arff
		Exporter exporter = new Exporter(is, bow);
		exporter.generateArffFile(arffPath);
	}
}
