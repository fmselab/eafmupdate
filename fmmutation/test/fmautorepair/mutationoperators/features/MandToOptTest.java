package fmautorepair.mutationoperators.features;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.editing.NodeCreator;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import fmautorepair.mutationoperators.FMMutation;
import fmautorepair.utils.CollectionsUtil;
import fmupdate.models.ExampleTaker;


public class MandToOptTest {

	@Test
	public void testFalseMandatory() throws FileNotFoundException,
			UnsupportedModelException, NoSuchExtensionException {
		IFeatureModel fmodel = ExampleTaker.readModel("models/modelfman.xml");
		// a1 is mandatory ma non reale (non mutare)
		IFeature a1 = fmodel.getFeature("a1");
		assertTrue(a1.getStructure().isMandatory());
		assertFalse(MandToOpt.isTrueMandatory(a1));
		// B is mandatory reale (da mutare)
		IFeature B = fmodel.getFeature("B");
		assertTrue(B.getStructure().isMandatory());
		assertTrue(MandToOpt.isTrueMandatory(B));
		// f is optional
		IFeature f = fmodel.getFeature("f");
		assertFalse(f.getStructure().isMandatory());
		// d is mandatory reale (da mutare)
		IFeature d = fmodel.getFeature("d");
		assertTrue(d.getStructure().isMandatory());
		assertTrue(MandToOpt.isTrueMandatory(d));
		//
		IFeature c1 = fmodel.getFeature("c1");
		assertTrue(c1.getStructure().isMandatory());
		assertFalse(MandToOpt.isTrueMandatory(c1));
		IFeature c2 = fmodel.getFeature("c2");
		assertTrue(c2.getStructure().isMandatory());
		assertFalse(MandToOpt.isTrueMandatory(c2));
		IFeature g = fmodel.getFeature("g");
		assertTrue(g.getStructure().isMandatory());
		assertFalse(MandToOpt.isTrueMandatory(g));
		// i is mandatoria reale
		IFeature i = fmodel.getFeature("i");
		assertTrue(i.getStructure().isMandatory());
		assertTrue(MandToOpt.isTrueMandatory(i));
		// w is not mandatory
		IFeature w = fmodel.getFeature("w");
		assertTrue(w.getStructure().isMandatory());
		assertFalse(MandToOpt.isTrueMandatory(w));
	}

	@Test
	public void testMutation() throws FileNotFoundException,
			UnsupportedModelException, NoSuchExtensionException {
		IFeatureModel fmodel = ExampleTaker.readModel("models/modelfman.xml");
		List<FMMutation> res = CollectionsUtil.listFromIterator(MandToOpt.instance.mutate(fmodel));	
		assertEquals(4,res.size());		
	}

	@Test
	public void testMutation4() throws FileNotFoundException,
			UnsupportedModelException, NoSuchExtensionException {
		IFeatureModel fmodel = ExampleTaker.readModel("models/model4.xml");
		List<FMMutation> res = CollectionsUtil.listFromIterator(MandToOpt.instance.mutate(fmodel));	
		assertEquals(2,res.size());		
	}
	
	@Test
	public void testMutation1Mandatory() throws FileNotFoundException,
			UnsupportedModelException, NoSuchExtensionException {
		IFeatureModel fmodel = ExampleTaker.readModel("models/model_one_mandatory.xml");
		List<FMMutation> res = CollectionsUtil.listFromIterator(MandToOpt.instance.mutate(fmodel));	
		assertEquals(1,res.size());
		// converto
		Node ffm = NodeCreator.createNodes(fmodel,false);
		//IdExpressionCreator iec = new IdExpressionCreator();		
		// constraints impliciti ed ecpliciti (TUTTI)
		//ArrayList<Expression> constraints = new ArrayList<>(NodeToExpression.nodeToConstraints(ffm,iec));
		//System.out.println(constraints);
	}
}
