package com.stripe.interview;


import java.util.*;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;

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


enum Amenity {MICROPHONE, CAMERA, RESTRICTED}

class Room {
    String roomId;
    List<Amenity> amenities;
    Integer capacity;
}

class User {
    String userId, name;

    public void notify(Meeting meeting) {
        System.out.printf("User userid - {} notified for meeting id - {} \n", userId, meeting.meetingId);
    }
}

enum MeetingStatus {SCHEDULED, CANCELLED}

class Meeting {
    String meetingId;
    MeetingStatus status;
    Integer from, to;
    Room room;
    User[] users;
}

interface AllocationStrategy {

    default List<Room> filterRooms(List<Room> rooms, TreeMap<String, TreeMap<Integer, Integer>> roomToBooking,
                                   Integer startTime, Integer endTime, List<Amenity> amenities, Integer capacity) {
        List<Room> filteredRooms = rooms.stream()
                .filter(room -> amenities.containsAll(room.amenities) && room.capacity <= capacity)
                .collect(Collectors.toList());
        return filteredRooms.stream().filter(room -> {
            TreeMap<Integer, Integer> booking = roomToBooking.get(room.roomId);
            Map.Entry<Integer, Integer> prev = booking.floorEntry(startTime);
            Map.Entry<Integer, Integer> next = booking.ceilingEntry(startTime);
            return (prev == null || prev.getValue() <= startTime) && (next == null || endTime >= next.getKey());
        }).collect(Collectors.toList());
    }

    Room allocate(List<Room> rooms, TreeMap<String, TreeMap<Integer, Integer>> roomToBooking,
                    Integer startTime, Integer endTime, List<Amenity> amenities, Integer capacity);
}

class BestFit implements AllocationStrategy {
    @Override
    public Room allocate(List<Room> rooms, TreeMap<String, TreeMap<Integer, Integer>> roomToBooking,
                         Integer startTime, Integer endTime, List<Amenity> amenities, Integer capacity) {
        return filterRooms(rooms, roomToBooking, startTime, endTime, amenities, capacity)
                .stream()
                .min(Comparator.comparingInt(r -> r.capacity))
                .orElseThrow(NoSuchElementException::new);
    }
}


class WorstFit implements AllocationStrategy {
    @Override
    public Room allocate(List<Room> rooms, TreeMap<String, TreeMap<Integer, Integer>> roomToBooking,
                           Integer startTime, Integer endTime, List<Amenity> amenities, Integer capacity) {
        return filterRooms(rooms, roomToBooking, startTime, endTime, amenities, capacity)
                .stream()
                .max(Comparator.comparingInt(r -> r.capacity))
                .orElseThrow(NoSuchElementException::new);
    }
}

class FirstFit implements AllocationStrategy {
    @Override
    public Room allocate(List<Room> rooms, TreeMap<String, TreeMap<Integer, Integer>> roomToBooking,
                           Integer startTime, Integer endTime, List<Amenity> amenities, Integer capacity) {
        return filterRooms(rooms, roomToBooking, startTime, endTime, amenities, capacity)
                .stream()
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }
}


public class MeetingRoomScheduler {
    List<Room> rooms;
    TreeMap<String, TreeMap<Integer, Integer>> roomToBooking;
    List<Meeting> meetingLedger;

    /**
     *
     * @param users
     * @param startTime
     * @param endTime
     * @param amenities
     * @param allocationStrategy
     * @return - room no
     */
    public Meeting schedule(List<User> users, Integer startTime, Integer endTime, List<Amenity> amenities,
                           AllocationStrategy allocationStrategy) {
        // load all rooms and filter amenities and timings from their schedules
        // strategy to return the list of favourable room
//        List<Room> filteredRooms = rooms.stream().filter(room -> amenities.containsAll(room.amenities))
//                .collect(Collectors.toList());
//        filteredRooms.stream().filter(room -> {
//            TreeMap<Integer, Integer> booking = roomToBooking.getOrDefault(room.roomId, new TreeMap<>());
//            Map.Entry<Integer, Integer> prev = booking.floorEntry(startTime);
//            Map.Entry<Integer, Integer> next = booking.ceilingEntry(startTime);
//            return (prev == null || prev.getValue() <= startTime) && (next == null || endTime >= next.getKey());
//        });
        Room ans = allocationStrategy.allocate(rooms, roomToBooking, startTime, endTime, amenities, users.size());
        Meeting meeting = new Meeting();
        meeting.meetingId = randomUUID().toString();
        meeting.from = startTime;
        meeting.to = endTime;
        meeting.room = ans;
        meeting.status = MeetingStatus.SCHEDULED;

        // put it in a write-ahead-log file
        meetingLedger.add(meeting);

        users.forEach(user -> user.notify(meeting));
        return meeting;
    }
}
