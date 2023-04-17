package com.solvd.micro9.users.domain.command;

import com.solvd.micro9.users.domain.es.EsStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompleteTransactionCommand {

    private String userId;
    private EsStatus status;

}
