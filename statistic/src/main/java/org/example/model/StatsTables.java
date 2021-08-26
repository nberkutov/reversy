package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;


@SqlResultSetMapping(
        name = "StatsTables",
        entities = @EntityResult(
                entityClass = StatsTables.class
        ))
@Table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsTables implements Comparable<StatsTables> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "relation")
    private String table;
    @Column(name = "pretty_total_size")
    private String totalSize;
    @Column(name = "bytes_total_size")
    private BigInteger bytes;

    @Override
    public String toString() {
        return String.format("%s [%s]", table, totalSize);
    }

    @Override
    public int compareTo(final StatsTables o) {
        return bytes.compareTo(o.getBytes());
    }
}

