package com.cryptonita.app.data.entities;

import lombok.*;
import org.h2.engine.User;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(of = {"id","user"})
@Table(name = "HYSTORI")
public class HistoryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    @JoinColumn(name = "USER_ID",nullable = false)
    private UserModel user;
    private LocalDate date;
    private String origin;
    private String destiny;
    private int quantity;

    @Builder
    public HistoryModel(UserModel user,LocalDate date,String origin,String destiny,int quantity) {
        this.user = user;
        this.date = date;
        this.origin = origin;
        this.destiny = destiny;
        this.quantity = quantity;
    }



}