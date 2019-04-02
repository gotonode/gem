package io.github.gotonode.gem.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Link implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "uri")
    private String uri;

    @Override
    public String toString() {
        return "Link{" +
                "id=" + id +
                ", uri=" + uri +
                '}';
    }
}
