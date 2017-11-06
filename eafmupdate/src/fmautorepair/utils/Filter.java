package fmautorepair.utils;
public interface Filter<T> {

    /**
     * Determines whether elements should be filtered or not.
     * 
     * @param element the element to be matched against the filter
     * @return {@code true} if the element matches the filter, otherwise {@code false}
     */
    public boolean matches(T element);
}