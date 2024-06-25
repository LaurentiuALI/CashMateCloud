package com.example.account.model;

import com.example.account.dtos.CashUserDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="user_account")
public class UserAccount {

    @EmbeddedId
    private UserAccountId id;

    @ManyToOne
    @MapsId("account_id")
    @JoinColumn(name="account_id")
    private Account account;

}
