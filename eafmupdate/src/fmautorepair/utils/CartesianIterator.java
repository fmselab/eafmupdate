package fmautorepair.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CartesianIterator <T> implements Iterator <List <T>> {

    private final List <List <T>> lilio;    
    private int current = 0;
    private final long last;

    public CartesianIterator (final List <T> ... llo) {
    	this(Arrays.asList(llo));
    }
    
    public CartesianIterator (final List <List <T>> llo) {
        lilio = llo;
        long product = 1L;
        for (List <T> lio: lilio)
            product *= lio.size ();
        last = product;
    } 

    @Override
	public boolean hasNext () {
        return current != last;
    }

    @Override
	public List <T> next () {
        ++current;
        return get (current - 1, lilio);
    }

    @Override
	public void remove () {
        ++current;
    }

    private List<T> get (final int n, final List <List <T>> lili) {
        switch (lili.size ())
        {
            case 0: return new ArrayList <T> (); // no break past return;
            default: {
                List <T> inner = lili.get (0);
                List <T> lo = new ArrayList <T> ();
                lo.add (inner.get (n % inner.size ()));
                lo.addAll (get (n / inner.size (), lili.subList (1, lili.size ())));
                return lo;
            }
        }
    }
}

class CartesianIterable <T> implements Iterable <List <T>> {

    private List <List <T>> lilio;  

    public CartesianIterable (List <List <T>> llo) {
        lilio = llo;
    }

    @Override
	public Iterator <List <T>> iterator () {
        return new CartesianIterator <T> (lilio);
    }
}