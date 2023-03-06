package com.solvd.micro9.users.web.controller.openfeign;

import com.solvd.micro9.users.web.controller.UserController;
import com.solvd.micro9.users.web.dto.TicketDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = UserController.TICKET_SERVICE)
public interface TicketClient {

    @PostMapping(value = "/api/v1/tickets")
    TicketDto create(@RequestBody @Validated TicketDto ticketDto);

}
