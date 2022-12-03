package eu.derzauberer.javautils.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ObservableArrayList<T> extends ArrayList<T> implements Observable {
	
	private static final long serialVersionUID = 1L;
	private static final List<Observer> observerList = new ArrayList<>();
	
	@Override
	public void add(int index, T element) {
		updateObserver();
		super.add(index, element);
	}
	
	@Override
	public boolean add(T e) {
		updateObserver();
		return super.add(e);
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		updateObserver();
		return super.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		updateObserver();
		return super.addAll(index, c);
	}
	
	@Override
	public void clear() {
		updateObserver();
		super.clear();
	}
	
	@Override
	public T remove(int index) {
		updateObserver();
		return super.remove(index);
	}
	
	@Override
	public boolean remove(Object o) {
		updateObserver();
		return super.remove(o);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		updateObserver();
		return super.removeAll(c);
	}
	
	@Override
	public boolean removeIf(Predicate<? super T> filter) {
		updateObserver();
		return super.removeIf(filter);
	}
	
	@Override
	public void replaceAll(UnaryOperator<T> operator) {
		updateObserver();
		super.replaceAll(operator);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		updateObserver();
		return super.retainAll(c);
	}

	@Override
	public List<Observer> getObserverList() {
		return observerList;
	}
	
}
