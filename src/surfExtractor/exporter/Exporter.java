package surfExtractor.exporter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import surfExtractor.bow_classifier.Bow;
import surfExtractor.bow_classifier.Histogram;
import surfExtractor.image_set.ImageSet;
import configuration.*;

public class Exporter {
	/**
	 * What ImageSet we are exporting
	 */
	private ImageSet imageSet;
	/**
	 * BOW information
	 */
	private Bow bow;
	/**
	 * Addition information to add to the final arff file
	 */
	private ArrayList<String> comments = new ArrayList<String>();

	/**
	 * log4j object
	 */
	private final static Logger LOGGER = Logger.getLogger(Exporter.class);

	public Exporter(ImageSet is, Bow bow) {
		this.imageSet = is;
		this.bow = bow;
	}

	/**
	 * Export BOW histogram to .arff file for Weka
	 * 
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public String generateArffFile(String path) throws FileNotFoundException, UnsupportedEncodingException {
		LOGGER.info("Generating arff file at:" + path);
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		writer.println("@relation " + Configuration.getConfiguration("arff.relation"));
		writer.println();
		// comments
		LOGGER.info(comments.size() + " comments to be added");
		for (String s : comments) {
			writer.println("% " + s);
		}
		writer.println();
		for (int i = 0; i < bow.getClusterNum(); i++) {
			writer.println("@attribute A" + i + " numeric");
		}
		writer.print("@attribute class {");
		for (int i = 0; i < imageSet.getImageClasses().size(); i++) {
			writer.print(imageSet.getImageClasses().get(i).getFile().getName());
			if (i != imageSet.getImageClasses().size() - 1) {
				writer.print(", ");
			}
		}
		writer.print("}");
		writer.println();
		writer.println("@data");
		for (Histogram hh : this.bow.getHistograms()) {
			hh.normalize();
			// hh.normalize_as_vector();
			// hh.rescale(-1, 1);
			writer.println(hh.toString());
		}
		writer.close();

		// FIXME
		return null;
	}

	public void addCommentLine(String c) {
		this.comments.add(c);
		LOGGER.info("Adding comment. " + this.comments.size() + " comments currently");
	}
}
