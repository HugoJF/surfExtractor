package surfExtractor.exporter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import surfExtractor.bow_classifier.Bow;
import surfExtractor.bow_classifier.Histogram;
import surfExtractor.image_set.ImageSet;
import configuration.*;

public class Exporter {
	private ImageSet imageSet;
	private Bow bow;

	private final static Logger LOGGER = Logger.getLogger(Exporter.class);
	
	public Exporter(ImageSet is, Bow bow) {
		this.imageSet = is;
		this.bow = bow;
	}

	public String generateArffFile(String path) throws FileNotFoundException, UnsupportedEncodingException {
		LOGGER.info("Generating arff file at:" + path);
		//FIXME - 'hardcoded' path, method should have path parameter
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		writer.println("@relation " + Configuration.getConfiguration("arff.relation"));
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
			//hh.normalize_as_vector();
			//hh.rescale(-1, 1);
			writer.println(hh.toString());
		}
		writer.close();

		// FIXME
		return null;
	}
}
