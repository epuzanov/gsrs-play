package ix.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class UniqueStackTest {
	
	@Test
	public void testUniqueStackIsEmptyAfterAddingAndRemoving(){
		UniqueStack<String> ustack = new UniqueStack<>();
		String string1 = "S1";
		String string2 = "S2";
		String string3 = "S3";
		ustack.push(string1);
		ustack.push(string2);
		ustack.push(string3);
		
		ustack.pop();
		ustack.pop();
		ustack.pop();
		
		assertEquals(0,ustack.asStream().count());
		
		
	}
	
	@Test
	public void testUniqueStackPushAndPopRunnableHasSameHead(){
		UniqueStack<String> ustack = new UniqueStack<>();
		ustack.pushAndPopWith("TEST", ()->{
			assertEquals("TEST",ustack.getFirst());
		});
			
	}
	
	@Test
	public void addingThingsShouldBePresent(){
		UniqueStack<String> ustack = new UniqueStack<>();
		
		List<String> l =Stream.of("S1","S2","S3","S4").collect(Collectors.toList());
		
		l.stream().forEach(s->{
			ustack.push(s);
		});
		
		
		
		assertEquals(l.size(),ustack.asStream().count());
		
		l.stream().forEach(s->{
			assertTrue(ustack.contains(s));
		});
			
	}
	@Test
	public void addingDuplicateThingsShouldNotBePresent(){
		UniqueStack<String> ustack = new UniqueStack<>();
		
		List<String> l =Stream.of("S1","S2","S3","S4", "S1").collect(Collectors.toList());
		
		l.stream().forEach(s->{
			ustack.push(s);
		});
		
		assertEquals(l.size()-1,ustack.asStream().count());
		assertTrue(ustack.asStream().collect(Collectors.toList()).containsAll(l));
	}
	
	@Test
	public void addingThingsAndRemovingShouldBeGone(){
		UniqueStack<String> ustack = new UniqueStack<>();
		
		List<String> l =Stream.of("S1","S2","S3","S4").collect(Collectors.toList());
		
		l.stream().forEach(s->{
			ustack.push(s);
		});
		l.stream().forEach(s->{
			ustack.pop();
		});
		l.stream().forEach(s->{
			assertTrue(!ustack.contains(s));
		});
			
	}

}
