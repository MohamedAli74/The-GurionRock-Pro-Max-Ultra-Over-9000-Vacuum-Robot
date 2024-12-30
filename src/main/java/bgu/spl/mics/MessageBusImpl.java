package bgu.spl.mics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	ConcurrentHashMap<MicroService , BlockingQueue<Message>> microServicesQueues;
	ConcurrentHashMap<Class<? extends Event<?>>,List<MicroService>> eventSubscribers;
	ConcurrentHashMap<Class<? extends Broadcast>,List<MicroService>> broadCastSubscribers;
	ConcurrentHashMap<Event<?> ,Future<?>> futures;

	Object lock1 = new Object();//for the functions: sendEvent unregister
	Object lock2 = new Object();//for the functions: sendBroadCast unregister
	Object lock3 = new Object();//for the functions: sendBroadCast awaitMessage
	Object lock4 = new Object();//for the functions: sendEvent awaitMessage


	private static MessageBusImpl instance = null;

	private MessageBusImpl(){
		ConcurrentHashMap<MicroService , BlockingQueue<Message>> microServicesQueues = new ConcurrentHashMap<>();
		ConcurrentHashMap<Class<? extends Event<?>>,List<MicroService>> eventSubscribers = new ConcurrentHashMap<>();
		ConcurrentHashMap<Class<? extends Broadcast>,List<MicroService>> broadCastSubscribers = new ConcurrentHashMap<>();
		ConcurrentHashMap<Event<?> ,Future<?>> futures = new ConcurrentHashMap<>();
	}

	public static synchronized MessageBusImpl getInstance()
	{
		if (instance == null) {
			instance = new MessageBusImpl();
		}
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m)
	{
		eventSubscribers.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m)
	{
		broadCastSubscribers.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result)
	{
		Future f = futures.get(e);
		f.resolve(result);
		futures.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b)
	{
		synchronized (lock2){
			synchronized (lock4){
				List<MicroService> subs = broadCastSubscribers.get(b.getClass());
				for(MicroService m : subs){
					microServicesQueues.get(m.getClass()).add(b);
				}
				lock3.notifyAll();
			}
		}
		List<MicroService> subs = broadCastSubscribers.get(b.getClass());
		for(MicroService m : subs){
			microServicesQueues.get(m.getClass()).add(b);
		}
		//notifyAll();
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e)
	{
		synchronized (lock1) {
			synchronized (lock3) {
				List<MicroService> subs = eventSubscribers.get(e.getClass());
				if(!subs.isEmpty())
				{
					Future<T> f = new Future<>();
					MicroService ms = subs.removeFirst();
					microServicesQueues.get(ms.getClass()).add(e);
					subs.add(ms);
					futures.put(e,f);
					lock3.notifyAll();
					return f;
				}
				return null;
			}
		}
	}

	@Override
	public void register(MicroService m)
	{
		microServicesQueues.put(m,new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m)
	{
		synchronized (lock1) {
			synchronized (lock2) {
				microServicesQueues.remove(m);
				for (List<MicroService> list : eventSubscribers.values()) {
					list.remove(m);
				}
				for (List<MicroService> list : broadCastSubscribers.values()) {
					list.remove(m);
				}
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException
	{
		synchronized (lock3){
			synchronized (lock4){
				while(!microServicesQueues.get(m.getClass()).isEmpty())
				{
					try
					{
						wait();
					}
					catch (InterruptedException e)
					{
						System.out.println(e);
					}
				}
				Message message = microServicesQueues.get(m.getClass()).remove();
				return message;
			}
		}
	}
}
