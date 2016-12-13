package queue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueCorrectness {
	public static int THREADS = 10;
	public final static int PER_THREAD = 100;
	int index;
	public static int number = 0;
	public static String implementation;
	
	static ConcurrentLinkedQueue<Integer> instance = new ConcurrentLinkedQueue<Integer>();
	static SemiLinearizableQueue<Integer> instance1 = new SemiLinearizableQueue<Integer>();
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {
		if (args.length == 0){
		}
		else{
			THREADS = Integer.parseInt(args[0]);
		}
		System.out.println("Parallel Dequeue");
		implementation = "LockFree";
	    for (int i = 0; i < THREADS*PER_THREAD; i++) {
	        instance.add(i);
	    }
	    Thread[] myThreads = new Thread[THREADS];
	    for (int i = 0; i < THREADS; i++) {
	    	myThreads[i] = new EnqDeqThread();
	    }
	    for (int i = 0; i < THREADS; i ++) {
	    	myThreads[i].start();
	    }	    
	    for (int i = 0; i < THREADS; i ++) {
	    	myThreads[i].join();
	    }
	    System.out.println("\n Dequeue for LockFree			" + number);
	    
	    //SemiLinearizableQueue
	    number = 0;
	    implementation = "SemiLinearizable";
	    for (int i = 0; i < THREADS*PER_THREAD; i++) {
	        instance1.enq(i);
	    }
	    Thread[] myThreads1 = new Thread[THREADS];
	    for (int i = 0; i < THREADS; i++) {
	    	myThreads1[i] = new EnqDeqThread();
	    }
	    for (int i = 0; i < THREADS; i ++) {
	    	myThreads1[i].start();
	    }	    
	    for (int i = 0; i < THREADS; i ++) {
	    	myThreads1[i].join();
	    }
	    System.out.println("\n Dequeue for SemiLinearizable	  	" + number);
	}
	
	// Enqueue Call and Dequeue Call
	
	static class EnqDeqThread extends Thread {
		public void run() {
	    	if (implementation.equals("LockFree")){
				for (int i = 0; i < PER_THREAD; i++) {
					int value = (Integer)instance.poll();
					System.out.print(value + " ");
				}
				synchronized(this){
					number = number + PER_THREAD;
				}
	    	}
	    	else{
	    		try {
			        for (int i = 0; i < PER_THREAD; i++) {
			        	int value = (Integer)instance1.deq();
			        	System.out.print(value + " ");
			        }
		    	} catch (EmptyException ex) {
		    	}
				synchronized(this){
					number = number + PER_THREAD;
				}
	    	}
	    }
	}
}
