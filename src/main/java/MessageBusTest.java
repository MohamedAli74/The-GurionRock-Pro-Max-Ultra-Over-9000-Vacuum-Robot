import bgu.spl.mics.*;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;


public class MessageBusTest {
    @Test
    public void registerTest() {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        //eventSender
        MicroService microService = new ExampleMessageSenderService("microServic", new String[]{"event"});
        messageBus.register(microService);
        assertNotNull("queue must not be null ", messageBus.getMicroServicesQueues().get(microService));
        messageBus.unregister(microService);
        //broadcastSender
        MicroService microService1 = new ExampleMessageSenderService("microServic", new String[]{"broadcast"});
        messageBus.register(microService);
        assertNotNull("queue must not be null ", messageBus.getMicroServicesQueues().get(microService));
        messageBus.unregister(microService1);
    }

    @Test
    public void subscribeEventTest() {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        //eventSender
        MicroService microService = new ExampleMessageSenderService("microServic", new String[]{"event"});
        messageBus.register(microService);
        ExampleEvent event = new ExampleEvent("microService");
        messageBus.subscribeEvent(event.getClass(), microService);
        messageBus.getEventSubscribers().get(event.getClass());
        List<MicroService> list = messageBus.getEventSubscribers().get(event);
        assertNotNull("list must not be null ", list);
        assertTrue("list must contain the microservice ", list.contains(microService));
        assertEquals("list size must be = 1 ", 1, list.size());
        messageBus.unregister(microService);
        //broadCastSender
        MicroService microService1 = new ExampleMessageSenderService("microServic", new String[]{"broadcast"});
        messageBus.register(microService);
        ExampleEvent event1 = new ExampleEvent("microService1");
        messageBus.subscribeEvent(event1.getClass(), microService1);
        messageBus.getEventSubscribers().get(event1.getClass());
        List<MicroService> list1 = messageBus.getEventSubscribers().get(event1);
        assertNotNull("list1 must not be null ", list1);
        assertTrue("list1 must contain the microservice ", list1.contains(microService1));
        assertEquals("list1 size must be = 1 ", 1, list1.size());
        messageBus.unregister(microService1);
    }

    public void subscribeBroadCastTest() {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        //eventSender
        MicroService microService = new ExampleMessageSenderService("microServic", new String[]{"event"});
        messageBus.register(microService);
        ExampleBroadcast broadcast = new ExampleBroadcast("microService");
        messageBus.subscribeBroadcast(broadcast.getClass(), microService);
        messageBus.getBroadCastSubscribers().get(broadcast.getClass());
        List<MicroService> list = messageBus.getBroadCastSubscribers().get(broadcast);
        assertNotNull("list must not be null ", list);
        assertTrue("list must contain the microservice ", list.contains(microService));
        assertEquals("list size must be = 1 ", 1, list.size());
        messageBus.unregister(microService);
        //broadCastSender
        MicroService microService1 = new ExampleMessageSenderService("microServic", new String[]{"broadcast"});
        messageBus.register(microService);
        ExampleBroadcast broadcast1 = new ExampleBroadcast("microService1");
        messageBus.subscribeBroadcast(broadcast1.getClass(), microService1);
        messageBus.getBroadCastSubscribers().get(broadcast1.getClass());
        List<MicroService> list1 = messageBus.getBroadCastSubscribers().get(broadcast1);
        assertNotNull("list1 must not be null ", list1);
        assertTrue("list1 must contain the microservice ", list1.contains(microService1));
        assertEquals("list1 size must be = 1 ", 1, list1.size());
        messageBus.unregister(microService1);
    }

    @Test
    public void awaitMessageTest() throws InterruptedException {
        //event
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        ExampleMessageSenderService microservice = new ExampleMessageSenderService("microservice", new String[]{"event"});
        messageBus.register(microservice);
        ExampleEvent event = new ExampleEvent("send");
        messageBus.subscribeEvent(event.getClass(), microservice);
        messageBus.sendEvent(event);
        assertEquals("event must be to the awaited message ",event, messageBus.awaitMessage(microservice));
        messageBus.unregister(microservice);
        //broadcast
        ExampleMessageSenderService microservice1 = new ExampleMessageSenderService("microservice1", new String[]{"broadcast"});
        messageBus.register(microservice1);
        ExampleBroadcast broadcast = new ExampleBroadcast("send");
        messageBus.subscribeBroadcast(broadcast.getClass(), microservice1);
        messageBus.sendBroadcast(broadcast);
        assertEquals("broadcast must be to the awaited message ",broadcast, messageBus.awaitMessage(microservice1));
        messageBus.unregister(microservice1);
    }

