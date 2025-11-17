package com.stripe.interview.triage;


import java.util.*;

/**
 *
 * Strategy (base class)
 *  - findRoom(rooms: Room[], schedules: Schedule[]) : Room
 *  -> BestFit
 *  -> WorstFit
 *  -> FirstFit
 *
 * User
 *
 * Room
 *  - roomId
 *  - amenities: Amenity[]
 *
 * AMENITY {CAMERA , MICROPHONE, AUDIO, RESTRICTED}
 *
 * Meeting
 *  - fromTime: ts
 *  - toTime: ts
 *  - room: Room
 *  - users: User[]
 *
 * Scheduler
 *  - rooms: Room[]
 *  - meetings: Meeting[]
 *
 *  - schedule(users, fromTime, toTime, amenities, allocationStrategy: Strategy) : scheduleId
 *  - reschedule(scheduleId, fromTime, toTime, amenities) : {success / failure}
 *  - cancel(scheduleId) : {success / failure}
 *
 *
 */

class Room {
    TreeMap<Integer, Integer> schedules = new TreeMap<>();
    Integer capacity;
    String roomId;
}

interface AllocationStrategy {
    Optional<Room> findRoom(List<Room> rooms, Integer startTime, Integer endTime);
}

class BestFit implements AllocationStrategy {
    @Override
    public Optional<Room> findRoom(List<Room> rooms, Integer startTime, Integer endTime) {
        return Optional.ofNullable(rooms.stream()
                .filter(room -> {
                    Map.Entry<Integer, Integer> prev = room.schedules.floorEntry(startTime);
                    Map.Entry<Integer, Integer> next = room.schedules.ceilingEntry(endTime);
                    boolean isEligible = prev == null || prev.getValue() <= startTime;
                    isEligible = isEligible && (next == null || endTime <= next.getKey());
                    return isEligible;
                })
                .sorted(Comparator.comparingInt(x -> x.capacity))
                .findFirst()
                .orElseThrow(NoSuchElementException::new));
    }
}

class User {
    String userId, name;
    public void notify(String meetingId) {
        System.out.printf("User = %s is notified of meeting id = %s.\n", userId, meetingId);
    }
}

class Meeting {
    String meetingId;
    Integer from, to;
    List<User> users;
    Room room;

    public void notifySubs() {
        this.users.forEach(user -> user.notify(this.meetingId));
    }
}


// 8 + 5
public class MeetingRoomScheduler2 {

    private static MeetingRoomScheduler2 meetingRoomScheduler2 = new MeetingRoomScheduler2();

    List<Room> rooms;
    List<Meeting> ledger;

    private MeetingRoomScheduler2() {}

    public static MeetingRoomScheduler2 getInstance() {
        return meetingRoomScheduler2;
    }

    public Meeting schedule(Integer startTime, Integer endTime, List<User> users, AllocationStrategy strategy) {
        // filter rooms according to some strategy
        Room r = strategy.findRoom(rooms, startTime, endTime).get();
        // book the room
        r.schedules.put(startTime, endTime);
        // create a meeting object and write it in ledger
        Meeting m = new Meeting();
        m.from = startTime;
        m.to = endTime;
        m.meetingId = UUID.randomUUID().toString();
        m.users = users;
        m.room = r;
        ledger.add(m);

        // notify the users
        m.notifySubs();

        return m;
    }
}
