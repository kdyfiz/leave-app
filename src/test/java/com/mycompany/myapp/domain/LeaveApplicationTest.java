package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.LeaveApplicationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LeaveApplicationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LeaveApplication.class);
        LeaveApplication leaveApplication1 = getLeaveApplicationSample1();
        LeaveApplication leaveApplication2 = new LeaveApplication();
        assertThat(leaveApplication1).isNotEqualTo(leaveApplication2);

        leaveApplication2.setId(leaveApplication1.getId());
        assertThat(leaveApplication1).isEqualTo(leaveApplication2);

        leaveApplication2 = getLeaveApplicationSample2();
        assertThat(leaveApplication1).isNotEqualTo(leaveApplication2);
    }
}
