package surfExtractor.exporter;

import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.Saver;

public class WekaExporter extends FileExporter {

	private Saver saver;
	
	private Instances instances;
	
	public WekaExporter(Instances instances, Saver saver) {
		this.instances = instances;
		this.saver = saver;
	}
	
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
