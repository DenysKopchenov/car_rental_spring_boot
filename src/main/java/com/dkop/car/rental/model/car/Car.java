package com.dkop.car.rental.model.car;

import com.dkop.car.rental.model.order.RentOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
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
    @Enumerated(EnumType.STRING)
    private Manufacturer manufacturer;
    @Enumerated(EnumType.STRING)
    private CategoryClass categoryClass;
    private Transmission transmission;
    private Fuel fuel;
    private String model;
    private long pricePerDay;
    private boolean isAvailable;
    @OneToMany(mappedBy = "car",
            fetch = FetchType.LAZY)
    private Set<RentOrder> rentOrders;
    @NotNull
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] image;
}
