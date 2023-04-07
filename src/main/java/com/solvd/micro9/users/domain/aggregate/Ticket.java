package com.solvd.micro9.users.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    private String id;
    private String userId;
    private String eventId;
    private Integer quantity;
    private BigDecimal price;

}
