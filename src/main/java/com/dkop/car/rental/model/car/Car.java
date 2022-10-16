package com.dkop.car.rental.model.car;

import com.dkop.car.rental.model.order.RentOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
    private Manufacturer manufacturer;
    private CategoryClass categoryClass;
    private Transmission transmission;
    private Fuel fuel;
    private String model;
    private long pricePerDay;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "car",
            fetch = FetchType.LAZY)
    private Set<RentOrder> rentOrders;
    @NotNull
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] image;
}
