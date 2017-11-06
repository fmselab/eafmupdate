package org.prop4j;

import java.text.Normalizer;

public class NameNormalizer {

	// return the normalized version of a string
	public static String normalize(CharSequence x) {
		return Normalizer.normalize(x, Normalizer.Form.NFD)
				.replaceAll("[^\\p{ASCII}]", "").replace(",", "y");
	}

}
