package surfExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import surfExtractor.image_set.ImageSet;
import configuration.Configuration;

import org.apache.log4j.Logger;

import surfExtractor.clustering.Cluster;
import surfExtractor.clustering.Clustering;
import surfExtractor.exporter.InstanceGenerator;
import surfExtractor.exporter.WekaExporter;
import surfExtractor.GUI.UserInterface;
import surfExtractor.bow_classifier.Bow;
import weka.core.Instances;

/**
 * @author Hugo
 * 
 *         Main class
 */
public class SurfExtractor {
	private final static Logger LOGGER = Logger.getLogger(SurfExtractor.class);

	/**
	 * @param args - Run configuration parameters
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) {
		//Manually prepare the Configuration
		Configuration config = new Configuration();
		
		config.addNewValidParameter("imageset.path", true);
		config.addNewValidParameter("imageset.relation", true);
		config.addNewValidParameter("random.seed", false);
		config.addNewValidParameter("arff.relation", false);
		config.addNewValidParameter("arff.path", true);
		config.addNewValidParameter("kmeans.iteration", true);
		config.addNewValidParameter("kmeans.kvalue", true);
		config.addNewValidParameter("cluster.save_path", false);
		config.addNewValidParameter("cluster.load_path", false);
		config.addNewValidParameter("normalization.type", false);

		config.addNewValidParameter("surf.radius", false);
		config.addNewValidParameter("surf.threshold", false);
		config.addNewValidParameter("surf.ignoreborder", false);
		config.addNewValidParameter("surf.strictrule", false);
		config.addNewValidParameter("surf.maxfeaturesperscale", false);
		config.addNewValidParameter("surf.initialsamplerate", false);
		config.addNewValidParameter("surf.initialsize", false);
		config.addNewValidParameter("surf.numberscalesperoctave", false);
		config.addNewValidParameter("surf.numberofoctaves", false);

		config.addNewValidCommand("auto.imageset.relation");
		config.addNewValidCommand("auto.arff.relation");
		config.addNewValidCommand("auto.file.name");
		config.addNewValidCommand("use.gui");

		config.setConfiguration("random.seed", "1");

		config.readFromRunArgs(args);

		try {
			// Check if we have enought parameters to start
			config.verifyArgs();
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}


		// Print loaded configuration
		if(!config.isCommandSet("use.gui")) {
			config.debugParameters();
		}

		// Time extraction process started
		long start = System.currentTimeMillis();

		try {
			SurfExtractor m = new SurfExtractor();
			if(config.isCommandSet("use.gui")) {
				m.run(null);
			} else  {
				m.run(config);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		long duration = System.currentTimeMillis() - start;

		LOGGER.info("Duration of the process: " + (duration / 1000) + " seconds.");
	}

	public SurfExtractor() {

	}

	/**
	 * Main class object
	 * 
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */

