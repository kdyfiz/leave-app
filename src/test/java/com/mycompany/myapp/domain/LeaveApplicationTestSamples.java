package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LeaveApplicationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static LeaveApplication getLeaveApplicationSample1() {
        return new LeaveApplication().id(1L).reason("reason1").rejectionReason("rejectionReason1");
    }

    public static LeaveApplication getLeaveApplicationSample2() {
        return new LeaveApplication().id(2L).reason("reason2").rejectionReason("rejectionReason2");
    }

    public static LeaveApplication getLeaveApplicationRandomSampleGenerator() {
        return new LeaveApplication()
            .id(longCount.incrementAndGet())
            .reason(UUID.randomUUID().toString())
            .rejectionReason(UUID.randomUUID().toString());
    }
}
