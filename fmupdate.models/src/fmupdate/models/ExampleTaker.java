package fmupdate.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.impl.FMFactoryManager;
import de.ovgu.featureide.fm.core.init.FMCoreLibrary;
import de.ovgu.featureide.fm.core.init.LibraryManager;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelFormat;

/**
 * takes an example of feature ide - it must work in every project
 */
public class ExampleTaker {
	
	static {
		LibraryManager.registerLibrary(FMCoreLibrary.getInstance());
	}

	/**
	 * read an example (to be used also in another project)
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws UnsupportedModelException
	 * @throws NoSuchExtensionException
	 */
	static public IFeatureModel readExample(String path)
			throws FileNotFoundException, UnsupportedModelException, NoSuchExtensionException {
		// get the path relative to THIS project
		URL location = ExampleTaker.class.getProtectionDomain().getCodeSource().getLocation();
		String rootPath = location.getPath().replaceAll("%20", " ");
		String completePath = rootPath + "../" + path;		
		return readFeatureModel(completePath);
	}
	/**
	 * 
	 * @param completePath
	 * @return
	 */
	public static IFeatureModel readFeatureModel(String completePath) {
		Path path = new File(completePath).toPath();
		IFeatureModel fm = FeatureModelManager.load(path);
		assert fm != null;
		XmlFeatureModelFormat format = new XmlFeatureModelFormat();
		File exampleLocation = new File(completePath);
		assert exampleLocation.exists() : completePath;
		// load file
		FileHandler.load(exampleLocation.toPath(), fm, format);
		return fm;
	}

}