    @Test
    public void completeTest()
    {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        ExampleEvent event = new ExampleEvent("send");
        MicroService microService = new ExampleEventHandlerService("Handler", new String[]{"1"});
        messageBus.register(microService);
        messageBus.subscribeEvent(event.getClass(), microService);
        Future<String> future = messageBus.sendEvent(event);
        messageBus.complete(event,"Completed");
        assertTrue("the futre must be done",future.isDone());
        assertEquals("the future result must be : Completed","Completed", future.get());
        messageBus.unregister(microService);
    }
    @Test
    public void sendBroadcastTest() throws InterruptedException {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        ExampleBroadcastListenerService microservice = new ExampleBroadcastListenerService("microservice", new String[]{"2"});
        ExampleBroadcastListenerService microservice1 = new ExampleBroadcastListenerService("microservice1", new String[]{"2"});
        ExampleBroadcast broadcast = new ExampleBroadcast("sender");
        messageBus.register(microservice);
        messageBus.register(microservice1);
        messageBus.subscribeBroadcast(broadcast.getClass(), microservice);
        messageBus.subscribeBroadcast(broadcast.getClass(), microservice1);
        messageBus.sendBroadcast(broadcast);
        BlockingQueue<Message> list = messageBus.getMicroServicesQueues().get(microservice);
        BlockingQueue<Message> list1 = messageBus.getMicroServicesQueues().get(microservice1);
        assertTrue("the list must contain the broadcast",list.contains(broadcast));
        assertTrue("the list1 must contain the broadcast",list.contains(broadcast));
        messageBus.unregister(microservice);
        messageBus.unregister(microservice1);
    }
    @Test
    public void sendEventTest() throws InterruptedException
    {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        ExampleEvent event = new ExampleEvent("sender");
        MicroService microService = new ExampleEventHandlerService("microservice", new String[]{"2"});
        messageBus.register(microService);
        messageBus.subscribeEvent(event.getClass(), microService);
        Future<String> future = messageBus.sendEvent(event);
        BlockingQueue<Message> list = messageBus.getMicroServicesQueues().get(microService);
        assertTrue(list.contains(event));
        assertEquals(future, messageBus.getFutures().get(event));
        messageBus.unregister(microService);
    }
    @Test
    public void unRegisterTest()
    {
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        MicroService microservice = new ExampleMessageSenderService("microservice",new String[]{"event"});
        MicroService microservice1 = new ExampleMessageSenderService("microservice1",new String[]{"broadcast"});
        MicroService microservice2 = new ExampleEventHandlerService("microservice2",new String[]{"2"});
        MicroService microservice3 = new ExampleBroadcastListenerService("microservice3",new String[]{"2"});
        messageBus.register(microservice);
        messageBus.register(microservice1);
        messageBus.register(microservice2);
        messageBus.register(microservice3);
        ExampleEvent event = new ExampleEvent("sender");
        ExampleBroadcast broadcast = new ExampleBroadcast("sender");
        messageBus.subscribeEvent(event.getClass(),microservice2);
        messageBus.subscribeBroadcast(broadcast.getClass(),microservice3);
        messageBus.sendEvent(event);
        messageBus.sendBroadcast(broadcast);
        messageBus.unregister(microservice3);
        assertNull(messageBus.getMicroServicesQueues().get(microservice3));
        assertFalse(messageBus.getBroadCastSubscribers().get(broadcast).contains(microservice3));
        assertNotNull(messageBus.getMicroServicesQueues().get(microservice));
        assertNotNull(messageBus.getMicroServicesQueues().get(microservice1));
        assertNotNull(messageBus.getMicroServicesQueues().get(microservice2));
        messageBus.unregister(microservice2);
        assertFalse(messageBus.getEventSubscribers().get(event).contains(microservice2));
        assertNull(messageBus.getMicroServicesQueues().get(microservice2));
        assertNotNull(messageBus.getMicroServicesQueues().get(microservice));
        assertNotNull(messageBus.getMicroServicesQueues().get(microservice1));
        messageBus.unregister(microservice);
        messageBus.unregister(microservice1);








    }
}

