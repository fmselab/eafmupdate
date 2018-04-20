package fmmutation.mutationoperators.features;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import fmmutation.mutationoperators.FMMutation;
import fmmutation.mutationoperators.features.AlToOr;
import fmmutation.utils.CollectionsUtil;
import fmmutation.utils.Pair;
import fmupdate.models.ExampleTaker;


public class AltToOrTest {

	@Test
	public void test() throws FileNotFoundException, UnsupportedModelException, NoSuchExtensionException {
		IFeatureModel fm = ExampleTaker.readExample("models/model1fi.xml");
		assertTrue(fm.getStructure().getRoot().isAlternative());
		List<FMMutation> res = CollectionsUtil.listFromIterator(AlToOr.instance.mutate(fm));
		// only one mutation
		assertEquals(1, res.size());
		// the original is not mutated
		assertTrue(fm.getStructure().getRoot().toString() + " " + fm.getStructure().getRoot().isAnd() + " " + fm.getStructure().getRoot().isOr(), fm.getStructure().getRoot().isAlternative());
		// the mutated is 
		Pair<IFeatureModel, String> fmm = res.get(0);
		assertNotSame(fmm, fm);
		assertEquals(fmm.toString(), "model",fmm.getFirst().getStructure().getRoot().getFeature().getName());
		assertTrue(fmm.toString(), fmm.getFirst().getStructure().getRoot().isOr());
		
	}

}
