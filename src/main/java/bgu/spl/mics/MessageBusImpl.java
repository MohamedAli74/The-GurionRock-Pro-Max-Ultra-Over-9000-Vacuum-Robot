package bgu.spl.mics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	ConcurrentHashMap<MicroService , BlockingQueue<Event<?>>> microServicesQueues = new ConcurrentHashMap<>();
	ConcurrentHashMap<Class<? extends Event<?>>,List<MicroService>> eventSubscribers = new ConcurrentHashMap<>();
	ConcurrentHashMap<Class<? extends Broadcast>,List<MicroService>> broadCastSubscribers = new ConcurrentHashMap<>();
	ConcurrentHashMap<Event<?> ,Future<?>> futures = new ConcurrentHashMap<>();

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubscribers.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadCastSubscribers.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> f = futures.get(e);
		f.resolve(result);
		futures.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		List subs = broadCastSubscribers.get(b);
		for(MicroService m : subs){
			microServicesQueues.get(m).put(b);
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
