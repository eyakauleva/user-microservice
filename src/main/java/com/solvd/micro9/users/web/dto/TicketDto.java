package com.solvd.micro9.users.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TicketDto {

    private String id;
    private String userId;
    private EventDto event;
    private Integer quantity;
    private BigDecimal price;

}
