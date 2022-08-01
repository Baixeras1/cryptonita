package com.cryptonita.app.data.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of ={"userID"})
@ToString(exclude = "user")
@Table(name = "BannedUsers")
public class BannedUsersModel {

    @Id
    private long userID;

    private LocalDate bannedAt;

    private LocalDate expiresAt;

    @MapsId
    @OneToOne
    private UserModel user;

    @Builder
    public BannedUsersModel(LocalDate bannedAt, LocalDate expiresAt, UserModel user) {
        this.bannedAt = bannedAt;
        this.expiresAt = expiresAt;
        this.user = user;
    }
}
