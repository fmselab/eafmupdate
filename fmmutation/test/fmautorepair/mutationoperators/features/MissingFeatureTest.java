package fmautorepair.mutationoperators.features;

import java.io.FileNotFoundException;

import org.junit.Test;

import de.ovgu.featureide.fm.core.io.UnsupportedModelException;

public class MissingFeatureTest {

	@Test
	public void testMutateFeatureModelFeature() throws FileNotFoundException,
			UnsupportedModelException {
		/*IFeatureModel fmodel = Utils
				.readSPLOTModel("splotmodels_new/model_20091129_1734444143.xml");
		assertNotNull(fmodel);
		Node original = NodeCreator.createNodes(fmodel);
		System.err.println(original.toString());
		Iterator<FMMutation> mutations = RemoveFeature.instance.mutate(fmodel);
		while(mutations.hasNext()){
			FMMutation mutation = mutations.next();
			Node ffm = NodeCreator.createNodes(mutation.getFirst());
			System.out.println(ffm.toString());	
		}*/
	}

}
