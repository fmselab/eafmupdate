package fmficrepair.repair;

import java.io.IOException;
import java.util.Comparator;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import fmficrepair.Oracle;
import fmficrepair.Util;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

public class FMComparator implements Comparator<IFeatureModel> {

	public IFeatureModel reference;
	//public Map<IFeatureModel,Double> distanceCache = new HashMap<>();
	public Oracle oracle;
	
	public FMComparator(IFeatureModel reference) {
		this.reference = reference;
	}
	
	public FMComparator(Oracle oracle) {
		this.oracle=oracle;
	}

	@Override
	public int compare(IFeatureModel o1, IFeatureModel o2) {
		try {
			Double d1, d2;
			if (oracle==null) {
				d1 = Util.getAdequacy(reference, o1);
				d2 = Util.getAdequacy(reference, o2);
			} else {
				d1 = Util.getAdequacy(oracle, o1);
				d2 = Util.getAdequacy(oracle, o2);
			}
			//Double d1 = distanceCache.containsKey(o1) ? distanceCache.get(o1) : distanceCache.put(o1, Experiment.getAdequacy(reference, o1));
			//Double d2 = distanceCache.containsKey(o2) ? distanceCache.get(o2) : distanceCache.put(o2, Experiment.getAdequacy(reference, o2));
			return d1==null || d2==null || d1.equals(d2) ? 0 : (d1<d2 ? 1 : -1);
				
		} catch (TimeoutException | IOException | FeatureModelException | ConfigurationEngineException 	| UnsupportedModelException e) {e.printStackTrace(); return 0;}
	}
	
	

}
