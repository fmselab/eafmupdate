package fmmutation.mutationoperators.features;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import fmmutation.mutationoperators.FMMutation;
import fmmutation.mutationoperators.features.OrToAndOpt;
import fmmutation.utils.CollectionsUtil;
import fmupdate.models.ExampleTaker;

public class OrToAndOptTest {

	
	@Test
	public void testMutation() throws FileNotFoundException,
			UnsupportedModelException, NoSuchExtensionException {
		IFeatureModel fmodel = ExampleTaker.readExample("examples_fmsfrompreprocessor/TKESSPLC11/Figure4_pv.xml");
		List<FMMutation> res = CollectionsUtil.listFromIterator(OrToAndOpt.instance.mutate(fmodel));

		assertEquals(1,res.size());
		System.out.println("ORIGINAL "+fmodel);
		System.out.println("RESULT "+res.get(0).getFirst());
		
	}

}