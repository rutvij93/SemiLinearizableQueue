package queue;


import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import queue.*;

@SuppressWarnings("unused")
public class QueueTestOld {
	public static int THREADS = 2;
	public static int duration = 0;
	public static boolean running = true;
	public static String implementation;
	
	static ConcurrentLinkedQueue<Integer> instance = new ConcurrentLinkedQueue<Integer>();
	static SemiLinearizableQueue<Integer> instance1 = new SemiLinearizableQueue<Integer>();
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {
		int total = 0;
		if (args.length == 3 || args.length == 4) {
			implementation = args[0];
			THREADS = Integer.parseInt(args[1]);
			duration = Integer.parseInt(args[2]);
		}
		else{
			System.out.println("Incorrect Argument Count. Format --> java QueueTest <qname> <threads> <duration> [<n>]");
			return;
		}
		System.out.println("Concurrent Queue");
		long start = System.currentTimeMillis();
		implementation = "LockFree";
		
		
		Thread[] myThreads = new Thread[THREADS];
	    for (int i = 0; i < THREADS; i++) {
	    	myThreads[i] = new EnqDeqThread();
	    }
	    for (int i = 0; i < THREADS; i ++) {
	    	start = System.currentTimeMillis();
	    	myThreads[i].start();
	    }
	    while (System.currentTimeMillis() <= (start + 1000));			    
	    running = false;
	    for (int i = 0; i < THREADS; i++) {
	    	myThreads[i].join();
	    	total = (int) (total + ((EnqDeqThread) myThreads[i]).getOperations()); 
	    }
	    System.out.println("Operations for CconcurrentLinkedQueue	" + total);
	    
	    //SemiLinearizableQueue
	    
	    total = 0;
	    Thread[] myThreads1 = new Thread[THREADS];
	    running = true;
	    implementation = "SemiLinearizable";
	    if (args.length == 4) {
			SemiLinearizableQueue.n = Integer.parseInt(args[3]);
		}
		else{
			System.out.println("Incorrect Argument Count. Format --> java QueueTest <qname> <threads> <duration> [<n>]");
			return;
		}
	    for (int i = 0; i < THREADS; i++) {
	    	myThreads1[i] = new EnqDeqThread();
	    }
	    start = System.currentTimeMillis();
	    for (int i = 0; i < THREADS; i ++) {
	    	myThreads1[i].start();
	    }
	    while (System.currentTimeMillis() <= (start + 1000));		    
	    running = false;
	    for (int i = 0; i < THREADS; i ++) {
	    	myThreads1[i].join();
	    	total = (int) (total + ((EnqDeqThread) myThreads1[i]).getOperations()); 
	    }
	    System.out.println("Throughput for SemiLinearizable	  		" + total);
	}
	
	// Enqueue Call and Dequeue Call
	
	static class EnqDeqThread extends Thread {
		private long number = 0;
		public void run() {
	    	if (implementation.equals("LockFree")){
				while (running){
				    instance.add(1);
					instance.poll();
					number = number + 2;
					long nanos = System.nanoTime();
					while (System.nanoTime() <= (nanos + 1000));	
				}
	    	}
	    	else{
	    		while (running){
				    instance1.enq(1);
					try {
				        int value = (Integer)instance1.deq();
			    	} catch (EmptyException ex) {
			    	}
					number = number + 2;
					long nanos = System.nanoTime();
					while (System.nanoTime() <= (nanos + 1000));
				}
	    	}
	    }
	    public long getOperations() {
			return number;
		}
	}
}
