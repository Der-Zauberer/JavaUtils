package eu.derzauberer.javautils.observables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * An ArrayList which calls all observers as soon as elements gets
 * added or removed from the {@link ArrayList}.
 *
 * @param <T> the type of elements in this list
 */
public class ObservableArrayList<T> extends ArrayList<T> implements Observable {
	
	private static final long serialVersionUID = 1L;
	private static final List<Observer> observerList = new ArrayList<>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(int index, T element) {
		updateObserver();
		super.add(index, element);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(T e) {
		updateObserver();
		return super.add(e);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(Collection<? extends T> c) {
		updateObserver();
		return super.addAll(c);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		updateObserver();
		return super.addAll(index, c);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		updateObserver();
		super.clear();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T remove(int index) {
		updateObserver();
		return super.remove(index);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Object o) {
		updateObserver();
		return super.remove(o);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		updateObserver();
		return super.removeAll(c);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeIf(Predicate<? super T> filter) {
		updateObserver();
		return super.removeIf(filter);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void replaceAll(UnaryOperator<T> operator) {
		updateObserver();
		super.replaceAll(operator);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		updateObserver();
		return super.retainAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Observer> getObserverList() {
		return observerList;
	}
	
}
