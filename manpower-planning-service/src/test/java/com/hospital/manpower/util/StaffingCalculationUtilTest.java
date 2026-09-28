package com.hospital.manpower.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class StaffingCalculationUtilTest {

    @Test
    void calculateRequiredStaff_appliesShiftsAndLeaveBuffer() {
        // 20 beds / ratio 10 = 2 base staff, 24/8 = 3 shifts, +10% leave buffer -> ceil(6.6) = 7
        int required = StaffingCalculationUtil.calculateRequiredStaff(
                20, BigDecimal.valueOf(10), BigDecimal.valueOf(24), BigDecimal.valueOf(8), BigDecimal.valueOf(10));

        assertThat(required).isEqualTo(7);
    }

    @Test
    void calculateRequiredStaff_roundsUpPartialStaff() {
        // 20 beds / ratio 8 = 2.5 base staff, 1 shift, no buffer -> ceil(2.5) = 3
        int required = StaffingCalculationUtil.calculateRequiredStaff(
                20, BigDecimal.valueOf(8), BigDecimal.valueOf(8), BigDecimal.valueOf(8), BigDecimal.ZERO);

        assertThat(required).isEqualTo(3);
    }

    @Test
    void calculateVacancies_returnsZeroWhenFullyStaffed() {
        assertThat(StaffingCalculationUtil.calculateVacancies(5, 6)).isZero();
        assertThat(StaffingCalculationUtil.calculateVacancies(5, 3)).isEqualTo(2);
    }

    @Test
    void calculateExcess_returnsZeroWhenUnderstaffed() {
        assertThat(StaffingCalculationUtil.calculateExcess(5, 3)).isZero();
        assertThat(StaffingCalculationUtil.calculateExcess(5, 6)).isEqualTo(1);
    }

    @Test
    void calculateMonthlyBudget_multipliesRequiredStaffBySalary() {
        BigDecimal budget = StaffingCalculationUtil.calculateMonthlyBudget(3, BigDecimal.valueOf(150000));

        assertThat(budget).isEqualByComparingTo(BigDecimal.valueOf(450000));
    }
}
