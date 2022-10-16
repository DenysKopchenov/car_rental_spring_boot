package com.dkop.car.rental.model.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OrderDetails {

    @Id
    @GeneratedValue()
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    @OneToOne(cascade = CascadeType.PERSIST)
    private PassportData passportData;
    private boolean withDriver;
    private LocalDate startDate;
    private LocalDate endDate;
    private long rentalPrice;
    private OrderStatus orderStatus;
    private String rejectOrderDetails;
    @OneToOne(cascade = CascadeType.PERSIST)
    private RepairPayment repairPayment;
}
