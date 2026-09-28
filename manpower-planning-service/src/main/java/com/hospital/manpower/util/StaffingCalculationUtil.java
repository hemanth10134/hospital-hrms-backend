package com.hospital.manpower.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Derives required staff, vacancies/excess and monthly budget for a designation-wise
 * staffing line item. These values are never persisted; they are recomputed on every
 * read so that editing beds, ratio or leave buffer always keeps the plan consistent.
 * <p>
 * Required staff formula (standard round-the-clock hospital staffing calculation):
 * <pre>
 *   shiftsPerDay   = departmentOperatingHours / employeeWorkingHours
 *   baseStaff      = numberOfBeds / staffingRatio (beds per staff)
 *   requiredStaff  = ceil(baseStaff * shiftsPerDay * (1 + leaveBufferPct / 100))
 * </pre>
 */
public final class StaffingCalculationUtil {

    private StaffingCalculationUtil() {
    }

    public static int calculateRequiredStaff(int numberOfBeds,
                                              BigDecimal staffingRatio,
                                              BigDecimal departmentOperatingHours,
                                              BigDecimal employeeWorkingHours,
                                              BigDecimal leaveBufferPct) {
        BigDecimal shiftsPerDay = departmentOperatingHours.divide(employeeWorkingHours, 6, RoundingMode.HALF_UP);
        BigDecimal baseStaff = BigDecimal.valueOf(numberOfBeds).divide(staffingRatio, 6, RoundingMode.HALF_UP);
        BigDecimal bufferMultiplier = BigDecimal.ONE.add(leaveBufferPct.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));

        BigDecimal requiredStaff = baseStaff.multiply(shiftsPerDay).multiply(bufferMultiplier);
        return requiredStaff.setScale(0, RoundingMode.CEILING).intValue();
    }

    public static int calculateVacancies(int requiredStaff, int currentStaff) {
        return Math.max(requiredStaff - currentStaff, 0);
    }

    public static int calculateExcess(int requiredStaff, int currentStaff) {
        return Math.max(currentStaff - requiredStaff, 0);
    }

    public static BigDecimal calculateMonthlyBudget(int requiredStaff, BigDecimal monthlySalary) {
        return monthlySalary.multiply(BigDecimal.valueOf(requiredStaff));
    }
}
