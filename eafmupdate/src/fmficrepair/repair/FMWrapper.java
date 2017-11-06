package fmficrepair.repair;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import fmficrepair.MutatedModel;
import fmficrepair.Oracle;
import fmficrepair.Util;

public class FMWrapper {
	public final IFeatureModel fm;
	public final double adequacy;
	//public final int editDistance;
	public final MutatedModel mutatedModel;
	
	@Deprecated
	public FMWrapper(IFeatureModel fm, double adequacy, int editDistance) {
		this.fm=fm;
		this.adequacy=adequacy;
		//this.editDistance=editDistance;
		this.mutatedModel = new MutatedModel(fm);
	}
	
	@Deprecated
	public FMWrapper(IFeatureModel fm, IFeatureModel reference) {
		this.fm=fm;
		double adequacy = 0;
		try {adequacy = Util.getAdequacy(reference, fm);} catch (Exception e) {e.printStackTrace();}
		this.adequacy=adequacy;
		//editDistance = (int) EditDistance.instance.getDistance(reference, fm);
		this.mutatedModel = new MutatedModel(fm);
	}
	
	@Deprecated
	public FMWrapper(IFeatureModel fm, Oracle reference) {
		this.fm=fm;
		double adequacy = 0;
		try {adequacy = Util.getAdequacy(reference, fm);} catch (Exception e) {e.printStackTrace();}
		this.adequacy=adequacy;
		//editDistance = (int) EditDistance.instance.getDistance(reference.originalFM, fm);
		this.mutatedModel = new MutatedModel(fm);
	}
	
	public FMWrapper(MutatedModel model, Oracle reference) {
		this.fm=model.model;
		double adequacy = 0;
		try {adequacy = Util.getAdequacy(reference, fm);} catch (Exception e) {e.printStackTrace();}
		this.adequacy=adequacy;
		//editDistance = (int) EditDistance.instance.getDistance(reference.originalFM, fm);
		this.mutatedModel = model;
	}
	
	public boolean equals(Object fmWrapper) {
		return fmWrapper!=null && fmWrapper instanceof FMWrapper ? fm.toString().equals(((FMWrapper)fmWrapper).fm.toString()) : false;
	}
	
	
}
