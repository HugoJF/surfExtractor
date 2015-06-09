package surfExtractor.exporter;

import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.Saver;

public class WekaExporter extends FileExporter {

	/**
	 * What Saver to use when exporting
	 */
	private Saver saver;
	
	/**
	 * Weka Instances to export
	 */
	private Instances instances;
	
	/**
	 * @param instances - Weka Instances to export
	 * @param saver - What saver to use when exporting
	 */
	public WekaExporter(Instances instances, Saver saver) {
		this.instances = instances;
		this.saver = saver;
	}
	
	/**
	 * Construct WekaExporter with default ArffSaver saver
	 * 
	 * @param instances - Instances to export
	 */
	public WekaExporter(Instances instances) {
		this.instances = instances;
	}
	
	@Override
	public void export() {
		if(saver == null) {
			saver = new ArffSaver();
		}
		
		this.saver.setInstances(instances);
		
		if(path != "") {
			try {
				this.saver.setFile(this.getFile());
				this.saver.writeBatch();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