	public void run(Configuration config) {
		//Verify if config was passed
		if(config == null) {
			config = getConfigFromGUI();
		}
		// Load images from ImageSet
		ImageSet is;
		try {
			is = new ImageSet(config.getConfiguration("imageset.path"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		if (config.isCommandSet("auto.imageset.relation")) {
			is.setRelation(config.getConfiguration("imageset.relation") + "-" + config.getConfiguration("surf.radius") + "-" + config.getConfiguration("surf.threshold") + "-" + config.getConfiguration("surf.maxfeaturesperscale") + "-" + config.getConfiguration("surf.initialsamplerate") + "-" + config.getConfiguration("surf.initialsize") + "-" + config.getConfiguration("surf.numberscalesperoctave") + "-" + config.getConfiguration("surf.numberofoctaves"));
			LOGGER.info("Setting imageset.relation automatically");
		} else {
			LOGGER.info("Setting image.relation manually");
			is.setRelation(config.getConfiguration("imageset.relation"));
		}

		// Create clustering object
		Clustering clustering = new Clustering(is, Integer.valueOf(config.getConfiguration("kmeans.kvalue")), Integer.valueOf(config.getConfiguration("kmeans.iteration")));
		clustering.setSeed(Integer.parseInt(config.getConfiguration("random.seed")));

		// Set Dataset 'name'
		if (config.isCommandSet("auto.arff.relation")) {
			LOGGER.info("Setting arff.relation automatically");
			config.setConfiguration("arff.relation", config.getConfiguration("imageset.relation") + "-" + config.getConfiguration("surf.radius") + "-" + config.getConfiguration("surf.threshold") + "-" + config.getConfiguration("surf.maxfeaturesperscale") + "-" + config.getConfiguration("surf.initialsamplerate") + "-" + config.getConfiguration("surf.initialsize") + "-" + config.getConfiguration("surf.numberscalesperoctave") + "-" + config.getConfiguration("surf.numberofoctaves"));
		} else {
			LOGGER.info("arff.relation is set manually");
		}

		// Create SURF Feature extractor objects
		SurfDescriptor surfExtractor = new SurfDescriptor();

		config.addNewValidParameter("surf.radius", false);
		config.addNewValidParameter("surf.threshold", false);
		config.addNewValidParameter("surf.ignoreborder", false);
		config.addNewValidParameter("surf.strictrule", false);
		config.addNewValidParameter("surf.maxfeaturesperscale", false);
		config.addNewValidParameter("surf.initialsamplerate", false);
		config.addNewValidParameter("surf.initialsize", false);
		config.addNewValidParameter("surf.numberscalesperoctave", false);
		config.addNewValidParameter("surf.numberofoctaves", false);

		if (config.isCommandSet("surf.radius")) surfExtractor.setRadius(Integer.valueOf(config.getConfiguration("surf.radius")));

		if (config.isCommandSet("surf.threshold")) surfExtractor.setThreshold(Float.valueOf(config.getConfiguration("surf.threshold")));

		if (config.isCommandSet("surf.ignoreborder")) surfExtractor.setIgnoreBorder(Integer.valueOf(config.getConfiguration("surf.ignoreborder")));

		if (config.isCommandSet("surf.strictrule")) surfExtractor.setStrictRule(Boolean.valueOf(config.getConfiguration("surf.strictrule")));

		if (config.isCommandSet("surf.maxfeaturesperscale")) surfExtractor.setMaxFeaturesPerScale(Integer.valueOf(config.getConfiguration("surf.maxfeaturesperscale")));

		if (config.isCommandSet("surf.initialsamplerate")) surfExtractor.setInitialSampleRate(Integer.valueOf(config.getConfiguration("surf.initialsamplerate")));

		if (config.isCommandSet("surf.initialsize")) surfExtractor.setInitialSize(Integer.valueOf(config.getConfiguration("surf.initialsize")));

		if (config.isCommandSet("surf.numberscalesperoctave")) surfExtractor.setNumberScalesPerOctave(Integer.valueOf(config.getConfiguration("surf.numberscalesperoctave")));

		if (config.isCommandSet("surf.numberofoctaves")) surfExtractor.setNumberOfOctaves(Integer.valueOf(config.getConfiguration("surf.numberofoctaves")));

		// Load images from ImageSet
		is.getImageClasses();

		// Use surfExtractor to extract SURF features
		surfExtractor.extractImageSet(is);

		// Cluster all features
		if (config.getConfiguration("cluster.load_path") != null) {
			try {
				clustering.loadClustersFromFile(new File(config.getConfiguration("cluster.load_path")));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} else {
			clustering.cluster();
			clustering.getWk();
		}

		// Export clusters
		if (config.getConfiguration("cluster.save_path") != null) {
			LOGGER.info("Saving clusters to file");
			try {
				clustering.saveClustersToFile(new File(config.getConfiguration("clusters.save_path")));
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

		// Write experimental arff
		InstanceGenerator instanceGenerator = new InstanceGenerator(is, bow);
		if(config.getConfiguration("normalization.type") != null) {
			instanceGenerator.setNormalizationType(Integer.valueOf(config.getConfiguration("normalization.type")));
		}
		instanceGenerator.export();
		
		WekaExporter wekaExporter = new WekaExporter(instanceGenerator.getInstances());
		//Exporter exporter = new WekaExporter(is, bow);
		
		if (config.isCommandSet("auto.file.name")) {
			LOGGER.info("Automatically setting file name");
			wekaExporter.setPath(config.getConfiguration("arff.path") + config.getConfiguration("imageset.relation") + "-" + config.getConfiguration("surf.radius") + "-" + config.getConfiguration("surf.threshold") + "-" + config.getConfiguration("surf.maxfeaturesperscale") + "-" + config.getConfiguration("surf.initialsamplerate") + "-" + config.getConfiguration("surf.initialsize") + "-" + config.getConfiguration("surf.numberscalesperoctave") + "-" + config.getConfiguration("surf.numberofoctaves") + ".arff");
			wekaExporter.export();
		} else {
			LOGGER.info("Using manual file name");
			wekaExporter.setPath(config.getConfiguration("arff.path"));
			wekaExporter.export();
		}
	}

	/**
	 * Run
	 * 
	 * @return A configuration generated from the GUI
	 */
	private Configuration getConfigFromGUI() {
		UserInterface.start();
		UserInterface.hold();
		
		Configuration config = new Configuration();
		
		config.setConfiguration("imageset.path", UserInterface.imagesetPath.getAbsolutePath());
		config.setConfiguration("kmeans.kvalue", String.valueOf((Integer)UserInterface.kmeanskSpinner.getValue()));
		config.setConfiguration("kmeans.iteration", String.valueOf((Integer)UserInterface.kmeansIterSpinner.getValue()));
		config.setConfiguration("arff.path", UserInterface.arffDestinationPath.getAbsolutePath());
		config.setConfiguration("random.seed", String.valueOf((Integer) UserInterface.randSeedSpinner.getValue()));
		config.setConfiguration("imageset.relation", "DEBUGGING");
		
		try {
			config.verifyArgs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		config.debugParameters();
		return config;
	}

	/**
	 * Old method to create arff as a library(used in the surfExtractorGUI)
	 * 
	 * @param imagesetPath
	 * @param kmeansK
	 * @param kmeansIterations
	 * @param arffRelation
	 * @param arffPath
	 */
	public void generateArff(Configuration config) throws FileNotFoundException, UnsupportedEncodingException {

		// Load images from ImageSet
		ImageSet is = null;
		try {
			//is = new ImageSet(imagesetPath);
			is = new ImageSet(config.getConfiguration("imageset.path"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		// Create clustering object
		//Clustering clustering = new Clustering(is, kmeansK, kmeansIterations);
		Clustering clustering = new Clustering(is, Integer.valueOf(config.getConfiguration("kmeans.kvalue")), Integer.valueOf(config.getConfiguration("kmeans.iteration")));

		// Set Dataset 'name'
		is.setRelation(config.getConfiguration("arff.relation"));

		// Create SURF Feature extractor objects
		SurfDescriptor surfExtractor = new SurfDescriptor();

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
		InstanceGenerator instanceGenerator = new InstanceGenerator(is, bow);
		instanceGenerator.export();
		
		
		WekaExporter exporter = new WekaExporter(instanceGenerator.getInstances());
		if (config.isCommandSet("auto.file.name")) {
			LOGGER.info("Automatically setting file name");
			//exporter.setPath(arffPath + config.getConfiguration("imageset.relation") + "-" + config.getConfiguration("surf.radius") + "-" + config.getConfiguration("surf.threshold") + "-" + config.getConfiguration("surf.maxfeaturesperscale") + "-" + config.getConfiguration("surf.initialsamplerate") + "-" + config.getConfiguration("surf.initialsize") + "-" + config.getConfiguration("surf.numberscalesperoctave") + "-" + config.getConfiguration("surf.numberofoctaves"));
			exporter.setPath(config.getConfiguration("arff.path") + config.getConfiguration("imageset.relation") + "-" + config.getConfiguration("surf.radius") + "-" + config.getConfiguration("surf.threshold") + "-" + config.getConfiguration("surf.maxfeaturesperscale") + "-" + config.getConfiguration("surf.initialsamplerate") + "-" + config.getConfiguration("surf.initialsize") + "-" + config.getConfiguration("surf.numberscalesperoctave") + "-" + config.getConfiguration("surf.numberofoctaves"));
			exporter.export();
		} else {
			LOGGER.info("Using manual file name");
			exporter.setPath(config.getConfiguration("arff.path"));
			exporter.export();
		}
	}
	
	public Instances generateInstances(String imagesetPath, int kmeansK, int kmeansIterations, String arffRelation) {

		// Load images from ImageSet
		ImageSet is = null;
		try {
			is = new ImageSet(imagesetPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		// Create clustering object
		Clustering clustering = new Clustering(is, kmeansK, kmeansIterations);

		// Set Dataset 'name'
		is.setRelation(arffRelation);

		// Create SURF Feature extractor objects
		SurfDescriptor surfExtractor = new SurfDescriptor();

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
		InstanceGenerator instanceGenerator = new InstanceGenerator(is, bow);
		instanceGenerator.export();
		
		return instanceGenerator.getInstances();
	}
}
