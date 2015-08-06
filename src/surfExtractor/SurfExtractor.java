package surfExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EnumMap;

import surfExtractor.image_set.ImageSet;
import configuration.Configuration;
import micro.Benchmarking.MicroBench;

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

	public enum Config {
		IMAGESET_PATH, IMAGESET_RELATION, ARFF_RELATION, ARFF_PATH, KMEANS_ITERATION, KMEANS_KVALUE, CLUSTER_SAVEPATH, CLUSTER_LOADPATH, NORMALIZATION_TYPE, RANDOM_SEED,

		SURF_RADIUS, SURF_THRESHOLD, SURF_IGNOREBORDER, SURF_STRICTRULE, SURF_MAXFEATURESPERSCALE, SURF_INITIALSAMPLERATE, SURF_INITIALSIZE, SURF_NUMBERSCALESPEROCTAVE, SURF_NUMBEROFOCTAVES,

		AUTO_IMAGESET_RELATION, AUTO_ARFF_RELATION, AUTO_FILE_NAME,

		USE_GUI;
	}

	private final static Logger LOGGER = Logger.getLogger(SurfExtractor.class);

	/**
	 * @param args - Run configuration parameters
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */

	public static void setConfigParameters(Configuration config) {
		EnumMap<Config, String> map = new EnumMap<Config, String>(Config.class);

		map.put(Config.IMAGESET_PATH, "imageset.path");
		map.put(Config.IMAGESET_RELATION, "imageset.relation");
		map.put(Config.RANDOM_SEED, "random.seed");
		map.put(Config.ARFF_RELATION, "arff.relation");
		map.put(Config.ARFF_PATH, "arff.path");
		map.put(Config.KMEANS_ITERATION, "kmeans.iteration");
		map.put(Config.KMEANS_KVALUE, "kmeans.kvalue");
		map.put(Config.CLUSTER_SAVEPATH, "cluster.save_path");
		map.put(Config.CLUSTER_LOADPATH, "cluster.load_path");
		map.put(Config.NORMALIZATION_TYPE, "normalization.type");

		map.put(Config.SURF_RADIUS, "surf.radius");
		map.put(Config.SURF_THRESHOLD, "surf.threshold");
		map.put(Config.SURF_IGNOREBORDER, "surf.ignoreborder");
		map.put(Config.SURF_STRICTRULE, "surf.strictrule");
		map.put(Config.SURF_MAXFEATURESPERSCALE, "surf.maxfeaturesperscale");
		map.put(Config.SURF_INITIALSAMPLERATE, "surf.initialsamplerate");
		map.put(Config.SURF_INITIALSIZE, "surf.initialsize");
		map.put(Config.SURF_NUMBERSCALESPEROCTAVE, "surf.numberscalesperoctave");
		map.put(Config.SURF_NUMBEROFOCTAVES, "surf.numberofoctaves");

		map.put(Config.AUTO_IMAGESET_RELATION, "auto.imageset.relation");
		map.put(Config.AUTO_ARFF_RELATION, "auto.arff.relation");
		map.put(Config.AUTO_FILE_NAME, "auto.file.name");
		map.put(Config.USE_GUI, "use.gui");

		config.setKeyMap(map);

		config.setConfig(Config.RANDOM_SEED, "1");

		config.addNewValidParameter(Config.IMAGESET_PATH, true);
		config.addNewValidParameter(Config.IMAGESET_RELATION, true);
		config.addNewValidParameter(Config.RANDOM_SEED, false);
		config.addNewValidParameter(Config.ARFF_RELATION, false);
		config.addNewValidParameter(Config.ARFF_PATH, true);
		config.addNewValidParameter(Config.KMEANS_ITERATION, true);
		config.addNewValidParameter(Config.KMEANS_KVALUE, true);
		config.addNewValidParameter(Config.CLUSTER_SAVEPATH, false);
		config.addNewValidParameter(Config.CLUSTER_LOADPATH, false);
		config.addNewValidParameter(Config.NORMALIZATION_TYPE, false);

		config.addNewValidParameter(Config.SURF_RADIUS, false);
		config.addNewValidParameter(Config.SURF_THRESHOLD, false);
		config.addNewValidParameter(Config.SURF_IGNOREBORDER, false);
		config.addNewValidParameter(Config.SURF_STRICTRULE, false);
		config.addNewValidParameter(Config.SURF_MAXFEATURESPERSCALE, false);
		config.addNewValidParameter(Config.SURF_INITIALSAMPLERATE, false);
		config.addNewValidParameter(Config.SURF_INITIALSIZE, false);
		config.addNewValidParameter(Config.SURF_NUMBERSCALESPEROCTAVE, false);
		config.addNewValidParameter(Config.SURF_NUMBEROFOCTAVES, false);

		config.addNewValidCommand(Config.AUTO_IMAGESET_RELATION);
		config.addNewValidCommand(Config.AUTO_ARFF_RELATION);
		config.addNewValidCommand(Config.AUTO_FILE_NAME);
		config.addNewValidCommand(Config.USE_GUI);

		config.setConfig(Config.RANDOM_SEED, "1");

	}

	public static void main(String[] args) throws Exception {
		// //////////////////////////////
		// STARTUP PARAMETERS READING //
		// //////////////////////////////

		MicroBench.tick("Setting up parameters");

		// Manually prepare the Configuration
		Configuration config = new Configuration();

		setConfigParameters(config);

		config.readFromRunArgs(args);

		LOGGER.debug(MicroBench.tock());

		// Print loaded configuration if it's valid
		if (!config.isCommandSet(Config.USE_GUI) || (config.getValidCommandsSet() != 0 && config.getValidParametersSet() != 0)) {
			config.debugParameters();
		}

		// ///////////
		// STARTUP //
		// ///////////

		// Time extraction process started
		long start = System.currentTimeMillis();

		SurfExtractor m = new SurfExtractor();
		if (config.isCommandSet(Config.USE_GUI) || (config.getValidCommandsSet() == 0 && config.getValidParametersSet() == 0)) {
			m.run(null);
		} else {
			m.run(config);
		}
		long duration = System.currentTimeMillis() - start;

		LOGGER.info("Duration of the process: " + (duration / 1000) + " seconds.");

		MicroBench.printSummary();
	}

	public void run(Configuration cfg) {
		// ///////////////////
		// CONFIG HANDLING //
		// ///////////////////

		// Verify if config exists
		if (cfg == null) {
			cfg = getConfigFromGUI();
		}

		// Update arff relation
		if (cfg.isCommandSet(Config.AUTO_ARFF_RELATION)) {
			LOGGER.info("Setting arff.relation automatically");
			cfg.setConfig(Config.ARFF_RELATION, generateRelation(cfg));
		} else {
			LOGGER.info("arff.relation is set manually");
		}

		// /////////////////////
		// IMAGESET HANDLING //
		// /////////////////////

		MicroBench.tick("Starting the ImageSet");

		// Load images from ImageSet
		ImageSet imageSet = createImageSet(cfg);

		LOGGER.debug(MicroBench.tock());

		// ////////////////////
		// SURF DESCRIPTION //
		// ////////////////////

		// Create SURF Descriptor
		SurfDescriptor surfDescriptor = new SurfDescriptor();

		// Sets every parameters overwritten by the runtime config
		setupSurfDescriptor(surfDescriptor, cfg);

		// Use surfExtractor to extract SURF features
		surfDescriptor.extractImageSet(imageSet);

		// //////////////
		// CLUSTERING //
		// //////////////

		MicroBench.tick("Clustering and statistics");

		// Create clustering object
		Clustering clustering = new Clustering(imageSet, cfg.getConfigAsInt(Config.KMEANS_KVALUE), cfg.getConfigAsInt(Config.KMEANS_ITERATION));
		clustering.setSeed(cfg.getConfigAsInt(Config.RANDOM_SEED));

		// Cluster all features
		if (cfg.getConfig(Config.CLUSTER_LOADPATH) != null) {
			try {
				clustering.loadClustersFromFile(new File(cfg.getConfig(Config.CLUSTER_LOADPATH)));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} else {
			clustering.cluster();
			clustering.getWk();
		}

		// Export clusters
		if (cfg.getConfig(Config.CLUSTER_SAVEPATH) != null) {
			LOGGER.info("Saving clusters to file");
			try {
				clustering.saveClustersToFile(new File(cfg.getConfig(Config.CLUSTER_SAVEPATH)));
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			}
		}

		LOGGER.debug(MicroBench.tock());

		// ////////////////
		// BAG OF WORDS //
		// ////////////////

		MicroBench.tick("Starting BagOfWords class");

		// Create the Bag of Words classifier
		Bow bow = new Bow(imageSet, clustering.getClusters());

		// Compute frequency histograms
		bow.computeHistograms();

		LOGGER.debug(MicroBench.tock());

		// ///////////////////////
		// INSTANCE GENERATION //
		// ///////////////////////

		MicroBench.tick("Instances generation");

		// Starts Weka Instance generator
		InstanceGenerator instanceGenerator = new InstanceGenerator(imageSet, bow);

		// Sets the normalization type
		if (cfg.getConfig(Config.NORMALIZATION_TYPE) != null) {
			instanceGenerator.setNormalizationType(cfg.getConfigAsInt(Config.NORMALIZATION_TYPE));
		}

		// Generate instances
		instanceGenerator.export();

		LOGGER.debug(MicroBench.tock());

		// ///////////////
		// EXPORTATION //
		// ///////////////

		MicroBench.tick("File exportation");

		WekaExporter wekaExporter = new WekaExporter(instanceGenerator.getInstances());
		// Exporter exporter = new WekaExporter(is, bow);

		if (cfg.isCommandSet(Config.AUTO_FILE_NAME)) {
			LOGGER.info("Automatically setting file name");
			wekaExporter.setPath(generateRelation(cfg) + ".arff");
		} else {
			LOGGER.info("Using manual file name");
			wekaExporter.setPath(cfg.getConfig(Config.ARFF_PATH));
		}

		wekaExporter.export();

		LOGGER.debug(MicroBench.tock());
	}

	private ImageSet createImageSet(Configuration cfg) {
		ImageSet imageSet;
		try {
			imageSet = new ImageSet(cfg.getConfig(Config.IMAGESET_PATH));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}
		if (cfg.isCommandSet(Config.AUTO_IMAGESET_RELATION)) {
			imageSet.setRelation(generateRelation(cfg));
			LOGGER.info("Setting imageset.relation automatically");
		} else {
			LOGGER.info("Setting image.relation manually");
			imageSet.setRelation(cfg.getConfig(Config.IMAGESET_RELATION));
		}

		return imageSet;
	}

	private void setupSurfDescriptor(SurfDescriptor surfExtractor, Configuration cfg) {
		if (cfg.isCommandSet(Config.SURF_RADIUS))
			surfExtractor.setRadius(cfg.getConfigAsInt(Config.SURF_RADIUS));
		if (cfg.isCommandSet(Config.SURF_THRESHOLD))
			surfExtractor.setThreshold(cfg.getConfigAsFloat(Config.SURF_THRESHOLD));
		if (cfg.isCommandSet(Config.SURF_IGNOREBORDER))
			surfExtractor.setIgnoreBorder(cfg.getConfigAsInt(Config.SURF_IGNOREBORDER));
		if (cfg.isCommandSet(Config.SURF_STRICTRULE))
			surfExtractor.setStrictRule(cfg.getConfigAsBoolean(Config.SURF_STRICTRULE));
		if (cfg.isCommandSet(Config.SURF_MAXFEATURESPERSCALE))
			surfExtractor.setMaxFeaturesPerScale(cfg.getConfigAsInt(Config.SURF_MAXFEATURESPERSCALE));
		if (cfg.isCommandSet(Config.SURF_INITIALSAMPLERATE))
			surfExtractor.setInitialSampleRate(cfg.getConfigAsInt(Config.SURF_INITIALSAMPLERATE));
		if (cfg.isCommandSet(Config.SURF_INITIALSIZE))
			surfExtractor.setInitialSize(cfg.getConfigAsInt(Config.SURF_INITIALSIZE));
		if (cfg.isCommandSet(Config.SURF_NUMBERSCALESPEROCTAVE))
			surfExtractor.setNumberScalesPerOctave(cfg.getConfigAsInt(Config.SURF_NUMBERSCALESPEROCTAVE));
		if (cfg.isCommandSet(Config.SURF_NUMBEROFOCTAVES))
			surfExtractor.setNumberOfOctaves(cfg.getConfigAsInt(Config.SURF_NUMBEROFOCTAVES));
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

		config.setConfig(Config.IMAGESET_PATH, UserInterface.imagesetPath.getAbsolutePath());
		config.setConfig(Config.KMEANS_KVALUE, String.valueOf((Integer) UserInterface.kmeanskSpinner.getValue()));
		config.setConfig(Config.KMEANS_ITERATION, String.valueOf((Integer) UserInterface.kmeansIterSpinner.getValue()));
		config.setConfig(Config.ARFF_PATH, UserInterface.arffDestinationPath.getAbsolutePath());
		config.setConfig(Config.RANDOM_SEED, String.valueOf((Integer) UserInterface.randSeedSpinner.getValue()));
		config.setConfig(Config.IMAGESET_RELATION, "DEBUGGING");

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
			// is = new ImageSet(imagesetPath);
			is = new ImageSet(config.getConfig(Config.IMAGESET_PATH));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		// Create clustering object
		Clustering clustering = new Clustering(is, Integer.valueOf(config.getConfig(Config.KMEANS_KVALUE)), Integer.valueOf(config.getConfig(Config.KMEANS_ITERATION)));

		// Set Dataset 'name'
		is.setRelation(config.getConfig(Config.IMAGESET_RELATION));

		// Create SURF Feature extractor objects
		SurfDescriptor surfExtractor = new SurfDescriptor();

		// Load images from ImageSet
		is.getImageClasses();

		// Use surfExtractor to extract SURF features
		surfExtractor.extractImageSet(is);

		// Cluster all features
		clustering.cluster();

		// Return final clusters
		ArrayList<Cluster> featureCluster = clustering.getClusters();

		// Load Bag Of Words classifier
		Bow bow = new Bow(is, featureCluster);

		// Compute frequency histograms
		bow.computeHistograms();

		// Write experimental arff
		InstanceGenerator instanceGenerator = new InstanceGenerator(is, bow);
		instanceGenerator.export();

		WekaExporter exporter = new WekaExporter(instanceGenerator.getInstances());
		if (config.isCommandSet(Config.AUTO_FILE_NAME)) {
			LOGGER.info("Automatically setting file name");
			exporter.setPath(config.getConfig(Config.ARFF_PATH) + config.getConfig(Config.IMAGESET_RELATION) + "-" + config.getConfig(Config.SURF_RADIUS) + "-" + config.getConfig(Config.SURF_THRESHOLD) + "-" + config.getConfig(Config.SURF_MAXFEATURESPERSCALE) + "-" + config.getConfig(Config.SURF_INITIALSAMPLERATE) + "-" + config.getConfig(Config.SURF_INITIALSIZE) + "-" + config.getConfig(Config.SURF_NUMBERSCALESPEROCTAVE) + "-" + config.getConfig(Config.SURF_NUMBEROFOCTAVES));
			exporter.export();
		} else {
			LOGGER.info("Using manual file name");
			exporter.setPath(config.getConfig(Config.ARFF_PATH));
			exporter.export();
		}
	}

	public Instances generateInstances(Configuration config) {

		// Load images from ImageSet
		ImageSet is = null;
		try {
			// is = new ImageSet(imagesetPath);
			is = new ImageSet(config.getConfig(Config.IMAGESET_PATH));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		// Create clustering object
		Clustering clustering = new Clustering(is, Integer.valueOf(config.getConfig(Config.KMEANS_KVALUE)), Integer.valueOf(config.getConfig(Config.KMEANS_ITERATION)));

		// Set Dataset 'name'
		// is.setRelation(arffRelation);
		is.setRelation(config.getConfig(Config.IMAGESET_RELATION));

		// Create SURF Feature extractor objects
		SurfDescriptor surfExtractor = new SurfDescriptor();

		// Load images from ImageSet
		is.getImageClasses();

		// Use surfExtractor to extract SURF features
		surfExtractor.extractImageSet(is);

		// Cluster all features
		clustering.cluster();

		// Return final clusters
		ArrayList<Cluster> featureCluster = clustering.getClusters();

		// Load Bag Of Words classifier
		Bow bow = new Bow(is, featureCluster);

		// Compute frequency histograms
		bow.computeHistograms();

		// Write experimental arff
		InstanceGenerator instanceGenerator = new InstanceGenerator(is, bow);
		instanceGenerator.export();

		return instanceGenerator.getInstances();
	}

	/**
	 * @param config Runtime configuration object
	 * @return Relation with every SURF parameter included
	 */
	private String generateRelation(Configuration config) {
		String name = config.getConfig(Config.IMAGESET_RELATION) + "-" + config.getConfig(Config.SURF_RADIUS) + "-" + config.getConfig(Config.SURF_THRESHOLD) + "-" + config.getConfig(Config.SURF_MAXFEATURESPERSCALE) + "-" + config.getConfig(Config.SURF_INITIALSAMPLERATE) + "-" + config.getConfig(Config.SURF_INITIALSIZE) + "-" + config.getConfig(Config.SURF_NUMBERSCALESPEROCTAVE) + "-" + config.getConfig(Config.SURF_NUMBEROFOCTAVES);
		return name;
	}
}
