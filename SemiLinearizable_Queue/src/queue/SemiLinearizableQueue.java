package queue;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SemiLinearizableQueue<T>  {
	public static ThreadLocal<Integer> q = new ThreadLocal<Integer>();
	AtomicInteger size;
	public static int enq = 0;
	public static int deq = 0;
	private AtomicReference<Node> head;
	private AtomicReference<Node> tail;
	public static int n = 5;
	int capacity;
	public SemiLinearizableQueue() {
		Node sentinel = new Node(null);
	    this.head = new AtomicReference<Node>(sentinel);
	    this.tail = new AtomicReference<Node>(sentinel);
	}
	
	// Random Dequeue
	
	public T randomdeq(int item, Node next) throws EmptyException {
		int i = 0;
		while (i < item){
			if (next.next.get() == null){
				break;
			}
			next = next.next.get();
			i = i + 1;
		}

		if (next.marked.compareAndSet(false, true)){
			return next.value;
		}
		else
			return null;
	}
	
	//Enqueue

	public void enq(T item) {
	    if (item == null) throw new NullPointerException();
	    Node node = new Node(item); 				
	    while (true) {		 						
	    	Node last = tail.get();    				
	    	Node next = last.next.get(); 			
	    	if (last == tail.get()) {
	    		if (next == null) {
	    			if (last.next.compareAndSet(next, node)) {
	    				tail.compareAndSet(last, node);
	    				synchronized(this)
	    				{
	    					enq = enq + 1;
	    				}
	    				return;
	    			}
	    		} else {
	    			tail.compareAndSet(last, next);
	    		}
	    	}
	    }
  }
	
	// Dequeue
	
	public T deq() throws EmptyException {	
		q.set(0);
	    while (true) {
	    	Node first = head.get();
		    Node last = tail.get();
		    Node next = first.next.get();
		    if (first == head.get()) {												
			    if (first == last) {												
				    if (next == null) {												
				    	throw new EmptyException();
				    }	
				    tail.compareAndSet(last, next);									
				} 
			    else {
			    	int random_dequeue = 0;
			    	if (n > 1){
			    		if (q.get() < n*n){
				    		random_dequeue = ThreadLocalRandom.current().nextInt(0,n);
				    		if (random_dequeue > 0){
				    			T value = randomdeq(random_dequeue, next);					
				    			if (value != null){
				    				synchronized(this)
				    				{
				    					deq = deq + 1;
				    				}
				    				return value;
				    			}	
				    		}
				    		if (next != null && next.marked.get() == true){					
				    			if (next != last && head.compareAndSet(first, next)){		
				    				first = next;
				    				next = next.next.get();
				    			}
				    			if (next == null)
				    				return null;
				    			if (next.marked.compareAndSet(false, true)){
				    				synchronized(this)
				    				{
				    					deq = deq + 1;
				    				}
				    				return next.value;
				    			}
				    		}
				    		q.set(q.get() + 1);
				    	}
				    	else{
				    		if (first == head.get()) {										
				    	        if (first == last) {	
				    	          if (next == null) {	
				    	            throw new EmptyException();
				    	          }
				    	   
				    	          tail.compareAndSet(last, next);
				    	        } else {
				    	          T value = next.value; 
				    	          if (head.compareAndSet(first, next)){
				    	        	  synchronized(this)
				    	        	  {
					    					deq = deq + 1;
				    	        	  }
				    	        	  return value;
				    	          }  
				    	        }
				    	    }
				    	}
			    	}
			    	else{
			    		if (first == head.get()) {											
			    	        if (first == last) {	
			    	          if (next == null) {	
			    	            throw new EmptyException();
			    	          }
			    	   
			    	          tail.compareAndSet(last, next);
			    	        } else {
			    	          T value = next.value; 
			    	          if (head.compareAndSet(first, next)){
			    	        	  synchronized(this)
			    	        	  {
				    					deq = deq + 1;
				    				}
			    	        	  return value;
			    	          }  
			    	        }
			    	    }
			    	}
				}
			}
	    }
	}	
	// Node
	
	protected class Node {
		public T value;
	    public AtomicReference<Node> next;
	    AtomicBoolean marked = new AtomicBoolean();
	    public Node(T value) {
	    	this.value = value;
	    	this.next  = new AtomicReference<Node>(null);
	    	marked.set(false);
	    }
	}	
}
