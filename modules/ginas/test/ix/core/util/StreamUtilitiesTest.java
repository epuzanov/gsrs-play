package ix.core.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

import ix.ginas.utils.StreamUtil;
import ix.test.AbstractGinasTest;



public class StreamUtilitiesTest extends AbstractGinasTest{
	
	
	
	@Test
	public void ensureIteatorStreamUtilForListIsEquivalentToStreamFromList(){
		List<String> names = new ArrayList<String>();
		names.add("ABC");
		names.add("DEF");
		names.add("ABC");
		names.add("1234");
		Iterator<String> it=names.iterator();
		
		assertEquals(names, StreamUtil.ofIterator(it).collect(Collectors.toList()));
		
	}
	
	@Test
	public void ensureGeneratorUtilForListIsEquivalentToStreamFromList(){
		List<String> names = new ArrayList<String>();
		names.add("ABC");
		names.add("DEF");
		names.add("ABC");
		names.add("1234");
		Iterator<String> it=names.iterator();
		
		assertEquals(names, StreamUtil.forGenerator(()->(it.hasNext())?Optional.of(it.next()):Optional.empty()).collect(Collectors.toList()));
		
	}
	
	@Test
	public void ensureNullableGeneratorUtilForListIsEquivalentToStreamFromList(){
		List<String> names = new ArrayList<String>();
		names.add("ABC");
		names.add("DEF");
		names.add("ABC");
		names.add("1234");
		Iterator<String> it=names.iterator();
		
		assertEquals(names, StreamUtil.forNullableGenerator(()->(it.hasNext())?it.next():null).collect(Collectors.toList()));
		
	}

}