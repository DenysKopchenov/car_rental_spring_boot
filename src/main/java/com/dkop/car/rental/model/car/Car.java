package com.dkop.car.rental.model.car;

import com.dkop.car.rental.model.order.RentOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Car {

    @Id
    @GeneratedValue()
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    private Manufacturer manufacturer;
    private CategoryClass categoryClass;
    private String model;
    private long pricePerDay;
    @OneToMany(mappedBy = "car",
            fetch = FetchType.LAZY)
    private Set<RentOrder> rentOrders;
    private byte[] image;
}
