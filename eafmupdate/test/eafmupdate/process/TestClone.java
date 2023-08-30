package eafmupdate.process;

import org.junit.Test;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.impl.FMFactoryManager;

public class TestClone {
	@Test
	public void testClone() {
		IFeatureModel m1 = ModelsPair.AIRCRAFT1.getFM1();
		//
		IFeatureModel m2 = null;
				// no longer present this method:
				// FMFactoryManager.getEmptyFeatureModel();
		m2 = m1.clone();
		//m1.getStructure().clone(m2);
		IFeature f = //FMFactoryManager.getDefaultFactory().createFeature(m2, "Root");
				m1.getStructure().getRoot().getFeature().clone(m2, null);
		m2 = m2.clone(f);
		m2.getStructure().getRoot().getChildren().get(0).changeToOr();
		
		System.out.println(m1+"\n"+m2+"\n"+m1.equals(m2));
	}
}
