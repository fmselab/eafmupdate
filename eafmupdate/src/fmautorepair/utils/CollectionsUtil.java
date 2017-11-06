package fmautorepair.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CollectionsUtil {

	static public <T> List<T> listFromIterator(Iterator<T> iterator) {

		List<T> result = new LinkedList<T>();
		while (iterator.hasNext()) {
			T next = iterator.next();
			if (next == null)
				throw new RuntimeException("itaerator with null next");
			result.add(next);
		}
		return result;
	}
}
