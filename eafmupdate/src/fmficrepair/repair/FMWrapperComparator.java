package fmficrepair.repair;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import fmficrepair.Oracle;
import fmficrepair.Util;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

public class FMWrapperComparator implements Comparator<FMWrapper> {

	public final  IFeatureModel reference;
	public final  Oracle oracle;
	public final Map<FMWrapper,Double> distanceCache = new HashMap<>();
	
	public FMWrapperComparator(IFeatureModel reference) {
		this.reference = reference;
		this.oracle=null;
	}
	
	public FMWrapperComparator(Oracle oracle) {
		this.oracle=oracle;
		this.reference=null;
	}

	@Override
	public int compare(FMWrapper o1, FMWrapper o2) {
		try {
			Double d1, d2;
//			Double d1 = Util.getAdequacy(reference, o1.fm);
//			Double d2 = Util.getAdequacy(reference, o2.fm);
//			Double d1 = distanceCache.containsKey(o1) ? distanceCache.get(o1) : distanceCache.put(o1, Util.getAdequacy(reference, o1.fm));
//			Double d2 = distanceCache.containsKey(o2) ? distanceCache.get(o2) : distanceCache.put(o2, Util.getAdequacy(reference, o2.fm));
			
			if (oracle==null) {
				d1 = distanceCache.put(o1, Util.getAdequacy(reference, o1.fm));
				d2 = distanceCache.put(o2, Util.getAdequacy(reference, o2.fm));
			} else {
				d1 = distanceCache.put(o1, Util.getAdequacy(oracle, o1.fm));
				d2 = distanceCache.put(o2, Util.getAdequacy(oracle, o2.fm));
			}
			return d1==null || d2==null ? 0 : (d1<d2 ? 1 : -1);
		} catch (TimeoutException | IOException | FeatureModelException | ConfigurationEngineException 	| UnsupportedModelException e) {e.printStackTrace(); return 0;}
	}
	
	

}
