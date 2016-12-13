# SemiLinearizableQueue
Highly concurrent servers mainly rely on thread pool which consist of (1) set of threads ready to serve tasks (2) task queue. More than often these thread pools do not need a strict FIFO queue, what is required is a queue with relaxed consistency.
