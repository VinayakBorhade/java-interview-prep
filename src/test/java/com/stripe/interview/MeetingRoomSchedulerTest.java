package com.stripe.interview;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class MeetingRoomSchedulerTest {

    static MeetingRoomScheduler scheduler = new MeetingRoomScheduler();
    Room r1 = new Room();

    User u1 = new User();


    @BeforeClass
    public static void setupOnce() {
        scheduler.meetingLedger = new ArrayList<>();
        scheduler.roomToBooking = new TreeMap<>();
    }

    private void setupRooms() {
        r1.roomId = "1";
        r1.capacity = 1;
        r1.amenities = List.of(Amenity.MICROPHONE);
    }

    private void setupUsers() {
        u1.userId = "u1";
        u1.name = "u1";
    }

    @Before
    public void setup() {
        setupRooms();
        setupUsers();
        scheduler.rooms = new ArrayList<>();
        scheduler.rooms.add(r1);
        scheduler.roomToBooking.put(r1.roomId, new TreeMap<>());
    }

    @Test
    public void happyCase() {
        assertEquals(r1.roomId, scheduler.schedule(List.of(u1), 1, 2, List.of(Amenity.MICROPHONE), new BestFit()).room.roomId);
    }

    @Test
    public void test_RoomNotAvailable() {
        assertEquals(r1.roomId, scheduler.schedule(List.of(u1), 1, 2, List.of(Amenity.MICROPHONE), new BestFit()).room.roomId);
        assertThrows(NoSuchElementException.class, () -> scheduler.schedule(List.of(u1), 1, 2, List.of(Amenity.MICROPHONE), new BestFit()));
    }

}
