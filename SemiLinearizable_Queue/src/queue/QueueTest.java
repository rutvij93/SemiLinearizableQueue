package queue;


import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import queue.*;

@SuppressWarnings("unused")
public class QueueTest {
	public static int THREADS = 2;
	public static int duration = 0;
	public static boolean running = true;
	public static String implementation;
	
	static LinkedBlockingQueue<Integer> instance = new LinkedBlockingQueue<Integer>();
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
		
		//L-Queue
		
		long start;
		if (implementation.equals("LQueue")){
			System.out.println("L-Queue: JAVA Atomic Concurrent package");
			Thread[] myThreads = new Thread[THREADS];
		    for (int i = 0; i < THREADS; i++) {
		    	myThreads[i] = new EnqDeqThread(i);
		    }
		    start = System.currentTimeMillis();
		    for (int i = 0; i < THREADS; i ++) {
		    	myThreads[i].start();
		    }
		    while (System.currentTimeMillis() <= (start + 1000));		    
		    running = false;
		    for (int i = 0; i < THREADS; i ++) {
		    	myThreads[i].join();
		    	total = (int) (total + ((EnqDeqThread) myThreads[i]).getOperations()); 
		    }
		    System.out.println(total/2 + " " + total/2 + " " + 0);
		    System.out.println("Throughput for LQueue	" + total);
		}
	    
	    //SemiLinearizableQueue
		else if(implementation.equals("SLQueue")){
			if (args.length == 4) {
				SemiLinearizableQueue.n = Integer.parseInt(args[3]);
			}
			else{
				System.out.println("Incorrect Argument Count. Format --> java QueueTest <qname> <threads> <duration> [<n>]");
				return;
			}
			System.out.println(SemiLinearizableQueue.n + " Semi Linearizable Queue");
			total = 0;
		    Thread[] myThreads1 = new Thread[THREADS];
		    running = true;
		    implementation = "SemiLinearizable";
		    for (int i = 0; i < THREADS; i++) {
		    	myThreads1[i] = new EnqDeqThread(i);
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
		    System.out.println(SemiLinearizableQueue.enq + " " + SemiLinearizableQueue.deq + " " + (SemiLinearizableQueue.enq - SemiLinearizableQueue.deq));
		    System.out.println("Throughput for SLQueue	" + total);
		}
		else{
			System.out.println("Incorrect Argument.");
			return;
		}
	}
	
	// Enqueue Call and Dequeue Call
	
	static class EnqDeqThread extends Thread {
		
		private long number = 0;
		int value;
	    EnqDeqThread(int i) {
		    value = i;
	    }
		public void run() {
	    	if (implementation.equals("LQueue")){
				while (running){
				    instance.add(1);
					int value = (Integer)instance.poll();
					number = number + 2;
					long nanos = System.nanoTime();
					while (System.nanoTime() <= (nanos + 10));
				}
	    	}
	    	else{
	    		while (running){
	    			int a = ThreadLocalRandom.current().nextInt(0,2);	
	    			if (a == 0){
	    				instance1.enq(1);
	    			}
	    			else{
	    				try {																	//For Random Enqueue dequeue use this
					        int value = (Integer)instance1.deq();
				    	} catch (EmptyException ex) {
				    	}
	    			}
	    			number = number + 1;
	    			
	    			instance1.enq(1);


					
				}
	    	}
	    }
	    public long getOperations() {
			return number;
		}
	}
}
