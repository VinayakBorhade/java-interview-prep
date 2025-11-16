package com.stripe.interview;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class MeetingScheduler2Test {

    private static List<Room> initRooms() {
        Room r = new Room();
        r.schedules = new TreeMap<>();
        r.capacity = 1;
        r.roomId = "r1";
        return List.of(r);
    }


    @Test
    public void test_happyCase() {
        MeetingRoomScheduler2 underTest = MeetingRoomScheduler2.getInstance();
        underTest.rooms = initRooms();
        underTest.ledger = new ArrayList<>();

        User user = new User();
        user.name = "u1";
        user.userId = "u1";

        assertEquals(initRooms().get(0).roomId, underTest.schedule(1, 2, List.of(user), new BestFit()).room.roomId);
        assertThrows(NoSuchElementException.class, () -> underTest.schedule(1, 2, List.of(user), new BestFit()));
    }
}
