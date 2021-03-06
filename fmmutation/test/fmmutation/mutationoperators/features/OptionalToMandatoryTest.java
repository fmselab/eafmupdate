package fmmutation.mutationoperators.features;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import fmmutation.mutationoperators.FMMutation;
import fmmutation.mutationoperators.features.OptToMan;
import fmmutation.utils.CollectionsUtil;
import fmupdate.models.ExampleTaker;


public class OptionalToMandatoryTest {

	@Test
	public void testOr2Mandatory() throws FileNotFoundException,
			UnsupportedModelException, NoSuchExtensionException {
		IFeatureModel fmodel = ExampleTaker.readExample("models/modelfman.xml");
		// take a feature with an or
		IFeature g = fmodel.getFeature("g");
	}

	@Test
	public void testMutation() throws FileNotFoundException,
			UnsupportedModelException, NoSuchExtensionException {
		IFeatureModel fmodel = ExampleTaker.readExample("models/modelfman.xml");
		List<FMMutation> res = CollectionsUtil.listFromIterator(OptToMan.instance.mutate(fmodel));	
		assertEquals(1,res.size());		
	}

	@Test
	public void testMutation2() throws FileNotFoundException,
			UnsupportedModelException, NoSuchExtensionException {
		IFeatureModel fmodel = ExampleTaker.readExample("models/model3.xml");
		List<FMMutation> res = CollectionsUtil.listFromIterator(OptToMan.instance.mutate(fmodel));	
		assertEquals(2,res.size());		
	}

	
}
