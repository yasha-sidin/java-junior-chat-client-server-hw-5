package ru.gb.main.utils.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
@ToString
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "body", length = 500)
    private String body;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;
    @Column(name = "time")
    private Date time;
    @Column(name = "is_for_all")
    private boolean isForAll;

    public Message(String body, User initiator, User receiver) {
        this.body = body;
        this.initiator = initiator;
        this.receiver = receiver;
        this.time = new Date();
    }
}
