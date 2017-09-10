package ix.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tuple<K,V>{
	private K k;
	private V v;
	public Tuple(K k,V v){
		this.k=k;
		this.v=v;
	}
	
	@JsonProperty("key")
	public K k(){
		return k;
	}
	
	@JsonProperty("value")
	public V v(){
		return v;
	}
	
	
	public void consume(ThrowableBiConsumer<K,V> c){
	    c.accept(k, v);
	}
	
	public <T> T map(BiFunction<K,V,T> c){
        return c.apply(k, v);
    }
	
	
	public static <K,V> Tuple<K,V> of(K k, V v){
		return new Tuple<K,V>(k,v);
		
	}
	
	public Tuple<V,K> swap(){
		return Tuple.of(this.v(),this.k());
	}
	
	/**
	 * Maps the "value" (2nd element) of a Tuple to a new value,
	 * while keeping the "key" (1st element) in tact. 
	 * @param fun
	 * @return
	 */
	public static <K,V,U> Function<Tuple<K,V>, Tuple<K,U>> vmap(Function<V,U> fun){
	    return (t)->{
	        return Tuple.of(t.k(),fun.apply(t.v()));
	    };
    }
	
	
	/**
     * Maps the "key" (1st element) of a Tuple to a new value,
     * while keeping the "value" (2nd element) in tact. 
     * @param fun
     * @return
     */
	public static <K,V,L> Function<Tuple<K,V>, Tuple<L,V>> kmap(Function<K,L> fun){
        return (t)->{
            return Tuple.of(fun.apply(t.k()),t.v());
        };
    }
	
	
	
	/**
	 * Used to make a conversion from an {@link Entry} to
	 * a tuple.
	 * 
	 * @param ent
	 * @return
	 */
	public static <K,V> Tuple<K,V> of(Entry<K,V> ent){
		return Tuple.of(ent.getKey(),ent.getValue());
		
	}
	
	public static<K,V> BiFunction<K,V,Tuple<K,V>> map(){
		return (k,v)->{
			return new Tuple<K,V>(k,v);
		};
	}
	
	/**
	 * Collector to go from a stream of tuples to a map
	 * of the internal keys and values
	 * @return
	 */
	public static <T,U> Collector<Tuple<T,U>,?,Map<T,U>> toMap(){
    	return Collectors.toMap(e->e.k(), e->e.v());
    }
	
	/**
     * Collector to go from a stream of tuples to a map
     * grouped by the keys (1st element), with a list of the
     * valued (2nd element)
     * @return
     */
    public static <T,U> Collector<Tuple<T,U>,?,Map<T,List<U>>> toGroupedMap(){
        return toGroupedMap(Collectors.toList());
    }
    
    /**
     * Collector to go from a stream of tuples to a map
     * grouped by the keys (1st element), with a provided
     * collector for the values in each group
     * @return
     */
    public static <T,U,V> Collector<Tuple<T,U>,?,Map<T,V>> toGroupedMap(Collector<U,?,V> collect){
        return Collectors.groupingBy(t->t.k(), Collectors.mapping(t->t.v(), collect));
    }
	
	
	@Override
	public int hashCode(){
	    return this.k.hashCode() ^ (this.v.hashCode() ^ 0xDEADBEEF);
	}
	
	@Override
	public boolean equals(Object o){
	    if(o == this)return true;
	    if(!(o instanceof Tuple)){
	       return false; 
	    }

	    Tuple<K,V> tup2 = (Tuple<K,V>)o;
	    if(!(tup2.k.equals(this.k))){
	        return false;
	    }
	    if(!(tup2.v.equals(this.v))){
            return false;
        }
	    return true;
	    
	}
	
	public static <K,V> Set<Tuple<K,V>> toTupleSet(Map<K,V> map){
	    return map.entrySet().stream().map(Tuple::of).collect(Collectors.toSet());
	}
	
	@Override
	public String toString(){
	    return "<" + this.k.toString() + "," + this.v.toString() + ">";
	}
	
	public static interface ThrowableBiConsumer<K,V> extends BiConsumer<K,V>{
	    public void throwing(K k, V v) throws Exception;
	    
	    public default void accept(K k, V v){
            try {
                this.throwing(k, v);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
	}

    public static <K,V,T> Function<Tuple<K,V>, Stream<Tuple<K,T>>> vstream(Function<V,Stream<T>> smap) {
        return (t)->{
           return smap.apply(t.v())
                      .map(v->Tuple.of(t.k(),v));
        };
    }
    
    public static <K,V,T> Function<Tuple<K,V>, Stream<Tuple<T,V>>> kstream(Function<K,Stream<T>> smap) {
        return (t)->{
           return smap.apply(t.k())
                      .map(k->Tuple.of(k,t.v()));
        };
    }
	
    /**
     * Returns a version of this Tuple that uses the hashcode and
     * equals methods from the k value for equality testing.
     * @return
     */
    public KEqualityTuple<K,V> withKEquality(){
    	return new KEqualityTuple<K,V>(this.k, this.v);
    }
    
    
    /**
     * Utility tuple class for doing uniqueness testing based on a specific key value.
     * 
     * @author tyler
     *
     * @param <K>
     * @param <V>
     */
    public static class KEqualityTuple<K,V> extends Tuple<K,V>{

		public KEqualityTuple(K k, V v) {
			super(k, v);
		}
		
		@Override
		public boolean equals(Object o){
			if(o==null)return false;
			if(!(o instanceof Tuple))return false;
			Tuple<K,V> other = (Tuple<K,V>)o;
			if(this.k().equals(other.k()))return true;
			return false;
		}
		
		@Override
		public int hashCode(){
			if(this.k()==null)return 0;
			return this.k().hashCode() ^ 0xBABAFEEF;
		}
    	
    }
    
    
}