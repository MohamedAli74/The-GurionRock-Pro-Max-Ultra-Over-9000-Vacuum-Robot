package bgu.spl.mics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServicesQueues;
	ConcurrentHashMap<Class<? extends Event<?>>, List<MicroService>> eventSubscribers;
	ConcurrentHashMap<Class<? extends Broadcast>, List<MicroService>> broadCastSubscribers;
	ConcurrentHashMap<Event<?>, Future<?>> futures;

	Object lock1 = new Object();//for the functions: sendEvent unregister
	Object lock2 = new Object();//for the functions: sendBroadCast unregister
	Object lock3 = new Object();//for the functions: sendBroadCast awaitMessage
	Object lock4 = new Object();//for the functions: sendEvent awaitMessage


	private static MessageBusImpl instance = null;

	private MessageBusImpl() {
		this.microServicesQueues = new ConcurrentHashMap<>();
		this.eventSubscribers = new ConcurrentHashMap<>();
		this.broadCastSubscribers = new ConcurrentHashMap<>();
		this.futures = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		if (instance == null) {
			instance = new MessageBusImpl();
		}
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubscribers.computeIfAbsent(type, k -> new ArrayList<>());
		synchronized (type) {
			eventSubscribers.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadCastSubscribers.computeIfAbsent(type, k -> new ArrayList<>());
		synchronized (type) {
			broadCastSubscribers.get(type).add(m);
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future f = futures.get(e);
		f.resolve(result);
		futures.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (b.getClass()) {
			if (broadCastSubscribers.contains(b.getClass())) {
				for (MicroService m : broadCastSubscribers.get(b.getClass())) {
					BlockingQueue<Message> queue = microServicesQueues.get(m);
					if (queue != null)
					{
						queue.add(b);
					}
				}
			}
		}
		}



	@Override
	public <T> Future<T> sendEvent(Event<T> e)
	{
		MicroService m;
		Future<T> future = new Future<>();
		synchronized (e.getClass()) {
			if (eventSubscribers.get(e.getClass()) == null || !eventSubscribers.containsKey(e.getClass())) {
				return null;
			}
			futures.put(e, future);
			List<MicroService> list = eventSubscribers.get(e.getClass());
			if (list == null) {//if no queue then no one has registered to it yet, or already unregistered
				return null;
			}
			m = list.remove(0);
			if (m == null) {
				return null;
			}
			list.add(m);
		}

		synchronized (m) {
			BlockingQueue<Message> queue = microServicesQueues.get(m);
			if (queue == null) {
				return null;
			}
			queue.add(e);
		}
		return future;
	}

	@Override
	public void register(MicroService m)
	{
		microServicesQueues.put(m,new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m)
	{
		if (microServicesQueues.containsKey(m)) {
			BlockingQueue<Message> q;
			synchronized (m) {
				for (Class<? extends Broadcast> type : broadCastSubscribers.keySet()) {
					synchronized (type) {
						broadCastSubscribers.get(type).remove(m);
					}
				}

				for (Class<? extends Event> type : eventSubscribers.keySet()) {
					synchronized (type) {
						eventSubscribers.get(type).remove(m);
					}
				}

				q = microServicesQueues.remove(m);

				if (q == null) {
					return;
				}
			}
			while (!q.isEmpty()) {
				Message message = q.poll();
				if (message != null) {
					Future<?> future = futures.get(message);
					if (future != null) {
						future.resolve(null);
					}
				}
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException
	{
		BlockingQueue<Message> queue = microServicesQueues.get(m);
		if (queue == null) {
			throw new IllegalArgumentException("MicroService is not registered!");
		}
		Message msg = null;
		synchronized (queue) {
			try {
				msg = queue.take();
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
		return msg;
	}

	public ConcurrentHashMap<Class<? extends Broadcast>, List<MicroService>> getBroadCastSubscribers() {
		return broadCastSubscribers;
	}

	public ConcurrentHashMap<Class<? extends Event<?>>, List<MicroService>> getEventSubscribers() {
		return eventSubscribers;
	}

	public ConcurrentHashMap<Event<?>, Future<?>> getFutures() {
		return futures;
	}

	public ConcurrentHashMap<MicroService, BlockingQueue<Message>> getMicroServicesQueues() {
		return microServicesQueues;
	}
}
