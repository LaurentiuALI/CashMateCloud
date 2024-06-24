package com.example.account.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CashUserDTO {
    private long id;
    private String name;
    private String password;
}
