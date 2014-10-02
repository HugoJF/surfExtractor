package surfExtractor.exporter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import surfExtractor.bow_classifier.Bow;
import surfExtractor.bow_classifier.Histogram;
import surfExtractor.misc.Configuration;
import surfExtractor.surf_extractor.ImageSet;

public class Exporter {
	private ImageSet imageSet;
	private Bow bow;

	public Exporter(ImageSet is, Bow bow) {
		this.imageSet = is;
		this.bow = bow;
	}

	public String generateArffFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(Configuration.getConfiguration("arff.path"), "UTF-8");
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
			writer.println(hh.toString());
		}
		writer.close();

		// FIXME
		return null;
	}
}
