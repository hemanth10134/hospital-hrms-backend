package com.hospital.manpower.entity;

import com.hospital.manpower.util.DateTimeUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Designation-wise staffing line item (staffing ratio, salary, leave buffer, current staff)
 * that belongs to a {@link ManpowerPlan}. Required staff, vacancies and budget are derived
 * values computed by the service layer, not persisted here.
 */
@Entity
@Table(name = "manpower_plan_designations")
@Getter
@Setter
@NoArgsConstructor
public class ManpowerPlanDesignation {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private ManpowerPlan plan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "designation_id", nullable = false)
    private Designation designation;

    @Column(name = "staffing_ratio", nullable = false)
    private BigDecimal staffingRatio;

    @Column(name = "monthly_salary", nullable = false)
    private BigDecimal monthlySalary;

    @Column(name = "leave_buffer_pct", nullable = false)
    private BigDecimal leaveBufferPct;

    @Column(name = "current_staff", nullable = false)
    private Integer currentStaff;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = DateTimeUtil.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = DateTimeUtil.now();
    }
}
