package com.hospital.manpower.entity;

import com.hospital.manpower.enums.PositionRequestStatus;
import com.hospital.manpower.util.DateTimeUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "position_requests")
@Getter
@Setter
@NoArgsConstructor
public class PositionRequest {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private ManpowerPlan plan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "designation_id", nullable = false)
    private Designation designation;

    @Column(name = "requested_positions", nullable = false)
    private Integer requestedPositions;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "additional_monthly_budget", nullable = false)
    private BigDecimal additionalMonthlyBudget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PositionRequestStatus status = PositionRequestStatus.PENDING_APPROVAL;

    @Column(name = "requested_by", nullable = false, length = 150)
    private String requestedBy;

    @Column(name = "reviewed_by", length = 150)
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

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
